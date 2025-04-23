package app.utils.iterable

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
        result.add(WithNeighbours(prev, current, next))
        prev = current
        current = next
    }

    result.add(WithNeighbours(prev, current, outerRight))
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
