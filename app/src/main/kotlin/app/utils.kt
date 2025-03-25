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

fun <T : Comparable<T>> Iterable<T>.isSorted(): Boolean = mapWithNext { a, b -> a <= b }.all { it }

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

/**
 * Returns a list containing the results of applying the given [transform] function
 * to an each pair of two adjacent elements in this collection.
 *
 * The returned list is empty if this collection contains less than two elements.
 *
 */
inline fun <T, R> Iterable<T>.mapWithNext(transform: (a: T, b: T) -> R): List<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()
    val result = mutableListOf<R>()
    var current = iterator.next()
    while (iterator.hasNext()) {
        val next = iterator.next()
        result.add(transform(current, next))
        current = next
    }
    return result
}

/**
 * Returns a list containing the results of applying the given [transform] function
 * to each pair of two adjacent elements in this collection, and also applies the
 * [transform] function to the last element of this collection paired with the given [rightEdge].
 *
 * The returned list is empty if this collection is empty.
 *
 * @param rightEdge The element to be paired with the last element of the collection to form the final pair.
 * @param transform A function that takes two parameters: the first is an element of the collection,
 * and the second is the next element (or [rightEdge] for the last element) to transform into a result value.
 *
 * @return A list of transformed values produced by applying the [transform] function to
 * each pair of adjacent elements in this collection, and to the last element paired with [rightEdge].
 *
 * @param T The type of elements in the collection and the type of the [rightEdge].
 * @param U A subtype of [T] that represents the actual type of elements in the collection.
 * @param R The type of results produced by the [transform] function.
 *
 * Example:
 * ```
 * val numbers = listOf(1, 2, 3)
 * val result = numbers.zipWithNext(0) { a, b -> a + b }
 * // result is [3, 5, 3]
 * ```
 */
inline fun <T, U : T, R> Iterable<U>.mapWithNext(
    rightEdge: T,
    transform: (a: U, b: T) -> R,
): List<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val result = mutableListOf<R>()
    var current = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()
        result.add(transform(current, next))
        current = next
    }

    result.add(transform(current, rightEdge))

    return result
}

data class WithNeighbours<T : Any>(
    val prev: T?,
    val a: T,
    val next: T?,
)

fun <T : Any> Iterable<T>.withNeighbours(): List<WithNeighbours<T>> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val result = mutableListOf<WithNeighbours<T>>()
    var prev: T? = null
    var current = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()

        result.add(
            WithNeighbours(
                prev = prev,
                a = current,
                next = next
            ),
        )

        prev = current
        current = next
    }

    result.add(
        WithNeighbours(
            prev = prev,
            a = current,
            next = null
        ),
    )

    return result
}

inline fun <T : Any, U : T, R> Iterable<U>.mapWithNeighbours(
    transform: (prev: T?, a: U, next: T?) -> R,
): List<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val result = mutableListOf<R>()
    var prev: T? = null
    var current = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()
        result.add(transform(prev, current, next))
        prev = current
        current = next
    }

    result.add(transform(prev, current, null))

    return result
}
