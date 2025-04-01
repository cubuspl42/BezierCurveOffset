package app.geometry.bezier_curves

import app.assertPointEquals
import app.geometry.Point
import app.geometry.bezier_splines.BezierSplineEdge
import kotlin.test.*

class CubicBezierCurveTests {
    private val eps = 10e-3

    @Test
    fun testSplitAtMultiple_singleTValue() {
        val start = Point.of(0.0, 0.0)
        val end = Point.of(3.0, 0.0)

        val bezierCurve = CubicBezierCurve.of(
            start = start,
            control0 = Point.of(1.0, 1.0),
            control1 = Point.of(2.0, 1.0),
            end = end,
        )

        val splitSpline = assertNotNull(
            bezierCurve.splitAtMultiple(
                tValues = setOf(0.2),
            ),
        )

        val expectedNewControl0 = Point.of(0.2, 0.2)
        val expectedNewControl1 = Point.of(0.4, 0.36)
        val expectedSplitPoint = Point.of(0.6, 0.48)
        val expectedNewControl2 = Point.of(1.4, 0.96)
        val expectedNewControl3 = Point.of(2.2, 0.8)

        assertEquals(
            expected = 2,
            actual = splitSpline.innerLinks.size,
        )

        val firstLink = splitSpline.innerLinks[0]
        val firstEdge = assertIs<BezierSplineEdge>(firstLink.edge)

        assertEquals(
            expected = start,
            actual = firstLink.startKnot,
        )

        assertPointEquals(
            expected = expectedNewControl0,
            actual = firstEdge.startControl,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl1,
            actual = firstEdge.endControl,
            absoluteTolerance = eps,
        )

        val secondLink = splitSpline.innerLinks[1]
        val secondEdge = assertIs<BezierSplineEdge>(secondLink.edge)

        assertPointEquals(
            expected = expectedSplitPoint,
            actual = secondLink.startKnot,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl2,
            actual = secondEdge.startControl,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl3,
            actual = secondEdge.endControl,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = end,
            actual = splitSpline.terminalLink.endKnot
        )
    }

    @Test
    fun testSplitAtMultiple_twoTValues() {
        val start = Point.of(0.0, 0.0)
        val end = Point.of(3.0, 0.0)

        val bezierCurve = CubicBezierCurve.of(
            start = start,
            control0 = Point.of(1.0, 1.0),
            control1 = Point.of(2.0, 1.0),
            end = end,
        )

        val splitSpline = assertNotNull(
            bezierCurve.splitAtMultiple(
                tValues = setOf(0.2, 0.8),
            ),
        )

        val expectedNewControl0 = Point.of(0.2, 0.2)
        val expectedNewControl1 = Point.of(0.4, 0.36)
        val expectedSplitPoint0 = Point.of(0.6, 0.48)
        val expectedNewControl2 = Point.of(1.2, 0.84)
        val expectedNewControl3 = Point.of(1.8, 0.84)
        val expectedSplitPoint1 = Point.of(2.4, 0.48)
        val expectedNewControl4 = Point.of(2.6, 0.36)
        val expectedNewControl5 = Point.of(2.8, 0.2)

        assertEquals(
            expected = 3,
            actual = splitSpline.innerLinks.size,
        )

        val firstLink = splitSpline.firstLink
        val firstEdge = assertIs<BezierSplineEdge>(firstLink.edge)

        assertEquals(
            expected = start,
            actual = firstLink.startKnot,
        )

        assertPointEquals(
            expected = expectedNewControl0,
            actual = firstEdge.startControl,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl1,
            actual = firstEdge.endControl,
            absoluteTolerance = eps,
        )

        val secondLink = splitSpline.innerLinks[1]
        val secondEdge = assertIs<BezierSplineEdge>(secondLink.edge)

        assertPointEquals(
            expected = expectedSplitPoint0,
            actual = secondLink.startKnot,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl2,
            actual = secondEdge.startControl,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl3,
            actual = secondEdge.endControl,
            absoluteTolerance = eps,
        )

        val thirdLink = splitSpline.innerLinks[2]
        val thirdEdge = assertIs<BezierSplineEdge>(thirdLink.edge)

        assertPointEquals(
            expected = expectedSplitPoint1,
            actual = thirdLink.startKnot,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl4,
            actual = thirdEdge.startControl,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl5,
            actual = thirdEdge.endControl,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = end,
            actual = splitSpline.terminalLink.endKnot
        )
    }

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
            expected = 0.0, // TODO: Verify this
            actual = offsetSplineResult.globalDeviation,
            absoluteTolerance = eps,
        )
    }
}
