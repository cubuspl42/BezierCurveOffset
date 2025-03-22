package app.geometry.bezier_splines

import app.geometry.bezier_curves.CubicBezierCurve

/**
 * A bi-Bézier curve (a spline formed of a pair of Bézier curves)
 */
class BiCubicBezierCurve(
    override val startNode: OpenCubicBezierSpline.StartNode,
    val midNode: OpenCubicBezierSpline.InnerNode,
    override val endNode: OpenCubicBezierSpline.EndNode,
) : OpenCubicBezierSpline() {
    override val innerNodes: List<OpenCubicBezierSpline.InnerNode> = listOf(midNode)

    val firstSubCurve by lazy {
        CubicBezierCurve(
            start = startNode.point,
            control0 = startNode.forwardControl,
            control1 = midNode.backwardControl,
            end = midNode.point,
        )
    }

    val secondSubCurve by lazy {
        CubicBezierCurve(
            start = midNode.point,
            control0 = midNode.forwardControl,
            control1 = endNode.backwardControl,
            end = endNode.point,
        )
    }

    override val subCurves: List<CubicBezierCurve> by lazy {
        listOf(
            firstSubCurve,
            secondSubCurve,
        )
    }
}
