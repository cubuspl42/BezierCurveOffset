package app.arithmetic.polynomial_formulas

import app.arithmetic.bezier_formulas.RealFunction

sealed class PolynomialFormula : RealFunction<Double>() {
    abstract fun findRoots(): Set<Double>
}
