package app.geometry.bezier_splines

import app.geometry.bezier_curves.CubicBezierCurve

class ClosedPolyBezierCurve(
    override val innerNodes: List<InnerNode>,
) : ClosedBezierSpline() {
    init {
        require(innerNodes.size >= 2)
    }

    override val subCurves: List<CubicBezierCurve> by lazy {
        CubicBezierCurve.interConnectAll(
            innerNodes = innerNodes,
        ) + CubicBezierCurve.interConnect(
            prevNode = innerNodes.last(),
            nextNode = innerNodes.first(),
        )
    }
}
