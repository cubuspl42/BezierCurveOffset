package app.algebra.euclidean

import app.algebra.NumericObject
import app.algebra.euclidean.bezier_binomials.ParametricCurveFunction
import app.algebra.implicit_polynomials.ImplicitLinearPolynomial
import app.algebra.polynomials.ParametricPolynomial
import app.geometry.PointFunction
import app.geometry.RawVector

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
        a1 = d,
        a0 = s,
    )

    override fun implicitize(): ImplicitLinearPolynomial = ImplicitLinearPolynomial(
        a1 = d.y,
        b1 = -d.x,
        c = d.cross(s),
    )

    /**
     * Solve the intersection of two lines
     *
     * @return The intersection t-value for this curve
     */
    fun solveIntersection(
        other: ParametricLineFunction,
    ): Double? = solveIntersections(other).singleOrNull()

    /**
     * Solve the equation s + d * t = p for t
     */
    override fun solvePoint(
        p: RawVector,
        tolerance: NumericObject.Tolerance,
    ): Double? = when {
        d.x != 0.0 -> (p.x - s.x) / d.x
        d.y != 0.0 -> (p.y - s.y) / d.y
        else -> null
    }
}
