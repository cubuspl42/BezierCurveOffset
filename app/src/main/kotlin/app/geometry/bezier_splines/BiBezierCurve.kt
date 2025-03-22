package app.geometry.bezier_splines

import app.geometry.bezier_curves.BezierCurve

/**
 * A bi-Bézier curve (a spline formed of a pair of Bézier curves)
 */
class BiBezierCurve(
    override val startNode: OpenBezierSpline.StartNode,
    val midNode: OpenBezierSpline.InnerNode,
    override val endNode: OpenBezierSpline.EndNode,
) : OpenBezierSpline() {
    override val innerNodes: List<OpenBezierSpline.InnerNode> = listOf(midNode)

    val firstSubCurve by lazy {
        BezierCurve(
            start = startNode.point,
            control0 = startNode.forwardControl,
            control1 = midNode.backwardControl,
            end = midNode.point,
        )
    }

    val secondSubCurve by lazy {
        BezierCurve(
            start = midNode.point,
            control0 = midNode.forwardControl,
            control1 = endNode.backwardControl,
            end = endNode.point,
        )
    }

    override val subCurves: List<BezierCurve> by lazy {
        listOf(
            firstSubCurve,
            secondSubCurve,
        )
    }
}
