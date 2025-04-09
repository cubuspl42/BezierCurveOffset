package app.algebra

import kotlin.math.abs

interface NumericObject {
    fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean
}

fun Double.equalsWithTolerance(
    other: Double,
    absoluteTolerance: Double,
): Boolean = abs(this - other) <= absoluteTolerance

fun List<NumericObject>.equalsWithTolerance(
    other: List<NumericObject>,
    absoluteTolerance: Double,
): Boolean {
    if (this.size != other.size) return false

    return zip(other).all { (a, b) ->
        a.equalsWithTolerance(b, absoluteTolerance)
    }
}
