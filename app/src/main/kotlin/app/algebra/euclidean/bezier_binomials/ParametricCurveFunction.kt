package app.algebra.euclidean.bezier_binomials

import app.algebra.NumericObject
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
    fun solveIntersection(
        curveFunction: ParametricCurveFunction,
    ): List<Double> {
        val lineImplicit = curveFunction.implicitize()
        val thisParametric = toParametricPolynomial()
        val intersectionPolynomial = lineImplicit.put(thisParametric)
        return intersectionPolynomial.findRoots()
    }

    abstract fun solvePoint(
        p: RawVector,
        tolerance: NumericObject.Tolerance,
    ): Double?

    abstract fun implicitize(): ImplicitPolynomial

    abstract fun findDerivative(): ParametricCurveFunction

    abstract fun toParametricPolynomial(): ParametricPolynomial
}
