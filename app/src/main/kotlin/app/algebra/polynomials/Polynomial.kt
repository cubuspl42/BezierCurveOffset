package app.algebra.polynomials

import app.algebra.bezier_binomials.RealFunction

sealed class Polynomial : RealFunction<Double>() {
    fun solveFor(
        y: Double,
    ): Set<Double> = shift(deltaY = -y).findRoots()

    abstract fun shift(deltaY: Double): Polynomial

    abstract fun findRoots(): Set<Double>
}
