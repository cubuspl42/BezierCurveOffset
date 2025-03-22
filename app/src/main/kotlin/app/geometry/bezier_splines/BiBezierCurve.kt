package app.geometry.bezier_splines

import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.CubicBezierCurve

/**
 * A bi-Bézier curve (a spline formed of a pair of Bézier curves)
 */
class BiBezierCurve(
    override val startNode: BezierSpline.StartNode,
    val midNode: BezierSpline.InnerNode,
    override val endNode: OpenBezierSpline.EndNode,
) : OpenBezierSpline() {
    override val innerNodes: List<BezierSpline.InnerNode> = listOf(midNode)

    val firstSubCurve by lazy {
        CubicBezierCurve.of(
            start = startNode.point,
            control0 = startNode.forwardControl,
            control1 = midNode.backwardControl,
            end = midNode.point,
        )
    }

    val secondSubCurve by lazy {
        CubicBezierCurve.of(
            start = midNode.point,
            control0 = midNode.forwardControl,
            control1 = endNode.backwardControl,
            end = endNode.point,
        )
    }

    override val subCurves: List<BezierCurve<*>> by lazy {
        listOf(
            firstSubCurve,
            secondSubCurve,
        )
    }
}
