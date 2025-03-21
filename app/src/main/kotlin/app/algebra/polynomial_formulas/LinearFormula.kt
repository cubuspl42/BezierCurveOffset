package app.algebra.polynomial_formulas

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
        require(a != 0.0)
    }

    override fun apply(x: Double): Double = a * x + b

    override fun findRoots(): Set<Double> = setOf(findRoot())

    fun findRoot(): Double = -b / a
}
