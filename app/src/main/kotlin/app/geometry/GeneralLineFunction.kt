package app.geometry

import app.algebra.bezier_binomials.RealFunction
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
) : RealFunction<Double>() {
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

    override fun apply(x: Double): Double = a * x + b * x + c
}
