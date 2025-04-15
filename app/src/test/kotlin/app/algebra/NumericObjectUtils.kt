package app.algebra

import kotlin.test.assertEquals

fun <T : NumericObject> assertEqualsWithTolerance(
    expected: T,
    actual: T,
    absoluteTolerance: Double,
) {
    assert(expected.equalsWithTolerance(actual, absoluteTolerance = absoluteTolerance)) {
        "Expected $expected, but got $actual (tolerance: $absoluteTolerance)"
    }
}

fun <T : NumericObject> assertEqualsWithTolerance(
    expected: List<T>,
    actual: List<T>,
    absoluteTolerance: Double,
) {
    assert(expected.size == actual.size) {
        "Expected list size ${expected.size}, but got ${actual.size}"
    }

    for (i in expected.indices) {
        assertEqualsWithTolerance(
            expected = expected[i],
            actual = actual[i],
            absoluteTolerance = absoluteTolerance,
        )
    }
}

@JvmName("assertEqualsWithToleranceDouble")
fun assertEqualsWithTolerance(
    expected: List<Double>,
    actual: List<Double>,
    absoluteTolerance: Double,
) {
    assert(expected.size == actual.size) {
        "Expected list size ${expected.size}, but got ${actual.size}"
    }

    for (i in expected.indices) {
        assertEquals(
            expected = expected[i],
            actual = actual[i],
            absoluteTolerance = absoluteTolerance,
        )
    }
}
