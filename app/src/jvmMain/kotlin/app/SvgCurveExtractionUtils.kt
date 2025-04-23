package app

import app.geometry.BoundingBox
import app.geometry.Point
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.curves.toSpline
import app.geometry.documentFactory
import app.geometry.splines.ClosedSpline
import app.geometry.splines.OpenSpline
import app.geometry.splines.Spline
import app.geometry.svgDomImplementation
import app.geometry.toDebugSvgPathGroup
import app.utils.iterable.mapCarrying
import app.utils.iterable.uncons
import app.utils.iterable.untrail
import org.apache.batik.anim.dom.SVGOMDocument
import org.w3c.dom.svg.SVGCircleElement
import org.w3c.dom.svg.SVGColor
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import java.awt.Color
import java.io.Reader

object SvgCurveExtractionUtils {
    sealed class ExtractedShape {
        companion object {
            val red = Color(0xCC0000)
            val blue = Color(0x0000FF)
        }

        abstract val color: Color
    }

    data class ExtractedCircle(
        override val color: Color,
        val center: Point,
    ) : ExtractedShape()

    sealed class ExtractedPath : ExtractedShape() {
        companion object {
            fun lineSegment(
                color: Color,
                lineSegment: LineSegment,
            ): ExtractedPath = ExtractedOpenSpline(
                color = color,
                lineSegment.toSpline(
                    edgeMetadata = null,
                ),
            )
        }
    }

    data class ExtractedOpenSpline(
        override val color: Color,
        val openSpline: OpenSpline<*, *, *>,
    ) : ExtractedPath() {
        fun singleBezierCurve(): CubicBezierCurve = openSpline.subCurves.single() as CubicBezierCurve
    }

    data class ExtractedCurveSet(
        val extractedShapes: Set<ExtractedShape>,
    ) {
        fun getShapeByColor(
            color: Color,
        ): ExtractedShape = extractedShapes.single {
            it.color == color
        }

        fun getOpenSplineByColor(
            color: Color,
        ): ExtractedOpenSpline = getShapeByColor(
            color = color,
        ) as ExtractedOpenSpline

        fun getBezierCurveByColor(
            color: Color,
        ): CubicBezierCurve = getOpenSplineByColor(
            color = color,
        ).singleBezierCurve()

        fun dump(): SVGDocument {
            val extractedOpenSpline = extractedShapes.filterIsInstance<ExtractedOpenSpline>()

            val boundingBox = BoundingBox.unionAll(
                extractedOpenSpline.map { it.openSpline.findBoundingBox() },
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

                extractedOpenSpline.forEach {
                    val spline = it.openSpline

                    documentSvgElement.appendChild(
                        spline.toDebugSvgPathGroup(document = this).apply {
                            fill = "none"
                            stroke = it.color.toHex()
                        }
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

                is SVGCircleElement -> extractCircle(circleElement = child)

                else -> throw UnsupportedOperationException("Unsupported child element: $child")
            }
        }

        return ExtractedCurveSet(
            extractedShapes = extractedPaths.toSet(),
        )
    }

    private fun extractCircle(
        circleElement: SVGCircleElement,
    ): ExtractedCircle {
        val svgColor = circleElement.style.getPropertyCSSValue("fill") as? SVGColor
        val color = svgColor?.rgbColor?.color ?: Color.BLACK

        return ExtractedCircle(
            color = color,
            center = Point.of(
                circleElement.cx.baseVal.value.toDouble(),
                circleElement.cy.baseVal.value.toDouble(),
            ),
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

fun Color.toHex(): String {
    val red = this.red
    val green = this.green
    val blue = this.blue
    return String.format("#%02x%02x%02x", red, green, blue)
}
