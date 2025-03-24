package app.geometry.bezier_splines

import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.CubicBezierCurve

/**
 * A bi-Bézier curve (a spline formed of a pair of Bézier curves)
 */
class BiBezierCurve(
    override val startNode: StartNode,
    val midNode: InnerNode,
    override val endNode: EndNode,
) : OpenBezierSpline() {
    override val innerNodes: List<InnerNode> = listOf(midNode)

    val firstSubCurve by lazy {
        CubicBezierCurve.of(
            start = startNode.knotPoint,
            control0 = startNode.forwardControl,
            control1 = midNode.backwardControl,
            end = midNode.knotPoint,
        )
    }

    val secondSubCurve by lazy {
        CubicBezierCurve.of(
            start = midNode.knotPoint,
            control0 = midNode.forwardControl,
            control1 = endNode.backwardControl,
            end = endNode.knotPoint,
        )
    }

    override val subCurves: List<BezierCurve<*>> by lazy {
        listOf(
            firstSubCurve,
            secondSubCurve,
        )
    }
}
