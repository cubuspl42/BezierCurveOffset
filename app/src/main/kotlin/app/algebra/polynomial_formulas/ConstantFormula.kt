package app.algebra.polynomial_formulas

class ConstantFormula private constructor(
    val a: Double,
) : PolynomialFormula() {
    companion object {
        fun of(
            a: Double,
        ): ConstantFormula = ConstantFormula(
            a = a,
        )
    }

    override fun apply(x: Double): Double = a

    override fun findRoots(): Set<Double> = emptySet()
}
