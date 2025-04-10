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

data class WithPrevious<L, M>(
    val prevElement: L,
    val element: M,
) where M : L

fun <L, M> Iterable<M>.withPrevious(
    outerLeft: L,
): List<WithPrevious<L, M>> where M : L {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val result = mutableListOf<WithPrevious<L, M>>()
    var prev: L = outerLeft
    var current = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()

        result.add(
            WithPrevious(
                prevElement = prev,
                element = current,
            ),
        )

        prev = current
        current = next
    }

    result.add(
        WithPrevious(
            prevElement = prev,
            element = current,
        ),
    )

    return result
}

fun <T : Any> List<T>.withPreviousCyclic(): List<WithPrevious<T, T>> = when {
    isEmpty() -> emptyList()

    else -> withPrevious(
        outerLeft = last(),
    )
}

fun <T : Any> Iterable<T>.withPreviousOrNull(): List<WithPrevious<T?, T>> = withPrevious(
    outerLeft = null,
)

data class WithNext<M, R>(
    val element: M,
    val nextElement: R,
) where M : R

fun <M, R> Iterable<M>.withNext(
    outerRight: R,
): List<WithNext<M, R>> where M : R {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val result = mutableListOf<WithNext<M, R>>()
    var current = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()

        result.add(
            WithNext(
                element = current,
                nextElement = next,
            ),
        )

        current = next
    }

    result.add(
        WithNext(
            element = current,
            nextElement = outerRight,
        ),
    )

    return result
}

fun <T : Any> List<T>.withNextCyclic(): List<WithNext<T, T>> = when {
    isEmpty() -> emptyList()

    else -> withNext(
        outerRight = first(),
    )
}

fun <T : Any> Iterable<T>.withNextOrNull(): List<WithNext<T, T?>> = withNext(
    outerRight = null,
)

@Suppress("BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER")
data class WithNeighbours<L, M, R>(
    val prevElement: L,
    val element: M,
    val nextElement: R,
) where M : L, M : R

@Suppress("BOUNDS_NOT_ALLOWED_IF_BOUNDED_BY_TYPE_PARAMETER")
fun <L, M, R> Iterable<M>.withNeighbours(
    outerLeft: L,
    outerRight: R,
): List<WithNeighbours<L, M, R>> where M : L, M : R {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val result = mutableListOf<WithNeighbours<L, M, R>>()
    var prev: L = outerLeft
    var current = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()

        result.add(
            WithNeighbours(
                prevElement = prev, element = current, nextElement = next
            ),
        )

        prev = current
        current = next
    }

    result.add(
        WithNeighbours(
            prevElement = prev,
            element = current,
            nextElement = outerRight,
        ),
    )

    return result
}

fun <T : Any> Iterable<T>.withNeighboursOrNull(): List<WithNeighbours<T?, T, T?>> = withNeighbours(
    outerLeft = null,
    outerRight = null,
)

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

/**
 * Returns a list containing the results of applying the given [transform] function to each element
 * in this collection, interleaved with the results of applying the [separate] function to each pair
 * of adjacent elements in this collection.
 *
 * The returned list is empty if this collection is empty.
 *
 * @param transform A function that takes an element of the collection and returns a result value.
 * @param separate A function that takes two adjacent elements of the collection and returns a result value.
 * @return A list of transformed values produced by applying the [transform] function to each element
 * in this collection, interleaved with the results of applying the [separate] function to each pair
 * of adjacent elements.
 *
 * Example:
 * ```
 * val numbers = listOf(1, 2, 3)
 * val result = numbers.interleave(
 *     transform = { it * 2 },
 *     separate = { a, b -> a + b }
 * )
 * // result is [2, 3, 4, 5, 6]
 * ```
 */
inline fun <E, R> Iterable<E>.interleave(
    transform: (element: E) -> R,
    separate: (prev: E, next: E) -> R,
): List<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val result = mutableListOf<R>()
    var prev = iterator.next()

    while (iterator.hasNext()) {
        val next = iterator.next()
        result.add(transform(prev))
        result.add(separate(prev, next))
        prev = next
    }

    result.add(transform(prev))

    return result
}

@Suppress("UNCHECKED_CAST")
fun <E> List<*>.elementWiseAs(): List<E> = this.map { it as E }

fun <T> List<T>.mapFirst(transform: (T) -> T): List<T> = when {
    isEmpty() -> emptyList()
    else -> listOf(transform(first())) + drop(1)
}

fun linspace(x0: Double, x1: Double, n: Int): Sequence<Double> {
    if (n < 2) throw IllegalArgumentException("n must be at least 2 to include both boundaries")
    val step = (x1 - x0) / (n - 1)
    return generateSequence(0) { it + 1 }.take(n).map { i -> x0 + i * step }
}

fun <T : Comparable<T>> List<T>.indexOfMax(
    fromIndex: Int = 0,
    toIndex: Int = size - 1,
): Int {
    val (index, _) = withIndex().toList().subList(
        fromIndex = fromIndex,
        toIndex = toIndex,
    ).maxBy { (_, v) -> v }

    return index
}

fun <T, R : Comparable<R>> List<T>.indexOfMaxBy(
    fromIndex: Int = 0,
    toIndex: Int = size,
    selector: (T) -> R,
): Int {
    val (index, _) = withIndex().toList().subList(
        fromIndex = fromIndex,
        toIndex = toIndex,
    ).maxBy { (_, v) -> selector(v) }

    return index
}


operator fun <E> List<E>.component6(): E = this[5]

operator fun <E> List<E>.component7(): E = this[6]

operator fun <E> List<E>.component8(): E = this[7]

operator fun <E> List<E>.component9(): E = this[8]

operator fun <E> List<E>.component10(): E = this[9]

operator fun <E> List<E>.component11(): E = this[10]

operator fun <E> List<E>.component12(): E = this[11]

operator fun <E> List<E>.component13(): E = this[12]

operator fun <E> List<E>.component14(): E = this[13]

operator fun <E> List<E>.component15(): E = this[14]

operator fun <E> List<E>.component16(): E = this[15]

operator fun <E> List<E>.component17(): E = this[16]

operator fun <E> List<E>.component18(): E = this[17]

operator fun <E> List<E>.component19(): E = this[18]

operator fun <E> List<E>.component20(): E = this[19]

operator fun <E> List<E>.component21(): E = this[20]

operator fun <E> List<E>.component22(): E = this[21]

operator fun <E> List<E>.component23(): E = this[22]

operator fun <E> List<E>.component24(): E = this[23]

operator fun <E> List<E>.component25(): E = this[24]

operator fun <E> List<E>.component26(): E = this[25]

operator fun <E> List<E>.component27(): E = this[26]
