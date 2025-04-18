package app.algebra

import app.geometry.Point
import kotlin.test.assertEquals

fun assertEquals(
    expected: Point,
    actual: Point,
    absoluteTolerance: Double,
    message: String? = null,
) {
    kotlin.test.assertEquals(
        expected = expected.x,
        actual = actual.x,
        absoluteTolerance = absoluteTolerance,
        message = message?.let { "$it (x)" }
    )

    kotlin.test.assertEquals(
        expected = expected.y,
        actual = actual.y,
        absoluteTolerance = absoluteTolerance,
        message = message?.let { "$it (y)" }
    )
}

fun <T : NumericObject> assertEqualsWithTolerance(
    expected: T,
    actual: T,
    tolerance: Double,
) {
    assert(expected.equalsWithTolerance(actual, tolerance = tolerance)) {
        "Expected $expected, but got $actual (tolerance: $tolerance)"
    }
}

fun <T : NumericObject> assertEqualsWithTolerance(
    expected: List<T>,
    actual: List<T>,
    tolerance: Double,
) {
    assert(expected.size == actual.size) {
        "Expected list size ${expected.size}, but got ${actual.size}"
    }

    for (i in expected.indices) {
        assertEqualsWithTolerance(
            expected = expected[i],
            actual = actual[i],
            tolerance = tolerance,
        )
    }
}

@JvmName("assertEqualsWithToleranceDouble")
fun assertEqualsWithTolerance(
    expected: List<Double>,
    actual: List<Double>,
    tolerance: Double,
) {
    assert(expected.size == actual.size) {
        "Expected list size ${expected.size}, but got ${actual.size}"
    }

    for (i in expected.indices) {
        assertEquals(
            expected = expected[i],
            actual = actual[i],
            absoluteTolerance = tolerance,
        )
    }
}
