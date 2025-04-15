package app.geometry.curves.bezier

import app.algebra.assertEqualsWithTolerance
import app.assertEquals
import app.geometry.Curve
import app.geometry.Point
import app.geometry.SvgCurveExtractionUtils
import app.geometry.SvgCurveExtractionUtils.ExtractedPath
import app.geometry.curves.LineSegment
import app.geometry.splines.globalDeviation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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

        val subCurves: List<BezierCurve> = assertNotNull(
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
            actual = subCurves.size,
        )

        val bezierCurve0 = assertIs<CubicBezierCurve>(
            subCurves[0],
        )

        val bezierCurve1 = assertIs<CubicBezierCurve>(
            subCurves[1],
        )

        assertEquals(
            expected = start,
            actual = bezierCurve0.start,
        )

        assertEquals(
            expected = expectedNewControl0,
            actual = bezierCurve0.control0,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedNewControl1,
            actual = bezierCurve0.control1,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedSplitPoint,
            actual = bezierCurve0.end,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedNewControl2,
            actual = bezierCurve1.control0,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedNewControl3,
            actual = bezierCurve1.control1,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = end,
            actual = bezierCurve1.end,
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

        val subCurves = assertNotNull(
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
            actual = subCurves.size,
        )

        val bezierCurve0 = assertIs<CubicBezierCurve>(
            subCurves[0],
        )

        val bezierCurve1 = assertIs<CubicBezierCurve>(
            subCurves[1],
        )

        val bezierCurve2 = assertIs<CubicBezierCurve>(
            subCurves[2],
        )

        assertEquals(
            expected = start,
            actual = bezierCurve0.start,
        )

        assertEquals(
            expected = expectedNewControl0,
            actual = bezierCurve0.control0,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedNewControl1,
            actual = bezierCurve0.control1,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedSplitPoint0,
            actual = bezierCurve0.end,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedNewControl2,
            actual = bezierCurve1.control0,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedNewControl3,
            actual = bezierCurve1.control1,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedSplitPoint1,
            actual = bezierCurve1.end,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedNewControl4,
            actual = bezierCurve2.control0,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedNewControl5,
            actual = bezierCurve2.control1,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = end,
            actual = bezierCurve2.end,
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

        val subCurves = assertNotNull(
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
            actual = subCurves.size,
        )

        val bezierCurve0 = assertIs<CubicBezierCurve>(
            subCurves[0],
        )

        val bezierCurve1 = assertIs<CubicBezierCurve>(
            subCurves[1],
        )

        val bezierCurve2 = assertIs<CubicBezierCurve>(
            subCurves[2],
        )

        val bezierCurve3 = assertIs<CubicBezierCurve>(
            subCurves[3],
        )

        assertEquals(
            expected = start,
            actual = bezierCurve0.start,
        )

        assertEquals(
            expected = expectedNewControl0,
            actual = bezierCurve0.control0,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedNewControl1,
            actual = bezierCurve0.control1,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedSplitPoint0,
            actual = bezierCurve0.end,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedNewControl2,
            actual = bezierCurve1.control0,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedNewControl3,
            actual = bezierCurve1.control1,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedSplitPoint1,
            actual = bezierCurve1.end,
        )

        assertEquals(
            expected = expectedNewControl4,
            actual = bezierCurve2.control0,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedNewControl5,
            actual = bezierCurve2.control1,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedSplitPoint2,
            actual = bezierCurve2.end,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = end,
            actual = bezierCurve3.end,
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
        )

        val offsetSplineResult = assertNotNull(
            bezierCurve.findOffsetSpline(
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
                offset = 1.0,
            ),
        )

        assertEquals(
            expected = 0.0, // TODO: Verify this
            actual = offsetSplineResult.globalDeviation,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testFindIntersection_lineSegment() {
        val extractedCurveSet = SvgCurveExtractionUtils.extractCurves(
            clazz = CubicBezierSegmentCurveTests::class.java,
            resourceName = "lineBezierIntersection1.svg",
        )

        val extractedBezier = extractedCurveSet.getCurveByColor(
            color = ExtractedPath.blue,
        ) as SvgCurveExtractionUtils.ExtractedOpenSpline

        val bezierCurve = extractedBezier.openSpline.subCurves.single() as CubicBezierCurve

        val extractedLine = extractedCurveSet.getCurveByColor(
            color = ExtractedPath.red,
        ) as SvgCurveExtractionUtils.ExtractedOpenSpline

        val lineSegment = extractedLine.openSpline.subCurves.single() as LineSegment

        val intersectionDetails = CubicBezierCurve.findIntersections(
            lineSegment = lineSegment,
            bezierCurve = bezierCurve,
        )

        val intersectionDetailsSorted = intersectionDetails.sortedBy { it.point.x }

        assertEqualsWithTolerance(
            expected = listOf(
                object : Curve.IntersectionDetails() {
                    override val point: Point = Point.of(56.4, 121.4)
                    override val t0: Double = 0.1388
                    override val t1: Double = 0.097
                },
                object : Curve.IntersectionDetails() {
                    override val point: Point = Point.of(125.1, 138.2)
                    override val t0: Double = 0.4982
                    override val t1: Double = 0.5146
                },
                object : Curve.IntersectionDetails() {
                    override val point: Point = Point.of(191.7, 154.5)
                    override val t0: Double = 0.8466
                    override val t1: Double = 0.9314
                },
            ),
            actual = intersectionDetailsSorted,
            absoluteTolerance = eps,
        )
    }
}
