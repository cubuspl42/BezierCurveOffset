package app.algebra

import kotlin.math.abs
import kotlin.math.max

interface NumericObject {
    sealed class Tolerance {
        data class Absolute(
            val absoluteTolerance: Double,
        ) : Tolerance() {
            override fun equalsApproximately(
                first: Double,
                second: Double
            ): Boolean = abs(first - second) <= absoluteTolerance
        }

        data class Relative(
            val relativeTolerance: Double,
        ) : Tolerance() {
            init {
                require(relativeTolerance > 0.0 && relativeTolerance < 0.25)
            }

            override fun equalsApproximately(
                first: Double,
                second: Double
            ): Boolean = abs(first - second) <= relativeTolerance * max(abs(first), abs(second))
        }

        abstract fun equalsApproximately(
            first: Double,
            second: Double,
        ): Boolean
    }

    fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Double,
    ): Boolean
}

fun Double.equalsWithTolerance(
    other: Double,
    tolerance: Double,
): Boolean = abs(this - other) <= tolerance

@JvmName("equalsWithToleranceListDouble")
fun List<Double>.equalsWithTolerance(
    other: List<Double>,
    tolerance: Double,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}

@JvmName("equalsWithToleranceListNumericObject")
fun List<NumericObject>.equalsWithTolerance(
    other: List<NumericObject>,
    tolerance: Double,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, tolerance)
    }
}
