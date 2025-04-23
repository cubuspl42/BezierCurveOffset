package app.algebra.implicit_polynomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsWithTolerance
import app.algebra.polynomials.ParametricPolynomial
import app.algebra.polynomials.Polynomial
import app.algebra.polynomials.times
import app.geometry.RawVector

/**
 * A polynomial in the form a2 * x^2 + a1b1 * xy + b2 * y^2 + a1 * x + b1 * y + c
 */
data class ImplicitQuadraticPolynomial(
    val a2: Double,
    val a1b1: Double,
    val b2: Double,
    val a1: Double,
    val b1: Double,
    val c: Double,
) : ImplicitPolynomial() {
    companion object {
        fun of(
            a2: Double,
            a1b1: Double,
            b2: Double,
            a1: Double,
            b1: Double,
            c: Double,
        ): ImplicitQuadraticPolynomial = ImplicitQuadraticPolynomial(
            a2 = a2,
            a1b1 = a1b1,
            b2 = b2,
            a1 = a1,
            b1 = b1,
            c = c,
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is ImplicitQuadraticPolynomial -> false
        !a2.equalsWithTolerance(other.a2, tolerance = tolerance) -> false
        !a1b1.equalsWithTolerance(other.a1b1, tolerance = tolerance) -> false
        !b2.equalsWithTolerance(other.b2, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        !b1.equalsWithTolerance(other.b1, tolerance = tolerance) -> false
        !c.equalsWithTolerance(other.c, tolerance = tolerance) -> false
        else -> true
    }

    operator fun times(
        other: ImplicitLinearPolynomial,
    ): ImplicitCubicPolynomial = ImplicitCubicPolynomial.of(
        a3 = a2 * other.a1,
        a2b1 = a2 * other.b1 + a1b1 * other.a1,
        a1b2 = a1b1 * other.b1 + b2 * other.a1,
        b3 = b2 * other.b1,
        a2 = a2 * other.c + a1 * other.a1,
        a1b1 = a1b1 * other.c + a1 * other.b1 + b1 * other.a1,
        b2 = b2 * other.c + b1 * other.b1,
        a1 = a1 * other.c + c * other.a1,
        b1 = b1 * other.c + c * other.b1,
        c = c * other.c,
    )

    operator fun plus(
        other: ImplicitQuadraticPolynomial,
    ): ImplicitQuadraticPolynomial = ImplicitQuadraticPolynomial(
        a2 = a2 + other.a2,
        a1b1 = a1b1 + other.a1b1,
        b2 = b2 + other.b2,
        a1 = a1 + other.a1,
        b1 = b1 + other.b1,
        c = c + other.c,
    )

    operator fun plus(
        other: ImplicitLinearPolynomial,
    ): ImplicitQuadraticPolynomial = ImplicitQuadraticPolynomial(
        a2 = a2,
        a1b1 = a1b1,
        b2 = b2,
        a1 = a1 + other.a1,
        b1 = b1 + other.b1,
        c = c + other.c,
    )

    operator fun unaryMinus(): ImplicitQuadraticPolynomial = ImplicitQuadraticPolynomial(
        a2 = -a2,
        a1b1 = -a1b1,
        b2 = -b2,
        a1 = -a1,
        b1 = -b1,
        c = -c,
    )

    operator fun minus(
        other: ImplicitQuadraticPolynomial,
    ): ImplicitQuadraticPolynomial = this + (-other)

    operator fun minus(
        other: ImplicitLinearPolynomial,
    ): ImplicitQuadraticPolynomial = this + (-other)

    override fun apply(v: RawVector): Double {
        val x = v.x
        val y = v.y

        return a2 * (x * x) + a1b1 * (x * y) + b2 * (y * y) + a1 * x + b1 * y + c
    }

    override fun put(
        parametricPolynomial: ParametricPolynomial,
    ): Polynomial<*> {
        val x = parametricPolynomial.xFunction
        val y = parametricPolynomial.yFunction

        return a2 * (x * x) + a1b1 * (x * y) + b2 * (y * y) + a1 * x + b1 * y + c
    }

    operator fun minus(
        other: ImplicitCubicPolynomial,
    ): ImplicitCubicPolynomial = this + (-other)

    private operator fun plus(
        implicitCubicPolynomial: ImplicitCubicPolynomial,
    ): ImplicitCubicPolynomial = implicitCubicPolynomial + this
}
