package app.geometry

import app.algebra.bezier_binomials.ParametricCurveFunction
import app.algebra.polynomials.ParametricPolynomial

/**
 * Represents a line in 2D space in parametric form: p = s + d * t
 *
 * Given a t-value, it returns the point on the line at that t-value.
 */
data class ParametricLineFunction(
    val d: RawVector,
    val s: RawVector,
) : ParametricCurveFunction() {
    override fun findDerivative(): ParametricCurveFunction = PointFunction(
        c = d,
    )

    override fun apply(x: Double): RawVector = s + d * x

    override fun toParametricPolynomial() = ParametricPolynomial.linear(
        a = d,
        b = s,
    )

    fun toGeneralLineFunction(): ImplicitLinearPolynomial = ImplicitLinearPolynomial(
        a1 = d.y,
        b1 = -d.x,
        c = d.cross(s),
    )

    /**
     * Solve the equation s + d * t = s' + d' * t' for t
     */
    fun solve(
        other: ParametricLineFunction,
    ): Double? {
        val glf = other.toGeneralLineFunction()
        val pp = toParametricPolynomial()
        val finalP = glf.put(pp)
        return finalP.findRoots().singleOrNull()
    }

    /**
     * Solve the equation s + d * t = p for t
     */
    fun solve(
        p: RawVector,
    ): Double? = when {
        d.x != 0.0 -> (p.x - s.x) / d.x
        d.y != 0.0 -> (p.y - s.y) / d.y
        else -> null
    }
}
