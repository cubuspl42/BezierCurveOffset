package app

fun <T> assertEqual(a: T, b: T): T {
    assert(a == b)
    return a
}
