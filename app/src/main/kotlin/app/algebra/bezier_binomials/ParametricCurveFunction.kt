package app.algebra.bezier_binomials

import app.algebra.polynomials.ParametricPolynomial
import app.geometry.RawVector

abstract class ParametricCurveFunction : RealFunction<RawVector> {
    fun findCriticalPoints(): ParametricPolynomial.RootSet = findDerivative().findRoots()

    fun findRoots(): ParametricPolynomial.RootSet = toParametricPolynomial().findRoots()

    abstract fun findDerivative(): ParametricCurveFunction

    abstract fun toParametricPolynomial(): ParametricPolynomial
}
