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
data class GeneralLineFunction(
    val a: Double,
    val b: Double,
    val c: Double,
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
        ): GeneralLineFunction {
            TODO()
        }

        fun times(
            l0: GeneralLineFunction,
            l1: GeneralLineFunction,
            l2: GeneralLineFunction,
        ): CubedGeneralLineFunction {
            val a3 = l0.a * l1.a * l2.a
            val a2b1 = l0.a * l1.a * l2.b + l0.a * l1.b * l2.a + l0.b * l1.a * l2.a
            val a1b2 = l0.a * l1.b * l2.b + l0.b * l1.a * l2.b + l0.b * l1.b * l2.a
            val b3 = l0.b * l1.b * l2.b
            val a2 = l0.a * l1.a * l2.c + l0.a * l1.c * l2.a + l0.c * l1.a * l2.a
            val a1b1 =
                l0.a * l1.b * l2.c + l0.a * l1.c * l2.b + l0.b * l1.a * l2.c + l0.b * l1.c * l2.a + l0.c * l1.a * l2.b + l0.c * l1.b * l2.a
            val b2 = l0.b * l1.b * l2.c + l0.b * l1.c * l2.b + l0.c * l1.b * l2.b
            val a1 = l0.a * l1.c * l2.c + l0.c * l1.a * l2.c + l0.c * l1.c * l2.a
            val b1 = l0.b * l1.c * l2.c + l0.c * l1.b * l2.c + l0.c * l1.c * l2.b
            val c = l0.c * l1.c * l2.c

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
        other: GeneralLineFunction,
    ): GeneralLineFunction = GeneralLineFunction(
        a = a + other.a,
        b = b + other.b,
        c = c + other.c,
    )

    operator fun unaryMinus(): GeneralLineFunction = GeneralLineFunction(
        a = -a,
        b = -b,
        c = -c,
    )

    operator fun minus(
        other: GeneralLineFunction,
    ): GeneralLineFunction = this + (-other)

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
