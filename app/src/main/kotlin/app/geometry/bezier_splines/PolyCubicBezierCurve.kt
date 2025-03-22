package app.geometry.bezier_splines

import app.geometry.bezier_curves.CubicBezierCurve

/**
 * A poly-Bézier curve, also called a composite Bézier curve or a Bézier spline
 * (a spline formed of Bézier curves)
 */
class PolyCubicBezierCurve(
    override val startNode: StartNode,
    override val innerNodes: List<CubicBezierSpline.InnerNode>,
    override val endNode: EndNode,
) : CubicBezierSpline() {
    override val subCurves: List<CubicBezierCurve> by lazy {
        listOf(
            CubicBezierCurve(
                start = startNode.point,
                control0 = startNode.forwardControl,
                control1 = secondNode.backwardControl,
                end = secondNode.point,
            ),
        ) + innerNodes.zipWithNext { prevNode, nextNode ->
            CubicBezierCurve(
                start = prevNode.point,
                control0 = prevNode.forwardControl,
                control1 = nextNode.backwardControl,
                end = nextNode.point,
            )
        } + CubicBezierCurve(
            start = oneBeforeEndNode.point,
            control0 = oneBeforeEndNode.forwardControl,
            control1 = endNode.backwardControl,
            end = endNode.point,
        )
    }
}

fun PolyCubicBezierCurve.findOffsetSplineBestFitPoly(
    offset: Double,
): CubicBezierSpline = joinOf {
    it.findOffsetSplineBestFit(offset = offset)
}
