package app.algebra.polynomials

import app.algebra.bezier_binomials.RealFunction

sealed class Polynomial : RealFunction<Double>() {
    abstract fun findRoots(): Set<Double>
}
