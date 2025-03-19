package app

import kotlin.math.sqrt

data class QuadraticFormula(
    val a: Double,
    val b: Double,
    val c: Double,
) {
    val discriminant: Double
        get() = b * b - 4 * a * c

    val hasRealRoots: Boolean
        get() = discriminant >= 0

    /**
     * Find a root with the given sign (-1 or 1), assuming that the discriminant is non-negative
     */
    private fun findRoot(
        sign: Double,
    ): Double = (sign * b + sqrt(discriminant)) / (2 * a)

    /**
     * Find the roots of the quadratic equation, if they exist
     */
    fun findRoots(): Pair<Double, Double>? = when {
        hasRealRoots -> Pair(
            findRoot(sign = -1.0),
            findRoot(sign = 1.0),
        )

        else -> null
    }
}
