package app.algebra

import app.algebra.NumericObject.Tolerance

data class Ratio(
    val nominator: Double,
    val denominator: Double,
) : NumericObject {
    companion object {
        val ZeroByZero = Ratio(
            nominator = 0.0,
            denominator = 0.0,
        )
    }

    val value: Double
        get() = nominator / denominator

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is Ratio -> false
        !nominator.equalsWithTolerance(other.nominator, tolerance = tolerance) -> false
        !denominator.equalsWithTolerance(other.denominator, tolerance = tolerance) -> false
        else -> true
    }
}
