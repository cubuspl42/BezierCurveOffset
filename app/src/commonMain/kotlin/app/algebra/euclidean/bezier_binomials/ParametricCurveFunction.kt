package app.algebra.euclidean.bezier_binomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsZeroWithTolerance
import app.algebra.implicit_polynomials.ImplicitPolynomial
import app.algebra.polynomials.ParametricPolynomial
import app.geometry.RawVector

abstract class ParametricCurveFunction : RealFunction<RawVector> {
    fun findCriticalPoints(): ParametricPolynomial.RootSet = findDerivative().findRoots()

    fun findRoots(): ParametricPolynomial.RootSet = toParametricPolynomial().findRoots()

    /**
     * Solve the intersection of this parametric curve with another parametric curve.
     *
     * @return A set of intersection parameter values t for this curve.
     */
    fun solveIntersections(
        other: ParametricCurveFunction,
    ): List<Double> {
        val otherImplicit = other.implicitize()
        val thisParametric = this.toParametricPolynomial()
        val intersectionPolynomial = otherImplicit.substitute(thisParametric)

        val tolerance = Tolerance.Absolute(absoluteTolerance = 10e-4)

        return intersectionPolynomial.findRoots(
            areClose = { t0, t1 ->
                val p0 = thisParametric.apply(t0).asPoint
                val p1 = thisParametric.apply(t1).asPoint

                val distance = p0.distanceTo(p1)

                distance.equalsZeroWithTolerance(tolerance = tolerance)
            },
        )
    }

    abstract fun solvePoint(
        p: RawVector,
        tolerance: NumericObject.Tolerance,
    ): Double?

    abstract fun implicitize(): ImplicitPolynomial

    abstract fun findDerivative(): ParametricCurveFunction

    abstract fun toParametricPolynomial(): ParametricPolynomial
}
