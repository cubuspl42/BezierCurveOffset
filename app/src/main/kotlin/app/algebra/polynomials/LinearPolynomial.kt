package app.algebra.polynomials

@Suppress("DataClassPrivateConstructor")
data class LinearPolynomial private constructor(
    val a: Double,
    val b: Double,
) : Polynomial() {
    companion object {
        fun of(
            a: Double,
            b: Double,
        ): Polynomial? = when {
            a == 0.0 -> null
            else -> LinearPolynomial(a = a, b = b)
        }
    }

    init {
        require(a != 0.0)
    }

    override fun apply(x: Double): Double = a * x + b

    override fun shift(
        deltaY: Double,
    ): Polynomial = copy(
        b = b + deltaY,
    )

    override fun findRoots(): Set<Double> = setOf(findRoot())

    fun findRoot(): Double = -b / a
}
