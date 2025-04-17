package app.geometry

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.polynomials.LinearPolynomial
import app.algebra.polynomials.ParametricPolynomial
import app.algebra.polynomials.Polynomial
import app.algebra.polynomials.times

/**
 * A polynomial in the form a1 * x + b1 * y + c, describing a line in 2D space.
 *
 * Given a point, it evaluates to 0 if the point is on the line,
 * positive if above, and negative if below (the meaning of "above" and "below"
 * depend on the line's direction).
 */
data class ImplicitLinearPolynomial(
    val a1: Double,
    val b1: Double,
    val c: Double,
) : NumericObject {
    companion object {
        fun of(
            a1: Double,
            b1: Double,
            c: Double,
        ): ImplicitLinearPolynomial = ImplicitLinearPolynomial(
            a1 = a1,
            b1 = b1,
            c = c,
        )

        // TODO: Nuke
        fun times(
            l0: ImplicitLinearPolynomial,
            l1: ImplicitLinearPolynomial,
            l2: ImplicitLinearPolynomial,
        ): ImplicitCubicPolynomial {
            val a3 = l0.a1 * l1.a1 * l2.a1
            val a2b1 = l0.a1 * l1.a1 * l2.b1 + l0.a1 * l1.b1 * l2.a1 + l0.b1 * l1.a1 * l2.a1
            val a1b2 = l0.a1 * l1.b1 * l2.b1 + l0.b1 * l1.a1 * l2.b1 + l0.b1 * l1.b1 * l2.a1
            val b3 = l0.b1 * l1.b1 * l2.b1
            val a2 = l0.a1 * l1.a1 * l2.c + l0.a1 * l1.c * l2.a1 + l0.c * l1.a1 * l2.a1
            val a1b1 =
                l0.a1 * l1.b1 * l2.c + l0.a1 * l1.c * l2.b1 + l0.b1 * l1.a1 * l2.c + l0.b1 * l1.c * l2.a1 + l0.c * l1.a1 * l2.b1 + l0.c * l1.b1 * l2.a1
            val b2 = l0.b1 * l1.b1 * l2.c + l0.b1 * l1.c * l2.b1 + l0.c * l1.b1 * l2.b1
            val a1 = l0.a1 * l1.c * l2.c + l0.c * l1.a1 * l2.c + l0.c * l1.c * l2.a1
            val b1 = l0.b1 * l1.c * l2.c + l0.c * l1.b1 * l2.c + l0.c * l1.c * l2.b1
            val c = l0.c * l1.c * l2.c

            return ImplicitCubicPolynomial(
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

        /**
         * @param py - the polynomial in the form a1 * y + a0
         * @param px - the polynomial in the form a1' * x + a0'
         *
         * @return ay - bx
         */
        fun minus(
            py: LinearPolynomial,
            px: LinearPolynomial,
        ): ImplicitLinearPolynomial = ImplicitLinearPolynomial.of(
            a1 = -px.a1,
            b1 = py.a1,
            c = py.a0 - px.a0,
        )
    }

    operator fun plus(
        c: Double,
    ): ImplicitLinearPolynomial = ImplicitLinearPolynomial(
        a1 = a1,
        b1 = b1,
        c = this.c + c,
    )

    operator fun plus(
        other: ImplicitLinearPolynomial,
    ): ImplicitLinearPolynomial = ImplicitLinearPolynomial(
        a1 = a1 + other.a1,
        b1 = b1 + other.b1,
        c = c + other.c,
    )

    operator fun unaryMinus(): ImplicitLinearPolynomial = ImplicitLinearPolynomial(
        a1 = -a1,
        b1 = -b1,
        c = -c,
    )

    operator fun minus(
        other: ImplicitLinearPolynomial,
    ): ImplicitLinearPolynomial = this + (-other)

    operator fun times(
        s: Double,
    ) : ImplicitLinearPolynomial = ImplicitLinearPolynomial(
        a1 = a1 * s,
        b1 = b1 * s,
        c = c * s,
    )

    operator fun times(
        other: ImplicitLinearPolynomial,
    ): ImplicitQuadraticPolynomial = ImplicitQuadraticPolynomial.of(
        a2 = a1 * other.a1,
        a1b1 = a1 * other.b1 + b1 * other.a1,
        b2 = b1 * other.b1,
        a1 = a1 * other.c + c * other.a1,
        b1 = b1 * other.c + c * other.b1,
        c = c * other.c,
    )

    operator fun times(
        other: ImplicitQuadraticPolynomial,
    ): ImplicitCubicPolynomial = other * this

    fun put(
        x: Polynomial,
        y: Polynomial,
    ): Polynomial = a1 * x + b1 * y + c

    fun put(
        p: ParametricPolynomial,
    ): Polynomial = put(
        x = p.xFunction,
        y = p.yFunction,
    )

    fun apply(p: RawVector): Double = a1 * p.x + b1 * p.y + c

    override fun equalsWithTolerance(
        other: NumericObject, absoluteTolerance: Double
    ): Boolean = when {
        other !is ImplicitLinearPolynomial -> false
        !a1.equalsWithTolerance(other.a1, absoluteTolerance = absoluteTolerance) -> false
        !b1.equalsWithTolerance(other.b1, absoluteTolerance = absoluteTolerance) -> false
        !c.equalsWithTolerance(other.c, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }
}

operator fun Double.times(
    other: ImplicitLinearPolynomial,
): ImplicitLinearPolynomial = other * this
