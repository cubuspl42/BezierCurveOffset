package app.geometry.bezier_splines

import app.geometry.bezier_curves.CubicBezierCurve

/**
 * A bi-Bézier curve (a pair of Bézier curves)
 */
class BiCubicBezierCurve(
    val startNode: CubicBezierSpline.Node,
    val midNode: CubicBezierSpline.Node,
    val endNode: CubicBezierSpline.Node,
) : CubicBezierSpline {
    override val nodes: List<CubicBezierSpline.Node> = listOf(
        startNode,
        midNode,
        endNode,
    )

    val firstSubCurve by lazy {
        CubicBezierCurve(
            start = startNode.point,
            control0 = startNode.control1,
            control1 = midNode.control0,
            end = midNode.point,
        )
    }

    val secondSubCurve by lazy {
        CubicBezierCurve(
            start = midNode.point,
            control0 = midNode.control1,
            control1 = endNode.control0,
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
