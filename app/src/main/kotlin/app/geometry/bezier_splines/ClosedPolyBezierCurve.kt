package app.geometry.bezier_splines

import app.geometry.bezier_curves.BezierCurve

class ClosedPolyBezierCurve(
    override val innerNodes: List<InnerNode>,
) : ClosedBezierSpline() {
    init {
        require(innerNodes.size >= 2)
    }

    override val subCurves: List<BezierCurve> by lazy {
        BezierCurve.interConnectAll(
            innerNodes = innerNodes,
        ) + BezierCurve.interConnect(
            prevNode = innerNodes.last(),
            nextNode = innerNodes.first(),
        )
    }
}
