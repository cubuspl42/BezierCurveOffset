package app.utils

import kotlin.math.abs
import kotlin.math.absoluteValue

fun Double.equalsApproximately(
    other: Double,
    epsilon: Double,
): Boolean = abs(this - other) < epsilon

fun Double.equalsZeroApproximately(
    epsilon: Double,
): Boolean = this.absoluteValue < epsilon

infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {
    require(start.isFinite())
    require(endInclusive.isFinite())
    require(step > 0.0) { "Step must be positive, was: $step." }
    val sequence = generateSequence(start) { previous ->
        if (previous == Double.POSITIVE_INFINITY) return@generateSequence null
        val next = previous + step
        if (next > endInclusive) null else next
    }
    return sequence.asIterable()
}

fun linspace(x0: Double, x1: Double, n: Int): Sequence<Double> {
    if (n < 2) throw IllegalArgumentException("n must be at least 2 to include both boundaries")
    val step = (x1 - x0) / (n - 1)
    return generateSequence(0) { it + 1 }.take(n).map { i -> x0 + i * step }
}
