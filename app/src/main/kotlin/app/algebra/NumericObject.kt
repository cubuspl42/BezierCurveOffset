package app.algebra

import app.algebra.NumericObject.Tolerance
import app.equalsApproximately
import kotlin.math.abs
import kotlin.math.max

interface NumericObject {
    sealed class Tolerance {
        data class Absolute(
            val absoluteTolerance: Double,
        ) : Tolerance() {
            override fun equalsApproximately(
                first: Double, second: Double
            ): Boolean = abs(first - second) <= absoluteTolerance
        }

        data class Relative(
            val relativeTolerance: Double,
        ) : Tolerance() {
            init {
                require(relativeTolerance > 0.0 && relativeTolerance < 0.25)
            }

            override fun equalsApproximately(
                first: Double, second: Double
            ): Boolean = abs(first - second) <= relativeTolerance * max(abs(first), abs(second))
        }

        abstract fun equalsApproximately(
            first: Double,
            second: Double,
        ): Boolean
    }

    fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean
}

private fun NumericObject.equalsWithTolerance(
    other: NumericObject,
    absoluteTolerance: Double,
): Boolean = equalsWithTolerance(
    other = other,
    tolerance = Tolerance.Absolute(
        absoluteTolerance = absoluteTolerance,
    ),
)

fun NumericObject.equalsWithNoTolerance(
    other: NumericObject,
): Boolean = equalsWithTolerance(
    other = other,
    absoluteTolerance = 0.0,
)

fun Double.equalsWithTolerance(
    other: Double,
    absoluteTolerance: Double,
): Boolean = absoluteTolerance.equalsApproximately(this, other)

fun Double.equalsWithTolerance(
    other: Double,
    tolerance: Tolerance,
): Boolean = tolerance.equalsApproximately(this, other)

@JvmName("equalsWithToleranceListDouble")
fun List<Double>.equalsWithTolerance(
    other: List<Double>,
    tolerance: Tolerance,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}

@JvmName("equalsWithToleranceListNumericObject")
fun List<NumericObject>.equalsWithTolerance(
    other: List<NumericObject>,
    tolerance: Tolerance,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}
