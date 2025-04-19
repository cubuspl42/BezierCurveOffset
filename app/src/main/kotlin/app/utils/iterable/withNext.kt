package app.utils.iterable

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
        result.add(WithNext(current, next))
        current = next
    }

    result.add(WithNext(current, outerRight))
    return result
}

fun <T : Any> List<T>.withNextCyclic(): List<WithNext<T, T>> = when {
    isEmpty() -> emptyList()
    else -> withNext(first())
}

fun <T : Any> Iterable<T>.withNextOrNull(): List<WithNext<T, T?>> = withNext(null)

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
