package app

import app.geometry.Point
import app.geometry.Polyline
import kotlin.test.Test
import kotlin.test.assertEquals

class BezierFitTests {
    private val eps = 1e-5

    @Test
    fun testBasic() {
        val polyline = Polyline(
            points = listOf(
                Point(0.0, 0.0),
                Point(1.0, 1.0),
                Point(2.0, 0.0),
                Point(3.0, 2.0),
            ),
        )

        val timedPointSeries = polyline.timeNaively()
        val bezierCurve = timedPointSeries.bestFitCurve()
        val error = timedPointSeries.calculateFitError(bezierCurve)

        val c0 = bezierCurve.start
        val c1 = bezierCurve.control0
        val c2 = bezierCurve.control1
        val c3 = bezierCurve.end

        assertEquals(
            actual = c0.x,
            expected = 0.0,
            absoluteTolerance = eps,
        )

        assertEquals(
            actual = c0.y,
            expected = 0.0,
            absoluteTolerance = eps,
        )

        assertEquals(
            actual = c1.x,
            expected = 1.09878,
            absoluteTolerance = eps,
        )

        assertEquals(
            actual = c1.y,
            expected = 3.63908,
            absoluteTolerance = eps,
        )

        assertEquals(
            actual = c2.x,
            expected = 2.70750,
            absoluteTolerance = eps,
        )

        assertEquals(
            actual = c2.y,
            expected = -3.72022,
            absoluteTolerance = eps,
        )

        assertEquals(
            actual = c3.x,
            expected = 2.99999,
            absoluteTolerance = eps,
        )

        assertEquals(
            actual = c3.y,
            expected = 1.99999,
            absoluteTolerance = eps,
        )

        assertEquals(
            actual = error,
            expected = 0.0,
            absoluteTolerance = eps,
        )
    }
}
