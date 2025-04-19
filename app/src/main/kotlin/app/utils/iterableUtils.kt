package app.utils


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
