package app

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

data class PartitioningResult<T>(
    val leftPart: List<T>,
    val medianValue: T,
    val rightPart: List<T>,
)

fun <T : Comparable<T>> Iterable<T>.isSorted(): Boolean = zipWithNext { a, b -> a <= b }.all { it }

fun <T : Comparable<T>> List<T>.partitionSorted(): PartitioningResult<T>? {
    assert(isSorted())

    if (isEmpty()) return null

    val medianIndex = size / 2
    val medianValue = this[medianIndex]

    return PartitioningResult(
        leftPart = subList(0, medianIndex),
        medianValue = medianValue,
        rightPart = subList(medianIndex + 1, size),
    )
}

data class Uncons<T>(
    val head: T,
    val tail: List<T>,
)

fun <T> List<T>.uncons(): Uncons<T>? = firstOrNull()?.let { head ->
    Uncons(
        head = head,
        tail = drop(1),
    )
}
