package app.geometry.bezier_splines

import app.geometry.bezier_curves.CubicBezierCurve

class OpenPolyBezierCurve(
    override val startNode: StartNode,
    override val innerNodes: List<BezierSpline.InnerNode>,
    override val endNode: EndNode,
) : OpenBezierSpline() {
    override val subCurves: List<CubicBezierCurve> by lazy {
        listOf(
            CubicBezierCurve(
                start = startNode.point,
                control0 = startNode.forwardControl,
                control1 = secondNode.backwardControl,
                end = secondNode.point,
            ),
        ) + CubicBezierCurve.interConnectAll(
            innerNodes = innerNodes,
        ) + CubicBezierCurve(
            start = oneBeforeEndNode.point,
            control0 = oneBeforeEndNode.forwardControl,
            control1 = endNode.backwardControl,
            end = endNode.point,
        )
    }
}
