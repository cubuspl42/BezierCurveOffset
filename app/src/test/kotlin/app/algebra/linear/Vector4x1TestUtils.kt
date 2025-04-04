package app.algebra.linear

fun assertEquals(
    expected: Vector4x1,
    actual: Vector4x1,
    absoluteTolerance: Double,
) {
    kotlin.test.assertEquals(
        expected = expected.x,
        actual = actual.x,
        absoluteTolerance = absoluteTolerance,
    )

    kotlin.test.assertEquals(
        expected = expected.y,
        actual = actual.y,
        absoluteTolerance = absoluteTolerance,
    )

    kotlin.test.assertEquals(
        expected = expected.z,
        actual = actual.z,
        absoluteTolerance = absoluteTolerance,
    )

    kotlin.test.assertEquals(
        expected = expected.w,
        actual = actual.w,
        absoluteTolerance = absoluteTolerance,
    )
}
