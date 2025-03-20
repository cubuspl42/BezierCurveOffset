package app

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
