package app.geometry.bezier_splines

import app.fillCircle
import app.geometry.Point
import app.geometry.Segment
import app.geometry.bezier_curves.*
import app.geometry.cubicTo
import app.geometry.moveTo
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D

/**
 * A Bézier spline, also called "poly-Bézier curve", or "composite Bézier curve"
 * (a spline formed of cubic Bézier curves)
 */
abstract class BezierSpline<SplineT : BezierSpline<SplineT>> {
    abstract class Prototype<SplineT : BezierSpline<SplineT>> {
        abstract fun merge(
            splines: List<OpenBezierSpline>,
        ): SplineT
    }

    sealed interface Node {
        val backwardControl: Point?
        val knotPoint: Point
        val forwardControl: Point?

        fun isLoose(): Boolean
    }

    sealed interface ForwardNode : Node {
        override val forwardControl: Point
    }

    sealed interface BackwardNode : Node {
        override val backwardControl: Point
    }

    class StartNode(
        override val knotPoint: Point,
        override val forwardControl: Point,
    ) : ForwardNode {
        override val backwardControl: Nothing? = null

        override fun isLoose(): Boolean = knotPoint == forwardControl
    }

    class EndNode(
        override val knotPoint: Point,
        override val backwardControl: Point,
    ) : BackwardNode {
        override val forwardControl: Nothing? = null
        override fun isLoose(): Boolean {
            TODO("Not yet implemented")
        }
    }

    class InnerNode(
        override val backwardControl: Point,
        override val knotPoint: Point,
        override val forwardControl: Point,
    ) : ForwardNode, BackwardNode {
        companion object {
            fun start(
                point: Point,
                control1: Point,
            ): StartNode = StartNode(
                knotPoint = point,
                forwardControl = control1,
            )

            fun end(
                control0: Point,
                point: Point,
            ): EndNode = EndNode(
                backwardControl = control0,
                knotPoint = point,
            )
        }

        override fun isLoose(): Boolean = knotPoint == backwardControl && knotPoint == forwardControl
    }

    /**
     * @return A merged spline, or null if no sub-curves could be constructed
     */
    fun mergeOfNonNullOrNull(
        transformCurve: (BezierCurve<*>) -> OpenBezierSpline?,
    ): SplineT? {
        val transformedSplines = subCurves.mapNotNull(transformCurve)

        return when {
            transformedSplines.isEmpty() -> null

            else -> prototype.merge(
                splines = transformedSplines,
            )
        }
    }

    /**
     * A singularity spline is effectively a point. Mathematically, it's the
     * worst of corner cases.
     */
    fun isSingularity(): Boolean = knotPoints.size == 1 && nodes.all { it.isLoose() }

    abstract val prototype: Prototype<SplineT>

    /**
     * Splines always have at least one node
     */
    abstract val nodes: List<Node>

    /**
     * In a corner case (an open spline with just th start and the end node), a
     * spline might have no inner nodes.
     */
    abstract val innerNodes: List<InnerNode>

    /**
     * Splines always have at least one knot point
     */
    val knotPoints: Set<Point> by lazy {
        nodes.map { it.knotPoint }.toSet()
    }

    /**
     * Splines always have at least one sub-curve, but none of them might be
     * proper in a corner case (they might all be points)
     */
    abstract val subCurves: List<BezierCurve<*>>
}

fun Path2D.pathTo(
    subCurve: BezierCurve<*>,
) {
    when (subCurve) {
        is PointBezierCurve -> TODO()
        is LineSegmentBezierCurve -> TODO()
        is QuadraticBezierCurve -> TODO()
        is CubicBezierCurve -> cubicTo(
            control1 = subCurve.control0,
            control2 = subCurve.control1,
            end = subCurve.end,
        )
    }
}

fun BezierSpline<*>.toPath2D(): Path2D.Double = Path2D.Double().apply {
    moveTo(nodes.first().knotPoint)

    subCurves.forEach { subCurve ->
        pathTo(
            subCurve = subCurve,
        )
    }
}

fun BezierSpline<*>.drawSpline(
    graphics2D: Graphics2D,
    color: Color = Color.BLACK,
) {
    fun drawControlSegment(
        controlSegment: Segment,
    ) {
        graphics2D.draw(controlSegment.toLine2D())
        graphics2D.fillCircle(
            center = controlSegment.end,
            radius = 2.0,
        )
    }

    graphics2D.color = Color.LIGHT_GRAY

    nodes.forEach { node ->
        (node as? BezierSpline.BackwardNode)?.let {
            drawControlSegment(it.backwardControlSegment)
        }

        (node as? BezierSpline.ForwardNode)?.let {
            drawControlSegment(it.forwardControlSegment)
        }
    }

    graphics2D.color = color
    graphics2D.draw(toPath2D())

    nodes.forEach {
        graphics2D.fillCircle(
            center = it.knotPoint,
            radius = 2.0,
        )
    }
}
