package app.geometry.transformations

import app.algebra.assertEqualsWithTolerance
import app.geometry.Point
import app.geometry.RawVector
import kotlin.test.Test

class RotationTests {
    private val eps = 10e-3

    @Test
    fun testTransform() {
        val rotation = Rotation.byAngle(
            RawVector(2.0, -1.0).angleBetweenXAxis(),
        )

        assertEqualsWithTolerance(
            expected = Point.of(13.41, 4.47),
            actual = rotation.transform(
                Point.of(10.0, 10.0),
            ),
            absoluteTolerance = eps,
        )
    }
}
