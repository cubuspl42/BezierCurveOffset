package app.arithmetic.polynomial_formulas

sealed class PolynomialFormula {
    abstract fun findRoots(): Set<Double>
}
