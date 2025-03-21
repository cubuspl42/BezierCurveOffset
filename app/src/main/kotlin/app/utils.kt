@file:OptIn(ExperimentalContracts::class)

package app

import kotlin.contracts.ExperimentalContracts

fun <T> assertEqual(a: T, b: T): T {
    assert(a == b)
    return a
}

fun <A, B> Pair<A, A>.map(
    transform: (A) -> B,
): Pair<B, B> = Pair(
    first = transform(first),
    second = transform(second),
)

fun <A> Pair<A, A>.takeIf(
    predicate: (A) -> Boolean,
): Pair<A?, A?> = Pair(
    first = first.takeIf(predicate),
    second = second.takeIf(predicate),
)

fun <A> Pair<A, A>?.toListOrEmpty(): List<A> = this?.toList() ?: emptyList()

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

fun <T> requireEqual(a: T, b: T): T {
    if (a != b) {
        throw IllegalArgumentException("Values were supposed to be equal: $a, $b")
    }
    return a
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
