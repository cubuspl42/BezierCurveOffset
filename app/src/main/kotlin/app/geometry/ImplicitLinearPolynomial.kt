package app.geometry

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.polynomials.LinearPolynomial
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
data class ImplicitLinearPolynomial(
    val a2: Double,
    val a1: Double,
    val a0: Double,
) : NumericObject {
    companion object {
        /**
         * @param px - the polynomial in the form a0 * x + b0
         * @param py - the polynomial in the form a1 * y + b1
         *
         * @return The product polynomial px * py
         */
        fun times(
            px: LinearPolynomial,
            py: LinearPolynomial,
        ): ImplicitLinearPolynomial {
            TODO()
        }

        fun times(
            l0: ImplicitLinearPolynomial,
            l1: ImplicitLinearPolynomial,
            l2: ImplicitLinearPolynomial,
        ): CubedGeneralLineFunction {
            val a3 = l0.a2 * l1.a2 * l2.a2
            val a2b1 = l0.a2 * l1.a2 * l2.a1 + l0.a2 * l1.a1 * l2.a2 + l0.a1 * l1.a2 * l2.a2
            val a1b2 = l0.a2 * l1.a1 * l2.a1 + l0.a1 * l1.a2 * l2.a1 + l0.a1 * l1.a1 * l2.a2
            val b3 = l0.a1 * l1.a1 * l2.a1
            val a2 = l0.a2 * l1.a2 * l2.a0 + l0.a2 * l1.a0 * l2.a2 + l0.a0 * l1.a2 * l2.a2
            val a1b1 =
                l0.a2 * l1.a1 * l2.a0 + l0.a2 * l1.a0 * l2.a1 + l0.a1 * l1.a2 * l2.a0 + l0.a1 * l1.a0 * l2.a2 + l0.a0 * l1.a2 * l2.a1 + l0.a0 * l1.a1 * l2.a2
            val b2 = l0.a1 * l1.a1 * l2.a0 + l0.a1 * l1.a0 * l2.a1 + l0.a0 * l1.a1 * l2.a1
            val a1 = l0.a2 * l1.a0 * l2.a0 + l0.a0 * l1.a2 * l2.a0 + l0.a0 * l1.a0 * l2.a2
            val b1 = l0.a1 * l1.a0 * l2.a0 + l0.a0 * l1.a1 * l2.a0 + l0.a0 * l1.a0 * l2.a1
            val c = l0.a0 * l1.a0 * l2.a0

            return CubedGeneralLineFunction(
                a3 = a3,
                a2b1 = a2b1,
                a1b2 = a1b2,
                b3 = b3,
                a2 = a2,
                a1b1 = a1b1,
                b2 = b2,
                a1 = a1,
                b1 = b1,
                c = c,
            )
        }
    }

    /**
     * A bi-linear polynomial in the form...
     *
     * a3 * x^3 +
     * a2b1 * x^2y +
     * a1b2 * xy^2 +
     * b3 * y^3 +
     * a2 * x^2 +
     * a1b1 * xy +
     * b2 * y^2 +
     * a1 * x +
     * b1 * y +
     * c
     */
    data class CubedGeneralLineFunction(
        val a3: Double,
        val a2b1: Double,
        val a1b2: Double,
        val b3: Double,
        val a2: Double,
        val a1b1: Double,
        val b2: Double,
        val a1: Double,
        val b1: Double,
        val c: Double,
    ) {
        operator fun plus(
            other: CubedGeneralLineFunction,
        ): CubedGeneralLineFunction = CubedGeneralLineFunction(
            a3 = a3 + other.a3,
            a2b1 = a2b1 + other.a2b1,
            a1b2 = a1b2 + other.a1b2,
            b3 = b3 + other.b3,
            a2 = a2 + other.a2,
            a1b1 = a1b1 + other.a1b1,
            b2 = b2 + other.b2,
            a1 = a1 + other.a1,
            b1 = b1 + other.b1,
            c = c + other.c,
        )

        operator fun unaryMinus(): CubedGeneralLineFunction = CubedGeneralLineFunction(
            a3 = -a3,
            a2b1 = -a2b1,
            a1b2 = -a1b2,
            b3 = -b3,
            a2 = -a2,
            a1b1 = -a1b1,
            b2 = -b2,
            a1 = -a1,
            b1 = -b1,
            c = -c,
        )

        operator fun minus(
            other: CubedGeneralLineFunction,
        ): CubedGeneralLineFunction = this + (-other)

        fun put(
            parametricPolynomial: ParametricPolynomial,
        ): Polynomial {
            val x = parametricPolynomial.xFunction
            val y = parametricPolynomial.yFunction

            return a3 * (x * x * x) + a2b1 * (x * x * y) + a1b2 * (x * y * y) + b3 * (y * y * y) + a2 * (x * x) + a1b1 * (x * y) + b2 * (y * y) + a1 * x + b1 * y + c
        }
    }

    operator fun plus(
        other: ImplicitLinearPolynomial,
    ): ImplicitLinearPolynomial = ImplicitLinearPolynomial(
        a2 = a2 + other.a2,
        a1 = a1 + other.a1,
        a0 = a0 + other.a0,
    )

    operator fun unaryMinus(): ImplicitLinearPolynomial = ImplicitLinearPolynomial(
        a2 = -a2,
        a1 = -a1,
        a0 = -a0,
    )

    operator fun minus(
        other: ImplicitLinearPolynomial,
    ): ImplicitLinearPolynomial = this + (-other)

    fun put(
        x: Polynomial,
        y: Polynomial,
    ): Polynomial = a2 * x + a1 * y + a0

    fun put(
        p: ParametricPolynomial,
    ): Polynomial = put(
        x = p.xFunction,
        y = p.yFunction,
    )

    fun apply(p: RawVector): Double = a2 * p.x + a1 * p.y + a0

    override fun equalsWithTolerance(
        other: NumericObject, absoluteTolerance: Double
    ): Boolean = when {
        other !is ImplicitLinearPolynomial -> false
        !a2.equalsWithTolerance(other.a2, absoluteTolerance = absoluteTolerance) -> false
        !a1.equalsWithTolerance(other.a1, absoluteTolerance = absoluteTolerance) -> false
        !a0.equalsWithTolerance(other.a0, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }
}
