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
        out KnotMetadata,
        > {
    sealed interface Node<out KnotMetadata> {
        /**
         * The "front" knot, i.e. the next knot when looked from the perspective
         * of the previous node.
         */
        val frontKnot: Point

        val frontKnotMetadata: KnotMetadata
    }

    data class Segment<
            out CurveT : SegmentCurve<CurveT>,
            out EdgeMetadata,
            out KnotMetadata,
            >(
        val startKnot: Point,
        val startKnotMetadata: KnotMetadata,
        val edge: SegmentCurve.Edge<CurveT>,
        val edgeMetadata: EdgeMetadata,
    ) : Node<KnotMetadata>, NumericObject, Dumpbable {
        companion object {
            fun <EdgeMetadata, KnotMetadata> bezier(
                startKnot: Point,
                control0: Point,
                control1: Point,
                edgeMetadata: EdgeMetadata,
                knotMetadata: KnotMetadata,
            ): Segment<CubicBezierCurve, EdgeMetadata, KnotMetadata> = Segment(
                startKnot = startKnot,
                edge = CubicBezierCurve.Edge(
                    control0 = control0,
                    control1 = control1,
                ),
                edgeMetadata = edgeMetadata,
                startKnotMetadata = knotMetadata,
            )

            fun <EdgeMetadata, KnotMetadata> lineSegment(
                startKnot: Point,
                edgeMetadata: EdgeMetadata,
                knotMetadata: KnotMetadata,
            ): Segment<LineSegment, EdgeMetadata, KnotMetadata> = Segment(
                startKnot = startKnot,
                edge = LineSegment.Edge,
                edgeMetadata = edgeMetadata,
                startKnotMetadata = knotMetadata,
            )
        }

        override val frontKnot: Point
            get() = startKnot

        override val frontKnotMetadata: KnotMetadata
            get() = startKnotMetadata

        override fun dump() = """
            Spline.Segment(
                startKnot = ${startKnot.dump()},
                edge = ${edge.dump()},
                edgeMetadata = TODO(),
            )
        """.trimIndent()

        fun simplify(
            endKnot: Point,
        ): Segment<*, EdgeMetadata, KnotMetadata> = Segment(
            startKnot = startKnot,
            edge = edge.simplify(
                startKnot = startKnot,
                endKnot = endKnot,
            ),
            edgeMetadata = edgeMetadata,
            startKnotMetadata = startKnotMetadata,
        )

        fun transformVia(
            transformation: Transformation,
        ): Segment<CurveT, EdgeMetadata, KnotMetadata> = Segment(
            startKnot = startKnot.transformVia(transformation = transformation),
            edge = edge.transformVia(transformation),
            edgeMetadata = edgeMetadata,
            startKnotMetadata = startKnotMetadata,
        )

        fun <NewEdgeMetadata> mapEdgeMetadata(
            transform: (EdgeMetadata) -> NewEdgeMetadata,
        ): Segment<CurveT, NewEdgeMetadata, KnotMetadata> = Segment(
            startKnot = startKnot,
            edge = edge,
            edgeMetadata = transform(edgeMetadata),
            startKnotMetadata = startKnotMetadata,
        )

        override fun equalsWithTolerance(other: NumericObject, absoluteTolerance: Double): Boolean {
            return when {
                other !is Segment<*, *, *> -> false
                !other.startKnot.equalsWithTolerance(other.startKnot, absoluteTolerance = absoluteTolerance) -> false
                other.edge != edge -> false
                else -> true
            }
        }

        fun <NewKnotMetadata> replaceKnotMetadata(
            newKnotMetadata: NewKnotMetadata,
        ): Segment<CurveT, EdgeMetadata, NewKnotMetadata> = Segment(
            startKnot = startKnot,
            edge = edge,
            edgeMetadata = edgeMetadata,
            startKnotMetadata = newKnotMetadata,
        )
    }

    data class EdgeChunk<
            out CurveT : SegmentCurve<CurveT>,
            out EdgeMetadata,
            out KnotMetadata,
            >(
        val prevKnotMetadata: KnotMetadata,
        val prevKnot: Point,
        val edgeMetadata: EdgeMetadata,
        val edgeCurve: CurveT,
        val nextKnotMetadata: KnotMetadata,
        val nextKnot: Point,
    )

    data class Terminator<out KnotMetadata>(
        val endKnot: Point,
        val endKnotMetadata: KnotMetadata,
    ) : Node<KnotMetadata> {
        override val frontKnot: Point
            get() = endKnot

        override val frontKnotMetadata: KnotMetadata
            get() = endKnotMetadata
    }

    val firstSegment: Segment<CurveT, EdgeMetadata, KnotMetadata>
        get() = segments.first()

    /**
     * Splines always have at least one node
     */
    abstract val nodes: Iterable<Node<KnotMetadata>>

    abstract val segments: Iterable<Segment<CurveT, EdgeMetadata, KnotMetadata>>

    abstract val rightEdgeNode: Node<KnotMetadata>

    val edgeChunks: List<EdgeChunk<CurveT, EdgeMetadata, KnotMetadata>> by lazy {
        segments.mapWithNext(rightEdge = rightEdgeNode) { segment, nextNode ->
            val startKnot = segment.startKnot
            val endKnot = nextNode.frontKnot
            val edge = segment.edge

            EdgeChunk(
                prevKnotMetadata = segment.startKnotMetadata,
                prevKnot = startKnot,
                edgeMetadata = segment.edgeMetadata,
                edgeCurve = edge.bind(
                    startKnot = startKnot,
                    endKnot = endKnot,
                ),
                nextKnotMetadata = nextNode.frontKnotMetadata,
                nextKnot = endKnot,
            )
        }
    }

    val subCurves: List<CurveT> by lazy {
        edgeChunks.map { it.edgeCurve }
    }
}

