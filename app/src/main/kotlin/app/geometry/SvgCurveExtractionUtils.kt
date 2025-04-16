package app.geometry

import app.PatternSvg.mmToPtFactor
import app.SvgViewBox
import app.asList
import app.asSVGPathSegCurvetoCubicAbs
import app.asSVGPathSegLinetoAbs
import app.asSVGPathSegMovetoAbs
import app.childElements
import app.color
import app.createPathElement
import app.createRectElement
import app.createSvgDocument
import app.documentFactory
import app.documentSvgElement
import app.fill
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.curves.toDebugSvgPathGroup
import app.geometry.splines.ClosedSpline
import app.geometry.splines.OpenSpline
import app.geometry.splines.Spline
import app.geometry.splines.toDebugSvgPathGroup
import app.height
import app.mapCarrying
import app.p
import app.p1
import app.p2
import app.stroke
import app.svgDomImplementation
import app.uncons
import app.untrail
import app.viewBox
import app.width
import org.apache.batik.anim.dom.SVGOMDocument
import org.apache.batik.css.engine.value.svg12.LineHeightValue
import org.w3c.dom.svg.SVGColor
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import java.awt.Color
import java.io.Reader

object SvgCurveExtractionUtils {
    sealed class ExtractedPath {
        companion object {
            val red = Color(0xCC0000)
            val blue = Color(0x0000FF)
        }

        abstract val color: Color
    }

    data class ExtractedOpenSpline(
        override val color: Color,
        val openSpline: OpenSpline<*, *, *>,
    ) : ExtractedPath()

    data class ExtractedCurveSet(
        val extractedPaths: Set<ExtractedPath>,
    ) {
        fun getCurveByColor(
            color: Color,
        ): ExtractedPath = extractedPaths.single {
            it.color == color
        }
    }

    fun extractCurves(
        clazz: Class<*>,
        resourceName: String,
    ): ExtractedCurveSet {
        val inputStream = clazz.getResourceAsStream(resourceName) ?: throw IllegalArgumentException(
            "Resource not found: $resourceName"
        )

        return extractCurves(
            fileReader = inputStream.reader(),
        )
    }

    fun extractCurves(
        fileReader: Reader,
    ): ExtractedCurveSet {
        val uri = "file://Curves.svg"

        val document = documentFactory.createDocument(uri, fileReader) as SVGOMDocument
        document.cssEngine = svgDomImplementation.createCSSEngine(document, MinimalCssContext())

        return extractCurves(
            document = document,
        )
    }

    fun extractCurves(
        document: SVGDocument,
    ): ExtractedCurveSet {
        val svgElement = document.documentSvgElement

        val extractedPaths = svgElement.childElements.map { child ->
            when (child) {
                is SVGPathElement -> extractOpenSpline(pathElement = child)

                else -> throw UnsupportedOperationException("Unsupported child element: $child")
            }
        }

        return ExtractedCurveSet(
            extractedPaths = extractedPaths.toSet(),
        )
    }

    private fun extractOpenSpline(
        pathElement: SVGPathElement,
    ): ExtractedOpenSpline {
        val (firstPathSeg, trailingPathSegs) = pathElement.pathSegList.asList().uncons()
            ?: throw IllegalArgumentException(
                "Path element has no segments: $pathElement"
            )

        val firstPathSegMoveTo = firstPathSeg.asSVGPathSegMovetoAbs ?: throw IllegalArgumentException(
            "First path segment is not a MoveTo: $firstPathSeg"
        )

        val (innerPathSegs, lastPathSeg) = trailingPathSegs.untrail() ?: throw IllegalArgumentException(
            "Path element has no effective segments: $pathElement"
        )

        val (leadingLinks, finalInnerTrailingPoint) = innerPathSegs.mapCarrying(
            initialCarry = firstPathSegMoveTo.p,
        ) { prevTrailingPoint, pathSeg ->
            val (curveEdge, trailingPoint) = extractCurveEdgeAndTrailingPoint(
                pathSeg = pathSeg,
            )

            val edge = Spline.Edge<SegmentCurve<*>, Nothing?>(
                curveEdge = curveEdge,
                metadata = null,
            )

            Pair(
                Spline.PartialLink(
                    startKnot = Spline.Knot(
                        point = prevTrailingPoint,
                        metadata = null,
                    ),
                    edge = edge,
                ),
                trailingPoint,
            )
        }

        val (lastCurveEdge, lastPoint) = extractCurveEdgeAndTrailingPoint(
            pathSeg = lastPathSeg,
        )

        val lastLink = Spline.CompleteLink(
            startKnot = Spline.Knot(
                point = finalInnerTrailingPoint,
                metadata = null,
            ),
            edge = Spline.Edge<SegmentCurve<*>, Nothing?>(
                curveEdge = lastCurveEdge,
                metadata = null,
            ),
            endKnot = Spline.Knot(
                point = lastPoint,
                metadata = null,
            ),
        )

        val openSpline = OpenSpline.of(
            leadingLinks = leadingLinks,
            lastLink = lastLink,
        )

        val svgColor = pathElement.style.getPropertyCSSValue("stroke") as SVGColor
        val color = svgColor.rgbColor.color

        return ExtractedOpenSpline(
            color = color,
            openSpline = openSpline,
        )
    }

