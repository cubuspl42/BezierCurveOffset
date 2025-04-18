package app.geometry

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.polynomials.LinearPolynomial

/**
 * A polynomial in the form a1b1 * xy + a1 * x + b1 * y + c
 */
data class ImplicitBilinearPolynomial(
    val a1b1: Double,
    val a1: Double,
    val b1: Double,
    val c: Double,
) : NumericObject {
    companion object {
        fun of(
            a1b1: Double,
            a1: Double,
            b1: Double,
            c: Double,
        ): ImplicitBilinearPolynomial = ImplicitBilinearPolynomial(
            a1b1 = a1b1,
            a1 = a1,
            b1 = b1,
            c = c,
        )

        /**
         * @param ax - the polynomial in the form a1 * x + a0
         * @param by - the polynomial in the form a1' * y + a0'
         *
         * @return The product polynomial px * py
         */
        fun times(
            ax: LinearPolynomial,
            by: LinearPolynomial,
        ): ImplicitBilinearPolynomial = ImplicitBilinearPolynomial.of(
            a1b1 = ax.a1 * by.a1,
            a1 = ax.a1 * by.a0 + ax.a0 * by.a1,
            b1 = ax.a0 * by.a0,
            c = 0.0,
        )
    }

    operator fun times(
        other: ImplicitBilinearPolynomial,
    ): ImplicitBilinearPolynomial = ImplicitBilinearPolynomial.of(
        a1b1 = a1b1 * other.c + a1 * other.b1 + b1 * other.a1,
        a1 = a1 * other.c + c * other.a1,
        b1 = b1 * other.c + c * other.b1,
        c = c * other.c,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Double,
    ): Boolean = when {
        other !is ImplicitBilinearPolynomial -> false
        !a1b1.equalsWithTolerance(other.a1b1, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        !b1.equalsWithTolerance(other.b1, tolerance = tolerance) -> false
        !c.equalsWithTolerance(other.c, tolerance = tolerance) -> false
        else -> true
    }

    operator fun plus(
        other: ImplicitBilinearPolynomial,
    ): ImplicitBilinearPolynomial = ImplicitBilinearPolynomial(
        a1b1 = a1b1 + other.a1b1,
        a1 = a1 + other.a1,
        b1 = b1 + other.b1,
        c = c + other.c,
    )

    operator fun unaryMinus(): ImplicitBilinearPolynomial = ImplicitBilinearPolynomial(
        a1b1 = -a1b1,
        a1 = -a1,
        b1 = -b1,
        c = -c,
    )

    operator fun minus(
        other: ImplicitBilinearPolynomial,
    ): ImplicitBilinearPolynomial = this + (-other)
}
