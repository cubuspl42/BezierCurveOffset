package app.geometry

import app.geometry.bezier_curves.CubicBezierCurve

/**
 * A poly-Bézier curve, also called a composite Bézier curve
 */
class PolyCubicBezierCurve(
    /**
     * The nodes of this composite curve. The first control point of the first node and the last control point of the
     * last node are not effective.
     */
    override val nodes: List<CubicBezierSpline.Node>,
) : CubicBezierSpline {
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
