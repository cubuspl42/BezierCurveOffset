package app.geometry.bezier_curves

import app.geometry.Point
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CubicBezierCurveTests {
    @Test
    fun testFindOffsetSpline_micro() {
        val bezierCurve = CubicBezierCurve.of(
            start = Point.of(0.0, 0.0),
            control0 = Point.of(Double.MIN_VALUE, Double.MIN_VALUE),
            control1 = Point.of(2 * Double.MIN_VALUE, Double.MIN_VALUE),
            end = Point.of(3.0 * Double.MIN_VALUE, 0.0),
        )

        assertNull(
            bezierCurve.findOffsetSpline(
                strategy = ProperBezierCurve.BestFitOffsetStrategy,
                offset = Double.MIN_VALUE,
            ),
        )
    }

    @Test
    fun testFindOffsetSpline_micro_degenerate() {
        val bezierCurve = CubicBezierCurve.of(
            start = Point.of(0.0, 0.0),
            control0 = Point.of(-Double.MIN_VALUE, 0.0),
            control1 = Point.of(Double.MIN_VALUE, 0.0),
            end = Point.of(0.0, 0.0),
        )

        assertNull(
            bezierCurve.findOffsetSpline(
                strategy = ProperBezierCurve.BestFitOffsetStrategy,
                offset = Double.MIN_VALUE,
            ),
        )
    }

    @Test
    fun testFindOffsetSpline_simple() {
        val bezierCurve = CubicBezierCurve.of(
            start = Point.of(0.0, 0.0),
            control0 = Point.of(1.0, 1.0),
            control1 = Point.of(2.0, 1.0),
            end = Point.of(3.0, 0.0),
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

    @Test
    fun testFindOffsetSpline_degenerate() {
        val bezierCurve = CubicBezierCurve.of(
            start = Point.of(0.0, 0.0),
            control0 = Point.of(-1.0, 0.0),
            control1 = Point.of(1.0, 0.0),
            end = Point.of(0.0, 0.0),
        )

        val offsetSplineResult = assertNotNull(
            bezierCurve.findOffsetSpline(
                strategy = ProperBezierCurve.BestFitOffsetStrategy,
                offset = 1.0,
            ),
        )

        assertEquals(
            expected = 1.35,
            actual = offsetSplineResult.globalDeviation,
            absoluteTolerance = 0.01,
        )
    }

}