//fun Spline<*, *, *>.toDebugSvgPathGroup(
//    document: SVGDocument,
//): SVGGElement = document.createGElement().apply {
//    subCurves.forEach { subCurve ->
//        appendChild(subCurve.toDebugSvgPathGroup(document = document))
//    }
//}

fun Spline<*, *, *>.toDebugSvgPathGroup(
    document: SVGDocument,
): SVGGElement = SVGGElementUtils.of(
    document = document,
    elements = subCurves.map { subCurve ->
        subCurve.toDebugSvgPathGroup(document = document)
    },
)

fun OpenSpline<*, *, *>.toControlPathOpen(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstSegment.startKnot)

    subCurves.forEach { subCurve ->
        pathToControl(subCurve)
    }
}

fun OpenSpline<*, *, *>.toPathOpen(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstSegment.startKnot)

    subCurves.forEach { subCurve ->
        pathTo(subCurve)
    }
}

fun ClosedSpline<*, *, *>.toControlPathClosed(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstSegment.startKnot)

    subCurves.forEach { subCurve ->
        pathToControl(subCurve)
    }

    closePath()
}

fun ClosedSpline<*, *, *>.toPathClosed(): Path2D.Double = Path2D.Double().apply {
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

fun Spline<*, *, *>.toControlPath(): Path2D.Double = when (this) {
    is ClosedSpline -> toControlPathClosed()
    is OpenSpline -> toControlPathOpen()
}

fun Spline<*, *, *>.toPath(): Path2D.Double = when (this) {
    is ClosedSpline -> toPathClosed()
    is OpenSpline -> toPathOpen()
}

fun Spline<*, *, *>.drawSpline(
    graphics2D: Graphics2D,
    color: Color = Color.BLACK,
) {
    graphics2D.color = Color.LIGHT_GRAY
    graphics2D.draw(toControlPath())

    graphics2D.color = color
    graphics2D.draw(toPath())
}
