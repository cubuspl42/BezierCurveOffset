package app

class ConstantFormula(
    val a: Double,
) : PolynomialFormula() {
    override fun findRoots(): Set<Double> = emptySet()
}
