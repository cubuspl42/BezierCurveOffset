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

fun extractSplineFromElement(
    transformation: TotalTransformation,
    element: Element,
): ClosedSpline<*, *> = when (val singleChild = element.childElements.single()) {
    is SVGPathElement -> singleChild.toClosedSpline().transformVia(
        transformation = transformation
    )

    is SVGGElement -> {
        val newTransformation = singleChild.transformation.applyOver(base = transformation)

        extractSplineFromElement(
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
    val pathElement = extractSplineFromElement(
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
