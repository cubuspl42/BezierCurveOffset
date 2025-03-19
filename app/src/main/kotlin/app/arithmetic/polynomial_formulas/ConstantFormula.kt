package app.arithmetic.polynomial_formulas

class ConstantFormula private constructor(
    val a: Double,
) : PolynomialFormula() {
    companion object {
        fun of(
            a: Double,
        ): PolynomialFormula = ConstantFormula(
            a = a,
        )
    }

    override fun findRoots(): Set<Double> = emptySet()
}
