package app.algebra

import app.algebra.NumericObject.Tolerance
import app.geometry.Point

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
        message = message?.let { "$it (x)" })

    kotlin.test.assertEquals(
        expected = expected.y,
        actual = actual.y,
        absoluteTolerance = absoluteTolerance,
        message = message?.let { "$it (y)" })
}

fun assertEqualsWithTolerance(
    expected: Double,
    actual: Double,
    tolerance: Tolerance,
) {
    assert(expected.equalsWithTolerance(actual, tolerance = tolerance)) {
        "Expected $expected, but got $actual (tolerance: $tolerance)"
    }
}

fun <T : NumericObject> assertEqualsWithTolerance(
    expected: T,
    actual: T,
    tolerance: Tolerance,
) {
    assert(expected.equalsWithTolerance(actual, tolerance = tolerance)) {
        "Expected $expected, but got $actual (tolerance: $tolerance)"
    }
}

fun <T : NumericObject> assertEqualsWithTolerance(
    expected: T,
    actual: T,
    absoluteTolerance: Double,
) {
    assertEqualsWithTolerance(
        expected = expected,
        actual = actual,
        tolerance = Tolerance.Absolute(
            absoluteTolerance = absoluteTolerance,
        ),
    )
}

fun <T : NumericObject> assertEqualsWithTolerance(
    expected: List<T>,
    actual: List<T>,
    tolerance: Tolerance,
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

fun <T : NumericObject> assertEqualsWithTolerance(
    expected: List<T>,
    actual: List<T>,
    absoluteTolerance: Double,
) {
    assertEqualsWithTolerance(
        expected = expected,
        actual = actual,
        tolerance = Tolerance.Absolute(
            absoluteTolerance = absoluteTolerance,
        ),
    )
}

@JvmName("assertEqualsWithToleranceDouble")
fun assertEqualsWithTolerance(
    expected: List<Double>,
    actual: List<Double>,
    tolerance: Tolerance,
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

@JvmName("assertEqualsWithToleranceDoubleAbsolute")
fun assertEqualsWithTolerance(
    expected: List<Double>,
    actual: List<Double>,
    absoluteTolerance: Double,
) {
    assertEqualsWithTolerance(
        expected = expected,
        actual = actual,
        tolerance = Tolerance.Absolute(
            absoluteTolerance = absoluteTolerance,
        ),
    )
}
