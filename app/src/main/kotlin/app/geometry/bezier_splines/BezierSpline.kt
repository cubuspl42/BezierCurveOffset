package app.geometry.bezier_splines

import app.fillCircle
import app.geometry.Point
import app.geometry.Segment
import app.geometry.bezier_curves.BezierCurve
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
        val point: Point
        val forwardControl: Point?
    }

    sealed interface ForwardNode : Node {
        override val forwardControl: Point
    }

    sealed interface BackwardNode : Node {
        override val backwardControl: Point
    }

    class StartNode(
        override val point: Point,
        override val forwardControl: Point,
    ) : ForwardNode {
        override val backwardControl: Nothing? = null
    }

    class InnerNode(
        override val backwardControl: Point,
        override val point: Point,
        override val forwardControl: Point,
    ) : ForwardNode, BackwardNode {
        companion object {
            fun start(
                point: Point,
                control1: Point,
            ): StartNode = StartNode(
                point = point,
                forwardControl = control1,
            )

            fun end(
                control0: Point,
                point: Point,
            ): OpenBezierSpline.EndNode = OpenBezierSpline.EndNode(
                backwardControl = control0,
                point = point,
            )
        }
    }

    fun mergeOf(
        transform: (BezierCurve) -> OpenBezierSpline,
    ): SplineT = prototype.merge(
        splines = subCurves.map(transform),
    )

    fun findOffsetSpline(
        strategy: BezierCurve.OffsetStrategy,
        offset: Double,
    ): SplineT = mergeOf {
        it.findOffsetSpline(
            strategy = strategy,
            offset = offset,
        )
    }

    fun findOffsetSplineBestFit(
        offset: Double,
    ): SplineT = findOffsetSpline(
        strategy = BezierCurve.BestFitOffsetStrategy,
        offset = offset,
    )

    fun findOffsetSplineNormal(
        offset: Double,
    ): SplineT = findOffsetSpline(
        strategy = BezierCurve.NormalOffsetStrategy,
        offset = offset,
    )

    abstract val prototype: Prototype<SplineT>

    abstract val nodes: List<Node>

    abstract val innerNodes: List<InnerNode>

    abstract val subCurves: List<BezierCurve>
}

fun BezierSpline<*>.toPath2D(): Path2D.Double = Path2D.Double().apply {
    moveTo(nodes.first().point)

    subCurves.forEach { subCurve ->
        cubicTo(
            control1 = subCurve.control0, control2 = subCurve.control1, end = subCurve.end
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
            center = it.point,
            radius = 2.0,
        )
    }
}
