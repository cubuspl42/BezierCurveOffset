package app.arithmetic.polynomial_formulas

class ConstantFormula(
    val a: Double,
) : PolynomialFormula() {
    override fun findRoots(): Set<Double> = emptySet()
}
