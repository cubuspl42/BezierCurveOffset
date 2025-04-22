package app.geometry

import app.algebra.euclidean.bezier_binomials.ParametricCurveFunction
import app.algebra.polynomials.ParametricPolynomial

data class PointFunction(
    val c: RawVector,
) : ParametricCurveFunction() {
    companion object {
        val zero = PointFunction(
            c = RawVector.zero,
        )
    }

    override fun findDerivative(): ParametricCurveFunction = zero

    override fun toParametricPolynomial(): ParametricPolynomial = ParametricPolynomial.constant(a = c)

    override fun apply(x: Double): RawVector = c
}
