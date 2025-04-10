package app.geometry.splines

import app.Dumpbable
import app.SVGGElementUtils
import app.algebra.NumericObject
import app.geometry.Point
import app.geometry.cubicTo
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.curves.toDebugSvgPathGroup
import app.geometry.lineTo
import app.geometry.moveTo
import app.geometry.transformations.Transformation
import app.mapWithNext
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGGElement
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D

/**
 * A Bézier spline, also called "poly-Bézier curve", or "composite Bézier curve"
 * (a spline formed of cubic Bézier curves)
 */
sealed class Spline<
        out CurveT : SegmentCurve<CurveT>,
        out EdgeMetadata,
        > {
    sealed interface Node {
        /**
         * The "front" knot, i.e. the next knot when looked from the perspective
         * of the previous node.
         */
        val frontKnot: Point
    }

    data class Segment<
            out CurveT : SegmentCurve<CurveT>,
            out EdgeMetadata,
            >(
        val startKnot: Point,
        val edge: SegmentCurve.Edge<CurveT>,
        val edgeMetadata: EdgeMetadata,
    ) : Node, NumericObject, Dumpbable {
        companion object {
            fun <Metadata> bezier(
                startKnot: Point,
                control0: Point,
                control1: Point,
                metadata: Metadata,
            ): Segment<CubicBezierCurve, Metadata> = Segment(
                startKnot = startKnot,
                edge = CubicBezierCurve.Edge(
                    control0 = control0,
                    control1 = control1,
                ),
                edgeMetadata = metadata,
            )

            fun <Metadata> lineSegment(
                startKnot: Point,
                metadata: Metadata,
            ): Segment<LineSegment, Metadata> = Segment(
                startKnot = startKnot,
                edge = LineSegment.Edge,
                edgeMetadata = metadata,
            )
        }

        override val frontKnot: Point
            get() = startKnot

        override fun dump() = """
            Spline.Segment(
                startKnot = ${startKnot.dump()},
                edge = ${edge.dump()},
            )
        """.trimIndent()

        fun simplify(
            endKnot: Point,
        ): Segment<*, EdgeMetadata> = Segment(
            startKnot = startKnot,
            edge = edge.simplify(
                startKnot = startKnot,
                endKnot = endKnot,
            ),
            edgeMetadata = edgeMetadata,
        )

        fun transformVia(
            transformation: Transformation,
        ): Segment<CurveT, EdgeMetadata> = Segment(
            startKnot = startKnot.transformVia(transformation = transformation),
            edge = edge.transformVia(transformation),
            edgeMetadata = edgeMetadata,
        )

        fun <NewEdgeMetadata> mapMetadata(
            transform: (EdgeMetadata) -> NewEdgeMetadata,
        ): Segment<CurveT, NewEdgeMetadata> = Segment(
            startKnot = startKnot,
            edge = edge,
            edgeMetadata = transform(edgeMetadata),
        )

        override fun equalsWithTolerance(other: NumericObject, absoluteTolerance: Double): Boolean {
            return when {
                other !is Segment<*, *> -> false
                !other.startKnot.equalsWithTolerance(other.startKnot, absoluteTolerance = absoluteTolerance) -> false
                other.edge != edge -> false
                else -> true
            }
        }
    }

    data class SubSegment<
            out CurveT : SegmentCurve<CurveT>,
            out EdgeMetadata,
            >(
        val edgeMetadata: EdgeMetadata,
        val segmentCurve: CurveT,
    )

    data class Terminator(
        val endKnot: Point,
    ) : Node {
        override val frontKnot: Point
            get() = endKnot
    }

    val firstSegment: Segment<CurveT, EdgeMetadata>
        get() = segments.first()

    /**
     * Splines always have at least one node
     */
    abstract val nodes: Iterable<Node>

    abstract val segments: Iterable<Segment<CurveT, EdgeMetadata>>

    abstract val rightEdgeNode: Node

    val subSegments: List<SubSegment<CurveT, EdgeMetadata>> by lazy {
        segments.mapWithNext(rightEdge = rightEdgeNode) { segment, nextNode ->
            val startKnot = segment.startKnot
            val endKnot = nextNode.frontKnot
            val edge = segment.edge

            SubSegment(
                edgeMetadata = segment.edgeMetadata,
                segmentCurve = edge.bind(
                    startKnot = startKnot,
                    endKnot = endKnot,
                ),
            )
        }
    }

    val subCurves: List<CurveT> by lazy {
        subSegments.map { it.segmentCurve }
    }
}

//fun Spline<*, *>.toDebugSvgPathGroup(
//    document: SVGDocument,
//): SVGGElement = document.createGElement().apply {
//    subCurves.forEach { subCurve ->
//        appendChild(subCurve.toDebugSvgPathGroup(document = document))
//    }
//}

fun Spline<*, *>.toDebugSvgPathGroup(
    document: SVGDocument,
): SVGGElement = SVGGElementUtils.of(
    document = document,
    elements = subCurves.map { subCurve ->
        subCurve.toDebugSvgPathGroup(document = document)
    },
)

fun OpenSpline<*, *>.toControlPathOpen(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstSegment.startKnot)

    subCurves.forEach { subCurve ->
        pathToControl(subCurve)
    }
}

fun OpenSpline<*, *>.toPathOpen(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstSegment.startKnot)

    subCurves.forEach { subCurve ->
        pathTo(subCurve)
    }
}

fun ClosedSpline<*, *>.toControlPathClosed(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstSegment.startKnot)

    subCurves.forEach { subCurve ->
        pathToControl(subCurve)
    }

    closePath()
}

fun ClosedSpline<*, *>.toPathClosed(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstSegment.startKnot)

    subCurves.forEach { subCurve ->
        pathTo(subCurve)
    }

    closePath()
}

fun Path2D.pathToControl(curve: SegmentCurve<*>) {
    when (curve) {
        is CubicBezierCurve -> {
            lineTo(p = curve.control0)
            lineTo(p = curve.control1)
            lineTo(p = curve.end)
        }
    }
}

fun Path2D.pathTo(curve: SegmentCurve<*>) {
    when (curve) {
        is CubicBezierCurve -> {
            cubicTo(p1 = curve.control0, p2 = curve.control1, p3 = curve.end)
        }
    }
}

fun Spline<*, *>.toControlPath(): Path2D.Double = when (this) {
    is ClosedSpline -> toControlPathClosed()
    is OpenSpline -> toControlPathOpen()
}

fun Spline<*, *>.toPath(): Path2D.Double = when (this) {
    is ClosedSpline -> toPathClosed()
    is OpenSpline -> toPathOpen()
}

fun Spline<*, *>.drawSpline(
    graphics2D: Graphics2D,
    color: Color = Color.BLACK,
) {
    graphics2D.color = Color.LIGHT_GRAY
    graphics2D.draw(toControlPath())

    graphics2D.color = color
    graphics2D.draw(toPath())
}
