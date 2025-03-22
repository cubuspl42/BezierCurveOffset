package app.geometry.bezier_splines

import app.geometry.bezier_curves.BezierCurve

/**
 * A poly-Bézier curve, also called a composite Bézier curve or a Bézier spline
 * (a spline formed of Bézier curves)
 */
class PolyBezierCurve(
    override val startNode: StartNode,
    override val innerNodes: List<OpenBezierSpline.InnerNode>,
    override val endNode: EndNode,
) : OpenBezierSpline() {
    override val subCurves: List<BezierCurve> by lazy {
        listOf(
            BezierCurve(
                start = startNode.point,
                control0 = startNode.forwardControl,
                control1 = secondNode.backwardControl,
                end = secondNode.point,
            ),
        ) + innerNodes.zipWithNext { prevNode, nextNode ->
            BezierCurve(
                start = prevNode.point,
                control0 = prevNode.forwardControl,
                control1 = nextNode.backwardControl,
                end = nextNode.point,
            )
        } + BezierCurve(
            start = oneBeforeEndNode.point,
            control0 = oneBeforeEndNode.forwardControl,
            control1 = endNode.backwardControl,
            end = endNode.point,
        )
    }
}

fun PolyBezierCurve.findOffsetSplineBestFitPoly(
    offset: Double,
): OpenBezierSpline = mergeOf {
    it.findOffsetSplineBestFit(offset = offset)
}
