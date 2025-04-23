package app.geometry

import app.algebra.assertEqualsWithAbsoluteTolerance
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class RayTests {
    @Test
    fun testFindIntersection_crossing() {
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

        assertEqualsWithAbsoluteTolerance(
            expected = Point.of(0.0, 98.888888),
            actual = point,
            absoluteTolerance = Constants.epsilon,
        )
    }

    @Test
    fun testFindIntersection_notCrossing() {
        val ray0 = Ray(
            startingPoint = Point.of(-50.0, 0.0),
            direction = Direction.of(-1.0, -1.0)!!,
        )

        val ray1 = Ray(
            startingPoint = Point.of(50.0, 0.0),
            direction = Direction.of(1.0, -1.0)!!,
        )

        assertNull(
            ray0.findIntersection(ray1),
        )
    }

    @Test
    fun testFindIntersection_parallel() {
        val ray0 = Ray(
            startingPoint = Point.of(0.0, 0.0),
            direction = Direction.of(1.0, 0.0)!!,
        )

        val ray1 = Ray(
            startingPoint = Point.of(10.0, 10.0),
            direction = Direction.of(-1.0, 0.0)!!,
        )

        assertNull(
            ray0.findIntersection(ray1),
        )
    }
}
