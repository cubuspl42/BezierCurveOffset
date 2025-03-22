package app.geometry.bezier_splines

import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.CubicBezierCurve

class OpenPolyBezierCurve(
    override val startNode: StartNode,
    override val innerNodes: List<BezierSpline.InnerNode>,
    override val endNode: EndNode,
) : OpenBezierSpline() {
    override val subCurves: List<BezierCurve<*>> by lazy {
        listOf(
            CubicBezierCurve.of(
                start = startNode.point,
                control0 = startNode.forwardControl,
                control1 = secondNode.backwardControl,
                end = secondNode.point,
            ),
        ) + BezierCurve.interConnectAll(
            innerNodes = innerNodes,
        ) + CubicBezierCurve.of(
            start = oneBeforeEndNode.point,
            control0 = oneBeforeEndNode.forwardControl,
            control1 = endNode.backwardControl,
            end = endNode.point,
        )
    }
}
