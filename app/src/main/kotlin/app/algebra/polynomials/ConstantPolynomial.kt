package app.algebra.polynomials

@Suppress("DataClassPrivateConstructor")
data class ConstantPolynomial private constructor(
    val a: Double,
) : Polynomial() {
    companion object {
        fun of(
            a: Double,
        ): ConstantPolynomial = ConstantPolynomial(a = a)
    }

    override fun apply(x: Double): Double = a

    override fun solve(
        polynomial: Polynomial,
    ): Set<Double> = solveFor(
        y = a,
    )

    override fun solveLinear(
        linearPolynomial: LinearPolynomial,
    ): Set<Double> = linearPolynomial.solveFor(y = -a)

    override fun solveQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Set<Double> = quadraticPolynomial.solveFor(y = -a)

    override fun solveCubic(
        cubicPolynomial: CubicPolynomial,
    ): Set<Double> = cubicPolynomial.solveFor(-a)

    override fun shift(
        deltaY: Double,
    ): Polynomial = copy(
        a = a + deltaY,
    )

    override fun findRoots(): Set<Double> = emptySet()

    operator fun unaryMinus(): ConstantPolynomial = copy(
        a = -a,
    )
}
