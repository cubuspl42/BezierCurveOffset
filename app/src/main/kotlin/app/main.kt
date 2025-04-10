package app

import app.geometry.Point
import app.geometry.curves.LineSegment
import app.geometry.transformations.TotalTransformation
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.curves.SegmentCurve
import app.geometry.splines.*
import app.geometry.transformations.transformation
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.w3c.dom.Element
import org.w3c.dom.svg.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.reader

val documentFactory: SAXSVGDocumentFactory = SAXSVGDocumentFactory(null)

sealed class PathSeg {
    companion object {
        fun fromSvgPathSeg(
            pathSeg: SVGPathSeg,
        ): PathSeg {
            when (pathSeg.pathSegType) {
                SVGPathSeg.PATHSEG_MOVETO_ABS -> {
                    val pathSegMovetoAbs = pathSeg as SVGPathSegMovetoAbs

                    return MoveTo(
                        finalPoint = Point.of(
                            px = pathSegMovetoAbs.x.toDouble(),
                            py = pathSegMovetoAbs.y.toDouble(),
                        )
                    )
                }

                SVGPathSeg.PATHSEG_LINETO_ABS -> {
                    val pathSegLinetoAbs = pathSeg as SVGPathSegLinetoAbs

                    return LineTo(
                        endPoint = Point.of(
                            px = pathSegLinetoAbs.x.toDouble(),
                            py = pathSegLinetoAbs.y.toDouble(),
                        )
                    )
                }

                SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS -> {
                    val pathSegCubicToAbs = pathSeg as SVGPathSegCurvetoCubicAbs

                    return CubicTo(
                        firstControl = Point.of(
                            px = pathSegCubicToAbs.x1.toDouble(),
                            py = pathSegCubicToAbs.y1.toDouble(),
                        ),
                        secondControl = Point.of(
                            px = pathSegCubicToAbs.x2.toDouble(),
                            py = pathSegCubicToAbs.y2.toDouble(),
                        ),
                        endPoint = Point.of(
                            px = pathSegCubicToAbs.x.toDouble(),
                            py = pathSegCubicToAbs.y.toDouble(),
                        ),
                    )
                }

                else -> throw UnsupportedOperationException("Unsupported path segment type: ${pathSeg.pathSegType} (${pathSeg.pathSegTypeAsLetter})")
            }
        }
    }

    sealed class CurveTo : PathSeg() {
        abstract fun toEdge(startKnot: Point): SegmentCurve.Edge<SegmentCurve<*>>

        final override val finalPoint: Point
            get() = endPoint

        abstract val endPoint: Point
    }

    data class MoveTo(
        override val finalPoint: Point,
    ) : PathSeg()

    data class LineTo(
        override val endPoint: Point,
    ) : CurveTo() {
        override fun toEdge(startKnot: Point): SegmentCurve.Edge<SegmentCurve<*>> = LineSegment.Edge
    }

    data class CubicTo(
        val firstControl: Point,
        val secondControl: Point,
        override val endPoint: Point,
    ) : CurveTo() {
        override fun toEdge(
            startKnot: Point,
        ): SegmentCurve.Edge<SegmentCurve<*>> {
//            require(startKnot.distanceTo(firstControl) > 0.001)

            return CubicBezierCurve.Edge(
                control0 = firstControl,
                control1 = secondControl,
            )
        }
    }

    abstract val finalPoint: Point
}

fun SVGPathElement.toClosedSpline(): ClosedSpline<*, *> {
    val svgPathSegs = pathSegList.asList()

    require(svgPathSegs.last().pathSegType == SVGPathSeg.PATHSEG_CLOSEPATH)

    val pathSegs = svgPathSegs.dropLast(1).map {
        PathSeg.fromSvgPathSeg(it)
    }

    val (firstPathSeg, tailPathSegs) = pathSegs.uncons()!!

    val originPathSeg = firstPathSeg as PathSeg.MoveTo
    val edgePathSegs = tailPathSegs.elementWiseAs<PathSeg.CurveTo>()

    val segments = edgePathSegs.withPrevious(
        outerLeft = originPathSeg,
    ).map { (prevPathSeg, pathSeg) ->
        val startKnot = prevPathSeg.finalPoint
        Spline.Segment(
            startKnot = startKnot,
            edge = pathSeg.toEdge(startKnot),
            edgeMetadata = null,
        )
    }

    return ClosedSpline(
        segments = segments,
    )
}

fun extractChild(
    transformation: TotalTransformation,
    element: Element,
): ClosedSpline<*, *> = when (val singleChild = element.childElements.single()) {
    is SVGPathElement -> singleChild.toClosedSpline().transformVia(
        transformation = transformation
    )

    is SVGGElement -> {
        val newTransformation = singleChild.transformation.applyOver(base = transformation)

        extractChild(
            transformation = newTransformation, element = singleChild
        )
    }

    else -> throw UnsupportedOperationException("Unsupported child element: $singleChild")
}

