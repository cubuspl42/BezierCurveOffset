package app.geometry.bezier_splines

import app.geometry.bezier_curves.CubicBezierCurve

/**
 * A mono-Bézier curve (a spline formed of a single curve)
 */
class MonoCubicBezierCurve(
    val curve: CubicBezierCurve
) : CubicBezierSpline() {
    override val nodes: List<CubicBezierSpline.Node> by lazy {
        listOf(
            CubicBezierSpline.Node.start(
                point = curve.start,
                control1 = curve.control0,
            ),
            CubicBezierSpline.Node.end(
                control0 = curve.control1,
                point = curve.end,
            ),
        )
    }

    override val subCurves: List<CubicBezierCurve> = listOf(curve)
}
