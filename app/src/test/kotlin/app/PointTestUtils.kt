package app

import app.geometry.Point

fun assertPointEquals(
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
