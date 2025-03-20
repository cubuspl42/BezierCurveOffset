package app.algebra.polynomial_formulas

import kotlin.math.sqrt

class QuadraticFormula private constructor(
    val a: Double,
    val b: Double,
    val c: Double,
) : PolynomialFormula() {
    companion object {
        fun of(
            a: Double,
            b: Double,
            c: Double,
        ): PolynomialFormula = when {
            a == 0.0 -> LinearFormula.of(a = b, b = c)
            else -> QuadraticFormula(a = a, b = b, c = c)
        }
    }

    init {
        assert(a != 0.0)
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
