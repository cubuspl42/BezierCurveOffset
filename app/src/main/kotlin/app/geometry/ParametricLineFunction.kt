package app.geometry

import app.algebra.bezier_binomials.RealFunction
import app.algebra.polynomials.ParametricPolynomial

/**
 * Represents a line in 2D space in parametric form:
 * s + d * t
 *
 * Given a t-value, it returns the point on the line at that t-value.
 */
data class ParametricLineFunction(
    val s: RawVector,
    val d: RawVector,
) : RealFunction<RawVector>() {
    override fun apply(x: Double): RawVector = s + d * x

    fun toParametricPolynomial() = ParametricPolynomial.linear(
        a = d,
        b = s,
    )

    fun toGeneralLineFunction(): GeneralLineFunction = GeneralLineFunction(
        a = d.y,
        b = -d.x,
        c = -(d.y * s.x + -d.x * s.y),
    )

    /**
     * Solve the equation s + d * t = s' + d' * t' for t
     */
    fun solve(
        other: ParametricLineFunction,
    ): Double? = other.toGeneralLineFunction().put(
        toParametricPolynomial(),
    ).findRoots().singleOrNull()
}
