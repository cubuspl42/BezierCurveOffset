package app.geometry

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsWithTolerance
import app.algebra.polynomials.LinearPolynomial
import app.algebra.polynomials.ParametricPolynomial
import app.algebra.polynomials.Polynomial
import app.algebra.polynomials.times

/**
 * A polynomial in the form a1 * x + b1 * y + c
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
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is ImplicitLinearPolynomial -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        !b1.equalsWithTolerance(other.b1, tolerance = tolerance) -> false
        !c.equalsWithTolerance(other.c, tolerance = tolerance) -> false
        else -> true
    }
}

operator fun Double.times(
    other: ImplicitLinearPolynomial,
): ImplicitLinearPolynomial = other * this
