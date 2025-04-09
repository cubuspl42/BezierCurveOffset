package app.algebra

fun <T : NumericObject> assertEqualsWithTolerance(
    expected: T,
    actual: T,
    absoluteTolerance: Double,
) {
    assert(expected.equalsWithTolerance(actual, absoluteTolerance = absoluteTolerance)) {
        "Expected $expected, but got $actual (tolerance: $absoluteTolerance)"
    }
}
