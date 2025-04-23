package app.utils

data class OpenFloatingPointRange(
    val startExclusive: Double,
    val endExclusive: Double,
)

infix fun Double.untilOpen(endExclusive: Double) = OpenFloatingPointRange(
    startExclusive = this,
    endExclusive = endExclusive,
)

operator fun OpenFloatingPointRange.contains(
    value: Double,
): Boolean = startExclusive < value && value < endExclusive

val ClosedFloatingPointRange<Double>.open: OpenFloatingPointRange
    get() = OpenFloatingPointRange(
        startExclusive = this.start,
        endExclusive = this.endInclusive,
    )
