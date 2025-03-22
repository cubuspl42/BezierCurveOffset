package app.geometry.bezier_splines

import app.geometry.Point
import app.geometry.bezier_curves.BezierCurve

/**
 * A Bézier spline, also called "poly-Bézier curve", or "composite Bézier curve"
 * (a spline formed of cubic Bézier curves)
 */
abstract class BezierSpline {
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

    abstract val nodes: List<Node>

    abstract val innerNodes: List<InnerNode>

    abstract val subCurves: List<BezierCurve>
}
