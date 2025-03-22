package app.geometry.bezier_splines

import app.geometry.bezier_curves.BezierCurve

/**
 * A mono-Bézier curve (a spline formed of a single curve)
 */
class MonoBezierCurve(
    val curve: BezierCurve<*>,
) : OpenBezierSpline() {
    override val startNode = InnerNode.start(
        point = curve.start,
        control1 = curve.firstControl,
    )

    override val innerNodes: List<InnerNode> = emptyList()

    override val endNode = InnerNode.end(
        control0 = curve.lastControl,
        point = curve.end,
    )

    override val subCurves: List<BezierCurve<*>> = listOf(curve)
}
