package app

import app.geometry.Point
import kotlin.test.Test
import kotlin.test.assertEquals

class BezierFitTests {
    private val eps = 1e-5

    @Test
    fun testBasic() {
        val controlPoints = BezierFit.bestFit(
            points = arrayListOf(
                Point(0.0, 0.0),
                Point(1.0, 1.0),
                Point(2.0, 0.0),
                Point(3.0, 2.0),
            ),
        )

        val c0 = controlPoints[0]!!
        val c1 = controlPoints[1]!!
        val c2 = controlPoints[2]!!
        val c3 = controlPoints[3]!!


        assertEquals(
            expected = c0.x,
            actual = 0.0,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = c0.y,
            actual = 0.0,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = c1.x,
            actual = 1.09878,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = c1.y,
            actual = 3.63908,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = c2.x,
            actual = 2.70750,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = c2.y,
            actual = -3.72022,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = c3.x,
            actual = 2.99999,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = c3.y,
            actual = 1.99999,
            absoluteTolerance = eps,
        )
    }
}
