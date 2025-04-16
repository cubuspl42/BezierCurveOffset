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
        ): Polynomial = when {
            a == 0.0 -> ConstantPolynomial.of(a = b)
            else -> LinearPolynomial(a = a, b = b)
        }
    }

    init {
        require(a != 0.0)
    }

    override fun apply(x: Double): Double = a * x + b

    override fun solve(
        polynomial: Polynomial,
    ): Set<Double> = polynomial.solveLinear(linearPolynomial = this)

    override fun solveLinear(
        linearPolynomial: LinearPolynomial,
    ): Set<Double> = copy(
        a = a - linearPolynomial.a,
        b = b - linearPolynomial.b,
    ).findRoots()

    override fun solveQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Set<Double> = quadraticPolynomial.solveLinear(
        linearPolynomial = -this,
    )

    override fun solveCubic(
        cubicPolynomial: CubicPolynomial,
    ): Set<Double> = cubicPolynomial.solveLinear(
        linearPolynomial = -this,
    )

    override fun shift(
        deltaY: Double,
    ): Polynomial = copy(
        b = b + deltaY,
    )

    override fun findRoots(): Set<Double> = setOf(findRoot())

    operator fun unaryMinus(): LinearPolynomial = copy(
        a = -a,
        b = -b,
    )

    fun findRoot(): Double = -b / a
}
