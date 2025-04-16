package app.algebra.polynomials

import app.algebra.bezier_binomials.RealFunction

sealed class Polynomial : RealFunction<Double>() {
    fun solveFor(
        y: Double,
    ): Set<Double> = (this - y).findRoots()

    abstract operator fun plus(
        constant: Double,
    ): Polynomial

    operator fun minus(
        constant: Double,
    ): Polynomial = this + (-constant)

    abstract fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial

    abstract fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial

    abstract fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial

    abstract operator fun plus(
        other: Polynomial,
    ): Polynomial

    operator fun minus(
        other: Polynomial,
    ): Polynomial = other + (-other)

    abstract operator fun unaryMinus(): Polynomial

    abstract operator fun times(factor: Double): Polynomial

    abstract fun findRoots(): Set<Double>
}

operator fun Double.times(
    polynomial: Polynomial,
): Polynomial = polynomial * this