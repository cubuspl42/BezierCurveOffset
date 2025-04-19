package app.utils.iterable

data class Uncons<T>(
    val firstElement: T,
    val trailingElement: List<T>,
)

fun <T> List<T>.uncons(): Uncons<T>? = firstOrNull()?.let { head ->
    Uncons(
        firstElement = head,
        trailingElement = drop(1),
    )
}

data class Untrail<T>(
    val leadingElements: List<T>,
    val lastElement: T,
)

fun <T> List<T>.untrail(): Untrail<T>? = lastOrNull()?.let { foot ->
    Untrail(
        leadingElements = dropLast(1),
        lastElement = foot,
    )
}

data class Split<T>(
    val leadingElements: List<T>,
    val trailingElements: List<T>,
)

/**
 * Split the list after taking [n] front elements
 */
fun <T> List<T>.splitAfter(n: Int): Split<T> = Split(
    leadingElements = take(n),
    trailingElements = drop(n),
)
