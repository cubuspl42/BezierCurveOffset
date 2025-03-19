package app

sealed class PolynomialFormula {
    abstract fun findRoots(): Set<Double>
}
