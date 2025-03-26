package app.geometry.bezier_curves

import app.geometry.Point
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CubicBezierCurveTests {
    @Test
    fun testFindOffsetSpline() {
        val bezierCurve = CubicBezierCurve.of(
            start = Point(0.0, 0.0),
            control0 = Point(1.0, 1.0),
            control1 = Point(2.0, 1.0),
            end = Point(3.0, 0.0),
        ) as CubicBezierCurve

        val offsetSplineResult = assertNotNull(
            bezierCurve.findOffsetSpline(
                strategy = ProperBezierCurve.BestFitOffsetStrategy,
                offset = 1.0,
            ),
        )

        assertEquals(
            expected = 0.02,
            actual = offsetSplineResult.globalDeviation,
            absoluteTolerance = 0.01,
        )
    }
}
