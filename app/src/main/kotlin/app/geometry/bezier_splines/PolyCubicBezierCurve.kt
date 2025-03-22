package app.geometry.bezier_splines

import app.geometry.bezier_curves.CubicBezierCurve

/**
 * A poly-Bézier curve, also called a composite Bézier curve or a Bézier spline
 * (a spline formed of Bézier curves)
 */
class PolyCubicBezierCurve(
    override val nodes: List<CubicBezierSpline.Node>,
) : CubicBezierSpline() {
    init {
        require(nodes.size >= 2)
    }

    override val subCurves: List<CubicBezierCurve> by lazy {
        nodes.zipWithNext { node, nextNode ->
            CubicBezierCurve(
                start = node.point,
                control0 = node.control1,
                control1 = nextNode.control0,
                end = nextNode.point,
            )
        }
    }
}

fun PolyCubicBezierCurve.findOffsetSplineBestFitPoly(
    offset: Double,
): CubicBezierSpline = joinOf {
    it.findOffsetSplineBestFit(offset = offset)
}
