package app.arithmetic.polynomial_formulas

import kotlin.math.sqrt

class QuadraticFormula(
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

    override fun findRoots(): Set<Double> {
        val discriminant: Double = b * b - 4 * a * c

        val hasRealRoots: Boolean = discriminant >= 0

        fun findRoot(
            sign: Double,
        ): Double = (sign * b + sqrt(discriminant)) / (2 * a)

        return when {
            hasRealRoots -> setOf(
                findRoot(sign = -1.0),
                findRoot(sign = 1.0),
            )

            else -> emptySet()
        }
    }
}
