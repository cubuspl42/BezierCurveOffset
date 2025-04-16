package app.algebra.polynomials

import kotlin.math.sqrt

@Suppress("DataClassPrivateConstructor")
data class QuadraticPolynomial private constructor(
    val a: Double,
    val b: Double,
    val c: Double,
) : Polynomial() {
    companion object {
        fun of(
            a: Double,
            b: Double,
            c: Double,
        ): Polynomial = when {
            a == 0.0 -> LinearPolynomial.of(a = b, b = c)
            else -> QuadraticPolynomial(a = a, b = b, c = c)
        }
    }

    init {
        require(a != 0.0)
    }

    override fun apply(x: Double): Double = a * x * x + b * x + c

    override fun solve(
        polynomial: Polynomial,
    ): Set<Double> = polynomial.solveQuadratic(quadraticPolynomial = this)

    override fun solveLinear(
        linearPolynomial: LinearPolynomial,
    ): Set<Double> = copy(
        b = b - linearPolynomial.a,
        c = c - linearPolynomial.b,
    ).findRoots()

    override fun solveQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Set<Double> = copy(
        a = a - quadraticPolynomial.a,
        b = b - quadraticPolynomial.b,
        c = c - quadraticPolynomial.c,
    ).findRoots()

    override fun solveCubic(
        cubicPolynomial: CubicPolynomial,
    ): Set<Double> = cubicPolynomial.solveQuadratic(
        quadraticPolynomial = -this,
    )

    operator fun unaryMinus(): QuadraticPolynomial = copy(
        a = -a,
        b = -b,
        c = -c,
    )

    override fun shift(
        deltaY: Double,
    ): Polynomial = copy(
        c = c + deltaY,
    )

    override fun findRoots(): Set<Double> {
        val discriminant: Double = b * b - 4 * a * c

        fun buildRoot(
            sign: Double,
        ): Double = (-b + sign * sqrt(discriminant)) / (2 * a)

        return when {
            discriminant >= 0 -> setOf(
                buildRoot(sign = -1.0),
                buildRoot(sign = 1.0),
            )

            else -> emptySet()
        }
    }
}
