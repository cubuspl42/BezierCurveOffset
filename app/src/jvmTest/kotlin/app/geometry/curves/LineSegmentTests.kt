package app.geometry.curves

import app.algebra.assertEqualsWithAbsoluteTolerance
import app.geometry.Constants
import app.geometry.Point
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class LineSegmentTests {
    @Test
    fun testFindIntersection_crossing() {
        val lineSegment0 = LineSegment(
            start = Point.of(0.0, -50.0),
            end = Point.of(0.0, 50.0),
        )

        val lineSegment1 = LineSegment(
            start = Point.of(-50.0, 0.0),
            end = Point.of(50.0, 0.0),
        )

        val intersectionPoint = assertNotNull(
            LineSegment.findIntersection(
                lineSegment0, lineSegment1,
            ),
        )

        assertEqualsWithAbsoluteTolerance(
            expected = Point.of(0.0, 0.0),
            actual = intersectionPoint,
            absoluteTolerance = Constants.epsilon,
        )
    }

    @Test
    fun testFindIntersection_notCrossing() {
        val lineSegment0 = LineSegment(
            start = Point.of(-50.0, -50.0),
            end = Point.of(50.0, 50.0),
        )

        val lineSegment1 = LineSegment(
            start = Point.of(10.0, -10.0),
            end = Point.of(100.0, -100.0),
        )

        assertNull(
            LineSegment.findIntersection(
                lineSegment0 = lineSegment0,
                lineSegment1 = lineSegment1,
            ),
        )
    }

    @Test
    fun testFindIntersection_parallel() {
        val lineSegment0 = LineSegment(
            start = Point.of(0.0, 0.0),
            end = Point.of(50.0, 50.0),
        )

        val lineSegment1 = LineSegment(
            start = Point.of(0.0, 10.0),
            end = Point.of(50.0, 60.0),
        )

        assertNull(
            LineSegment.findIntersection(
                lineSegment0 = lineSegment0,
                lineSegment1 = lineSegment1,
            ),
        )
    }
}
