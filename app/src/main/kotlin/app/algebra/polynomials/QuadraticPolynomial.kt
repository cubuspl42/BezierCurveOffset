package app.algebra.polynomials

import kotlin.math.sqrt

class QuadraticPolynomial private constructor(
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
