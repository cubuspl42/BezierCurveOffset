package app.algebra.polynomials

class LinearPolynomial private constructor(
    val a: Double,
    val b: Double,
) : Polynomial() {
    companion object {
        fun of(
            a: Double,
            b: Double,
        ): Polynomial = when {
            a == 0.0 -> ConstantPolynomial.of(a = b)
            else -> LinearPolynomial(a = a, b = b)
        }
    }

    init {
        require(a != 0.0)
    }

    override fun apply(x: Double): Double = a * x + b

    override fun findRoots(): Set<Double> = setOf(findRoot())

    fun findRoot(): Double = -b / a
}
