package app.geometry.bezier_curves

import app.assertPointEquals
import app.geometry.Point
import kotlin.test.*

class CubicBezierSegmentCurveTests {
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
            actual = splitSpline.segments.size,
        )

        val segment0 = splitSpline.segments[0]
        val edge0 = assertIs<BezierCurve.Edge>(segment0.edge)

        assertEquals(
            expected = start,
            actual = segment0.startKnot,
        )

        assertPointEquals(
            expected = expectedNewControl0,
            actual = edge0.control0,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl1,
            actual = edge0.control1,
            absoluteTolerance = eps,
        )

        val link1 = splitSpline.segments[1]
        val edge1 = assertIs<BezierCurve.Edge>(link1.edge)

        assertPointEquals(
            expected = expectedSplitPoint,
            actual = link1.startKnot,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl2,
            actual = edge1.control0,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl3,
            actual = edge1.control1,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = end,
            actual = splitSpline.terminator.endKnot
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
            actual = splitSpline.segments.size,
        )

        val segment0 = splitSpline.segments[0]
        val firstEdge = assertIs<BezierCurve.Edge>(segment0.edge)

        assertEquals(
            expected = start,
            actual = segment0.startKnot,
        )

        assertPointEquals(
            expected = expectedNewControl0,
            actual = firstEdge.control0,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl1,
            actual = firstEdge.control1,
            absoluteTolerance = eps,
        )

        val link1 = splitSpline.segments[1]
        val edge1 = assertIs<BezierCurve.Edge>(link1.edge)

        assertPointEquals(
            expected = expectedSplitPoint0,
            actual = link1.startKnot,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl2,
            actual = edge1.control0,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl3,
            actual = edge1.control1,
            absoluteTolerance = eps,
        )

        val link2 = splitSpline.segments[2]
        val edge2 = assertIs<BezierCurve.Edge>(link2.edge)

        assertPointEquals(
            expected = expectedSplitPoint1,
            actual = link2.startKnot,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl4,
            actual = edge2.control0,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl5,
            actual = edge2.control1,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = end,
            actual = splitSpline.terminator.endKnot
        )
    }

    @Test
    fun testSplitAtMultiple_threeTValues() {
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
                tValues = setOf(0.2, 0.5, 0.8),
            ),
        )

        val expectedNewControl0 = Point.of(0.2, 0.2)
        val expectedNewControl1 = Point.of(0.4, 0.36)
        val expectedSplitPoint0 = Point.of(0.6, 0.48)
        val expectedNewControl2 = Point.of(0.9, 0.66)
        val expectedNewControl3 = Point.of(1.2, 0.75)
        val expectedSplitPoint1 = Point.of(1.5, 0.75)
        val expectedNewControl4 = Point.of(1.8, 0.75)
        val expectedNewControl5 = Point.of(2.1, 0.66)
        val expectedSplitPoint2 = Point.of(2.4, 0.48)
        val expectedNewControl6 = Point.of(2.6, 0.36)
        val expectedNewControl7 = Point.of(2.8, 0.2)

        assertEquals(
            expected = 4,
            actual = splitSpline.segments.size,
        )

        val segment0 = splitSpline.segments[0]
        val edge0 = assertIs<BezierCurve.Edge>(segment0.edge)

        assertEquals(
            expected = start,
            actual = segment0.startKnot,
        )

        assertPointEquals(
            expected = expectedNewControl0,
            actual = edge0.control0,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl1,
            actual = edge0.control1,
            absoluteTolerance = eps,
        )

        val link1 = splitSpline.segments[1]
        val edge1 = assertIs<BezierCurve.Edge>(link1.edge)

        assertPointEquals(
            expected = expectedSplitPoint0,
            actual = link1.startKnot,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl2,
            actual = edge1.control0,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl3,
            actual = edge1.control1,
            absoluteTolerance = eps,
        )

        val link2 = splitSpline.segments[2]
        val edge2 = assertIs<BezierCurve.Edge>(link2.edge)

        assertPointEquals(
            expected = expectedSplitPoint1,
            actual = link2.startKnot,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl4,
            actual = edge2.control0,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl5,
            actual = edge2.control1,
            absoluteTolerance = eps,
        )

        val link3 = splitSpline.segments[3]
        val edge3 = assertIs<BezierCurve.Edge>(link3.edge)

        assertPointEquals(
            expected = expectedSplitPoint2,
            actual = link3.startKnot,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl6,
            actual = edge3.control0,
            absoluteTolerance = eps,
        )

        assertPointEquals(
            expected = expectedNewControl7,
            actual = edge3.control1,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = end,
            actual = splitSpline.terminator.endKnot
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
