package app.algebra.polynomials

class ConstantPolynomial private constructor(
    val a: Double,
) : Polynomial() {
    companion object {
        fun of(
            a: Double,
        ): ConstantPolynomial = ConstantPolynomial(
            a = a,
        )
    }

    override fun apply(x: Double): Double = a

    override fun findRoots(): Set<Double> = emptySet()
}