fun extractSplineFromFile(
    filePath: Path,
): ClosedSpline<*, *> {
    val reader = filePath.reader()
    val uri = "file://Bezier.svg"

    val document = documentFactory.createDocument(uri, reader) as SVGDocument
    val svgElement = document.documentElement as SVGElement
    val pathElement = extractChild(
        transformation = TotalTransformation.identity, element = svgElement
    )

    return pathElement
}

sealed interface SeamAllowanceKind {
    data object Small : SeamAllowanceKind {
        override val widthMm = 6.0
    }

    data object Large : SeamAllowanceKind {
        override val widthMm = 12.0
    }

    val widthMm: Double
}

val spline = ClosedSpline(
    segments = listOf(
        Spline.Segment(
            startKnot = Point.of(2280.61, 473.44),
            edge = LineSegment.Edge,
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(1792.79, 202.57),
            edge = CubicBezierCurve.Edge(
                control0 = Point.of(1723.42, 384.97),
                control1 = Point.of(1623.99, 754.72),
            ),
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(1257.83, 966.02),
            edge = CubicBezierCurve.Edge(
                control0 = Point.of(1132.20, 1038.51),
                control1 = Point.of(989.98, 1075.54),
            ),
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(904.01, 1075.54),
            edge = CubicBezierCurve.Edge(
                control0 = Point.of(753.29, 1075.54),
                control1 = Point.of(570.93, 1014.24),
            ),
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(481.01, 937.40),
            edge = CubicBezierCurve.Edge(
                control0 = Point.of(378.19, 849.55),
                control1 = Point.of(225.24, 706.46),
            ),
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(214.84, 275.30),
            edge = LineSegment.Edge,
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(105.95, 275.60),
            edge = LineSegment.Edge,
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(106.38, 1032.16),
            edge = CubicBezierCurve.Edge(
                control0 = Point.of(152.17, 1034.32),
                control1 = Point.of(261.58, 1053.27),
            ),
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(439.58, 1154.19),
            edge = CubicBezierCurve.Edge(
                control0 = Point.of(455.23, 1163.06),
                control1 = Point.of(597.32, 1264.28),
            ),
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(901.22, 1278.41),
            edge = LineSegment.Edge,
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(903.32, 1278.66),
            edge = CubicBezierCurve.Edge(
                control0 = Point.of(1082.88, 1278.66),
                control1 = Point.of(1253.59, 1215.61),
            ),
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(1372.10, 1162.81),
            edge = CubicBezierCurve.Edge(
                control0 = Point.of(1382.84, 1158.02),
                control1 = Point.of(1664.43, 1036.65),
            ),
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(1860.92, 1027.53),
            edge = CubicBezierCurve.Edge(
                control0 = Point.of(1949.62, 1023.41),
                control1 = Point.of(2107.08, 1053.89),
            ),
            edgeMetadata = SeamAllowanceKind.Large,
        ),
        Spline.Segment(
            startKnot = Point.of(2147.96, 1061.04),
            edge = LineSegment.Edge,
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(3349.58, 1366.79),
            edge = LineSegment.Edge,
            edgeMetadata = SeamAllowanceKind.Small,
        ),
        Spline.Segment(
            startKnot = Point.of(3434.71, 1075.76),
            edge = LineSegment.Edge,
            edgeMetadata = SeamAllowanceKind.Small,
        ),
    ),
)

fun main() {
//    val spline = extractSplineFromFile(
//        filePath = Path("/Users/jakub/Temporary/Shape.svg"),
//    ).simplified

    println(spline.dump())

    val contourSpline = spline.findContourSpline(
        offsetStrategy = object : ClosedSpline.ContourOffsetStrategy<SeamAllowanceKind>() {
            override fun determineOffsetParams(
                edgeMetadata: SeamAllowanceKind,
            ): SegmentCurve.OffsetSplineParams {
                val seamAllowanceKind = edgeMetadata
                return SegmentCurve.OffsetSplineParams(
                    offset = seamAllowanceKind.widthMm,
                )
            }
        },
    )!!

    val contourBoundingBox = contourSpline.findBoundingBox()

    val document = createSvgDocument().apply {
        documentSvgElement.apply {
            viewBox = contourBoundingBox.toSvgViewBox()
            width = contourBoundingBox.width.toInt()
            height = contourBoundingBox.height.toInt()
        }

        documentSvgElement.appendChild(
            spline.toDebugSvgPathGroup(document = this)
        )

//        documentSvgElement.appendChild(
//            SVGGElementUtils.of(
//                document = document,
//                elements = offsetSplines.map {
//                    it.toDebugSvgPathGroup(document = document)
//                }
//            )
//        )

        documentSvgElement.appendChild(
            contourSpline.toDebugSvgPathGroup(document = this)
        )
    }

    document.writeToFile(
        filePath = Path("/Users/jakub/Temporary/Shape2.svg"),
    )
}
