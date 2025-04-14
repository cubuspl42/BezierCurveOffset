package app.geometry

import app.asList
import app.asSVGPathSegCurvetoCubicAbs
import app.asSVGPathSegLinetoAbs
import app.asSVGPathSegMovetoAbs
import app.childElements
import app.color
import app.documentFactory
import app.documentSvgElement
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.splines.OpenSpline
import app.geometry.splines.Spline
import app.mapCarrying
import app.p
import app.p1
import app.p2
import app.svgDomImplementation
import app.uncons
import app.untrail
import org.apache.batik.anim.dom.SVGOMDocument
import org.w3c.dom.svg.SVGColor
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import java.awt.Color
import java.io.Reader

object SvgCurveExtractionUtils {
    sealed class ExtractedPath {
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
}
