package app.geometry.bezier_splines

import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.CubicBezierCurve

class OpenPolyBezierCurve(
    override val startNode: StartNode,
    override val innerNodes: List<BezierSpline.InnerNode>,
    override val endNode: EndNode,
) : OpenBezierSpline() {
    override val subCurves: List<BezierCurve<*>> by lazy {
        when {
            innerNodes.isEmpty() -> listOf(
                CubicBezierCurve.of(
                    start = startNode.knotPoint,
                    control0 = startNode.forwardControl,
                    control1 = endNode.backwardControl,
                    end = endNode.knotPoint,
                ),
            )

            else -> listOf(
                CubicBezierCurve.of(
                    start = startNode.knotPoint,
                    control0 = startNode.forwardControl,
                    control1 = secondNode.backwardControl,
                    end = secondNode.knotPoint,
                ),
            ) + BezierCurve.interConnectAll(
                innerNodes = innerNodes,
            ) + CubicBezierCurve.of(
                start = oneBeforeEndNode.knotPoint,
                control0 = oneBeforeEndNode.forwardControl,
                control1 = endNode.backwardControl,
                end = endNode.knotPoint,
            )
        }
    }
}
