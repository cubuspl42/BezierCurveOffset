package app.utils.iterable

fun <T : Comparable<T>> Iterable<T>.isSorted(): Boolean = mapWithNext { a, b -> a <= b }.all { it }

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

/**
 * Splits the list into sub-lists based on the given predicate. The list is split
 * into chunks where each chunk contains elements that satisfy the predicate as the first
 * element.
 *
 * Example:
 * ```
 * val list = listOf(1, 7, 2, 3, 9, 4, 5, 9, 1)
 * val result = list.splitBy { it % 2 == 0 }
 * // result: [[1, 7], [2, 3, 9], [4, 5, 9, 1]]
 */
fun <T> List<T>.splitBy(
    predicate: (T) -> Boolean,
): List<List<T>> {
    val result = mutableListOf<List<T>>()
    val currentChunk = mutableListOf<T>()

    for (item in this) {
        if (predicate(item)) {
            if (currentChunk.isNotEmpty()) {
                result.add(currentChunk.toList())
                currentChunk.clear()
            }
        }
        currentChunk.add(item)
    }

    if (currentChunk.isNotEmpty()) {
        result.add(currentChunk.toList())
    }

    return result
}

/**
 * Shifts the elements of the list to the left until the first element that does not
 * satisfy the given predicate is found. The elements that satisfy the predicate
 * are moved to the end of the list.
 *
 * Example:
 * ```
 * val list = listOf('a', 'b', 'C', 'd', 'e', 'F')
 * val result = list.shiftWhile { it.isLowerCase() }
 * // result: ['C', 'd', 'e', 'F', 'a', 'b']
 *
 * @throw IllegalArgumentException if all elements satisfy the predicate
 */
fun <T> List<T>.shiftWhile(
    predicate: (T) -> Boolean,
): List<T> {
    val index = this.indexOfFirstOrNull { !predicate(it) } ?: throw IllegalArgumentException()
    val shifted = this.subList(index, size) + this.subList(0, index)
    return shifted
}

fun <T> List<T>.indexOfFirstOrNull(
    predicate: (T) -> Boolean,
): Int? {
    val index = this.indexOfFirst(predicate)
    return if (index == -1) null else index
}

/**
 * Transforms a list while carrying state between transformations.
 *
 * @param initialCarry The initial carry value.
 * @param transform A function that takes the current carry and an element of the list,
 * and returns a pair of the transformed element and the updated carry.
 * @return A pair of the transformed list and the final carry value.
 *
 * Example:
 * ```
 * val list = listOf("AB", "CD", "EF")
 * val (result, finalCarry) = list.mapCarrying(0) { carry, item ->
 *     val newCarry = carry + item
 *     val transformedItem = item * 2
 *     transformedItem to newCarry
 * }
 * // result: [2, 4, 6]
 * // finalCarry: 6
 * ```
 */
fun <T, R, C> Iterable<T>.mapCarrying(
    initialCarry: C,
    transform: (C, T) -> Pair<R, C>,
): Pair<List<R>, C> {
    val result = mutableListOf<R>()
    var carry = initialCarry

    for (item in this) {
        val (transformedItem, newCarry) = transform(carry, item)
        result.add(transformedItem)
        carry = newCarry
    }

    return Pair(result, carry)
}

/**
 * Transforms a list by confronting adjacent elements and applying a
 * transformation that considers the adjacent confrontations and the current
 * element.
 *
 * @param confront A function that computes a confrontation value between two
 * adjacent elements.
 * @param transform A function that takes the previous confrontation, the
 * current element, and the next confrontation,and returns a transformed value.
 * @param outerLeftConfrontation The confrontation value to use for the first
 * element's left side.
 * @param outerRightConfrontation The confrontation value to use for the last
 * element's right side.
 * @return A list of transformed values based on the confrontations and the
 * transformation function.
 *
 * Example:
 * ```
 * val list = listOf("abc", "bcde", "ef", "ghijk")
 * val result = list.confront(
 *     confront = { prevValue, nextValue -> prevValue.length * nextValue.length },
 *     transform = { prevConfrontation, value, nextConfrontation ->
 *         "$prevConfrontation|$value|$nextConfrontation"
 *     },
 *     outerLeftConfrontation = -1,
 *     outerRightConfrontation = -2,
 * )
 * // result: ["-1|abc|12", "12|bcde|8", "8|ef|10", "10|ghijk|-2"]
 * ```
 */
fun <T, C, R> List<T>.confront(
    confront: (prevValue: T, nextValue: T) -> C,
    transform: (prevConfrontation: C, value: T, nextConfrontation: C) -> R,
    outerLeftConfrontation: C,
    outerRightConfrontation: C,
): List<R> {
    val (firstValue, trailingValues) = uncons() ?: return emptyList()

    val (innerValues, lastValue) = trailingValues.untrail() ?: return listOf(
        transform(
            outerLeftConfrontation,
            firstValue,
            outerRightConfrontation,
        ),
    )

    val confrontations = this.zipWithNext { prevValue, nextValue ->
        confront(prevValue, nextValue)
    }

    return listOf(
        transform(
            outerLeftConfrontation,
            firstValue,
            confrontations.first(),
        ),
    ) + confrontations.zipWithNext().zip(innerValues) { (prevConfrontation, nextConfrontation), innerValue ->
        transform(
            prevConfrontation,
            innerValue,
            nextConfrontation,
        )
    } + listOf(
        transform(
            confrontations.last(),
            lastValue,
            outerRightConfrontation,
        ),
    )
}

fun <T, C, R> List<T>.confrontCyclic(
    confront: (prevValue: T, nextValue: T) -> C,
    transform: (prevConfrontation: C, value: T, nextConfrontation: C) -> R,
): List<R> = when {
    isEmpty() -> emptyList()

    else -> {
        val confrontation = confront(last(), first())

        confront(
            confront = confront,
            transform = transform,
            outerLeftConfrontation = confrontation,
            outerRightConfrontation = confrontation,
        )
    }
}
