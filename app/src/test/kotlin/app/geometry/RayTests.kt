package app.geometry

import app.algebra.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertNotNull

class RayTests {
    @Test
    fun testFindIntersection() {
        val ray0 = Ray(
            startingPoint = Point.of(50.0, 0.0),
            direction = Direction.of(-0.45, 0.89)!!,
        )

        val ray1 = Ray(
            startingPoint = Point.of(-50.0, 0.0),
            direction = Direction.of(0.45, 0.89)!!,
        )

        val point = assertNotNull(
            ray0.findIntersection(ray1),
        )

        assertEqualsWithTolerance(
            expected = Point.of(0.0, 98.888888),
            actual = point,
            absoluteTolerance = Constants.epsilon,
        )
    }
}
