package app.arithmetic.polynomial_formulas

class LinearFormula private constructor(
    val a: Double,
    val b: Double,
) : PolynomialFormula() {
    companion object {
        fun of(
            a: Double,
            b: Double,
        ): PolynomialFormula = when {
            a == 0.0 -> ConstantFormula.of(a = b)
            else -> LinearFormula(a = a, b = b)
        }
    }

    init {
        assert(a != 0.0)
    }

    override fun findRoots(): Set<Double> = setOf(findRoot())

    fun findRoot(): Double = -b / a
}
