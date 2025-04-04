package app.algebra.linear

fun assertEquals(
    expected: Matrix4x4,
    actual: Matrix4x4,
    absoluteTolerance: Double,
) {
    assertEquals(
        expected = expected.column0,
        actual = actual.column0,
        absoluteTolerance = absoluteTolerance,
    )

    assertEquals(
        expected = expected.column1,
        actual = actual.column1,
        absoluteTolerance = absoluteTolerance,
    )

    assertEquals(
        expected = expected.column2,
        actual = actual.column2,
        absoluteTolerance = absoluteTolerance,
    )

    assertEquals(
        expected = expected.column3,
        actual = actual.column3,
        absoluteTolerance = absoluteTolerance,
    )
}
