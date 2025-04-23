package app.geometry

import app.algebra.NumericObject
import app.algebra.euclidean.bezier_binomials.ParametricCurveFunction
import app.algebra.implicit_polynomials.ImplicitPolynomial
import app.algebra.polynomials.ParametricPolynomial

data class PointFunction(
    val c: RawVector,
) : ParametricCurveFunction() {
    companion object {
        val zero = PointFunction(
            c = RawVector.zero,
        )
    }

    override fun solvePoint(
        p: RawVector, tolerance: NumericObject.Tolerance
    ): Double? = when {
        c.equalsWithTolerance(p, tolerance = tolerance) -> 0.0
        else -> null
    }

    override fun implicitize(): ImplicitPolynomial {
        TODO("Not yet implemented")
    }

    override fun findDerivative(): ParametricCurveFunction = zero

    override fun toParametricPolynomial(): ParametricPolynomial = ParametricPolynomial.constant(a = c)

    override fun apply(x: Double): RawVector = c
}
