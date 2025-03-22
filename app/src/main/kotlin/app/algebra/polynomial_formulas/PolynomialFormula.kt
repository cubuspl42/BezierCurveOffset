package app.algebra.polynomial_formulas

import app.algebra.bezier_binomials.RealFunction

sealed class PolynomialFormula : RealFunction<Double>() {
    abstract fun findRoots(): Set<Double>
}
