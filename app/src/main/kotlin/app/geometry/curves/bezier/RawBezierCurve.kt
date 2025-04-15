package app.geometry.curves.bezier

import app.algebra.bezier_binomials.DifferentiableBezierBinomial
import app.algebra.bezier_binomials.findFaster
import app.geometry.Curve
import app.geometry.Direction
import app.geometry.Point
import app.geometry.RawVector

/**
 * A raw Bézier curve
 */
sealed class RawBezierCurve : Curve() {
    val curveFunction: TimeFunction<Point> by lazy {
        basisFormula.findFaster().map { it.asPoint }
    }

    /**
     * The tangent direction function of the curve, based on the curve's
     * velocity. If this curve is degenerate, it might "slow down" at some point
     * to zero, so the tangent direction is non-existent (null). Theoretically,
     * for longitudinal (non-point) curves (even the otherwise degenerate ones),
     * the tangent should always be defined for t=0 and t=1, but even that is
     * difficult to guarantee from the numerical perspective.
     */
    val tangentFunction: RawTimeFunction<Direction?> by lazy {
        basisFormula.findDerivative().map {
            Direction.of(it)
        }
    }

    /**
     * The normal direction of the curve, i.e. the direction perpendicular to
     * the tangent direction.
     */
    val normalFunction: RawTimeFunction<Direction?> by lazy {
        tangentFunction.map {
            it?.perpendicular
        }
    }

    abstract val basisFormula: DifferentiableBezierBinomial<RawVector>
}