    private fun extractCurveEdgeAndTrailingPoint(
        pathSeg: SVGPathSeg,
    ): Pair<SegmentCurve.Edge<*>, Point> {
        val lineSeg = pathSeg.asSVGPathSegLinetoAbs
        if (lineSeg != null) return Pair(
            LineSegment.Edge,
            lineSeg.p,
        )

        val cubicSeg = pathSeg.asSVGPathSegCurvetoCubicAbs
        if (cubicSeg != null) return Pair(
            CubicBezierCurve.Edge(
                control0 = cubicSeg.p1,
                control1 = cubicSeg.p2,
            ),
            cubicSeg.p,
        )

        throw UnsupportedOperationException("Unsupported path segment type: ${pathSeg.pathSegType}")
    }

    fun dumpSpline(
        spline: ClosedSpline<*, *, *>,
    ): SVGDocument {
        val boundingBox = spline.findBoundingBox()

        val computedWidth = boundingBox.xMax
        val computedHeight = boundingBox.yMax

        return createSvgDocument().apply {
            documentSvgElement.apply {
                viewBox = SvgViewBox(
                    xMin = 0.0,
                    yMin = 0.0,
                    width = computedWidth,
                    height = computedHeight,
                )
                this.width = "${computedWidth}px"
                this.height = "${computedHeight}px"
            }

            documentSvgElement.appendChild(
                spline.toDebugSvgPathGroup(document = this)
            )

            documentSvgElement.appendChild(
                this.createRectElement().apply {
                    this.width.baseVal.value = computedWidth.toFloat()
                    this.height.baseVal.value = computedHeight.toFloat()
                    fill = "none"
                    stroke = "black"
                },
            )
        }
    }

    fun dumpSplines(
        splines: List<ClosedSpline<*, *, *>>,
    ): SVGDocument {
        val boundingBox = BoundingBox.unionAll(
            splines.map { it.findBoundingBox() },
        )

        val computedWidth = boundingBox.xMax
        val computedHeight = boundingBox.yMax

        return createSvgDocument().apply {
            documentSvgElement.apply {
                viewBox = SvgViewBox(
                    xMin = 0.0,
                    yMin = 0.0,
                    width = computedWidth,
                    height = computedHeight,
                )
                this.width = "${computedWidth}px"
                this.height = "${computedHeight}px"
            }

            splines.forEach { spline ->
                documentSvgElement.appendChild(
                    spline.toDebugSvgPathGroup(document = this)
                )
            }

            documentSvgElement.appendChild(
                this.createRectElement().apply {
                    this.width.baseVal.value = computedWidth.toFloat()
                    this.height.baseVal.value = computedHeight.toFloat()
                    fill = "none"
                    stroke = "black"
                },
            )
        }
    }

    fun dumpCurve(
        bezierCurve: CubicBezierCurve,
    ): SVGDocument {
        val boundingBox = bezierCurve.findBoundingBox()

        return createSvgDocument().apply {
            documentSvgElement.apply {
                viewBox = SvgViewBox(
                    xMin = boundingBox.xMin,
                    yMin = boundingBox.yMin,
                    width = boundingBox.width,
                    height = boundingBox.height,
                )
                width = "${boundingBox.width}"
                height = "${boundingBox.height}"
            }

            documentSvgElement.appendChild(
                bezierCurve.toDebugSvgPathGroup(document = this)
            )

            documentSvgElement.appendChild(
                this.createRectElement().apply {
                    width.baseVal.value = boundingBox.xMax.toFloat()
                    height.baseVal.value = boundingBox.yMax.toFloat()
                    fill = "none"
                    stroke = "black"
                },
            )
        }
    }
}
