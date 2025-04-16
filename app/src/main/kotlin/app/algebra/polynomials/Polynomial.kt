package app.algebra.polynomials

import app.algebra.bezier_binomials.RealFunction

sealed class Polynomial : RealFunction<Double>() {
    fun solveFor(
        y: Double,
    ): Set<Double> = shift(deltaY = -y).findRoots()

    /**
     * Solve ax^n + ... = a'x^n' ...
     */
    abstract fun solve(
        polynomial: Polynomial,
    ): Set<Double>

    abstract fun solveLinear(
        linearPolynomial: LinearPolynomial,
    ): Set<Double>

    abstract fun solveQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Set<Double>

    abstract fun solveCubic(
        cubicPolynomial: CubicPolynomial,
    ): Set<Double>

    abstract fun shift(deltaY: Double): Polynomial

    abstract fun findRoots(): Set<Double>
}
