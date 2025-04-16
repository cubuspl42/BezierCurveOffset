package app.geometry

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.polynomials.ParametricPolynomial
import app.algebra.polynomials.Polynomial
import app.algebra.polynomials.times

/**
 * Represents expression ax + by + c describing a line in 2D space.
 *
 * Given a point, it evaluates to 0 if the point is on the line,
 * positive if above, and negative if below (the meaning of "above" and "below"
 * depend on the line's direction.
 */
data class GeneralLineFunction(
    val a: Double,
    val b: Double,
    val c: Double,
) : NumericObject {
    fun put(
        x: Polynomial,
        y: Polynomial,
    ): Polynomial = a * x + b * y + c

    fun put(
        p: ParametricPolynomial,
    ): Polynomial = put(
        x = p.xFunction,
        y = p.yFunction,
    )

    fun apply(p: RawVector): Double = a * p.x + b * p.y + c

    override fun equalsWithTolerance(
        other: NumericObject, absoluteTolerance: Double
    ): Boolean = when {
        other !is GeneralLineFunction -> false
        !a.equalsWithTolerance(other.a, absoluteTolerance = absoluteTolerance) -> false
        !b.equalsWithTolerance(other.b, absoluteTolerance = absoluteTolerance) -> false
        !c.equalsWithTolerance(other.c, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }
}
