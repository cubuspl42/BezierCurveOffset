package app.geometry.curves.bezier

import app.algebra.NumericObject
import app.algebra.assertEqualsWithAbsoluteTolerance
import app.algebra.assertEqualsWithTolerance
import app.algebra.euclidean.bezier_binomials.RealFunction
import app.algebra.euclidean.bezier_binomials.sample
import app.geometry.Point
import app.SvgCurveExtractionUtils
import app.SvgCurveExtractionUtils.ExtractedCircle
import app.SvgCurveExtractionUtils.ExtractedOpenSpline
import app.SvgCurveExtractionUtils.ExtractedShape
import app.geometry.curves.LineSegment
import app.geometry.splines.globalDeviation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

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

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl0,
            actual = bezierCurve0.control0,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl1,
            actual = bezierCurve0.control1,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedSplitPoint,
            actual = bezierCurve0.end,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl2,
            actual = bezierCurve1.control0,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
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

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl0,
            actual = bezierCurve0.control0,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl1,
            actual = bezierCurve0.control1,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedSplitPoint0,
            actual = bezierCurve0.end,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl2,
            actual = bezierCurve1.control0,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl3,
            actual = bezierCurve1.control1,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedSplitPoint1,
            actual = bezierCurve1.end,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl4,
            actual = bezierCurve2.control0,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
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

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl0,
            actual = bezierCurve0.control0,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl1,
            actual = bezierCurve0.control1,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedSplitPoint0,
            actual = bezierCurve0.end,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl2,
            actual = bezierCurve1.control0,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl3,
            actual = bezierCurve1.control1,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = expectedSplitPoint1,
            actual = bezierCurve1.end,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl4,
            actual = bezierCurve2.control0,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedNewControl5,
            actual = bezierCurve2.control1,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
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
    fun testFindIntersections_lineSegment() {
        val extractedCurveSet = SvgCurveExtractionUtils.extractCurves(
            clazz = CubicBezierCurveTests::class.java,
            resourceName = "lineBezierIntersection1.svg",
        )

        val extractedBezier = extractedCurveSet.getShapeByColor(
            color = ExtractedShape.blue,
        ) as SvgCurveExtractionUtils.ExtractedOpenSpline

        val bezierCurve = extractedBezier.openSpline.subCurves.single() as CubicBezierCurve

        val extractedLine = extractedCurveSet.getShapeByColor(
            color = ExtractedShape.red,
        ) as SvgCurveExtractionUtils.ExtractedOpenSpline

        val lineSegment = extractedLine.openSpline.subCurves.single() as LineSegment

        val intersectionDetails = CubicBezierCurve.findIntersections(
            lineSegment = lineSegment,
            bezierCurve = bezierCurve,
        )

        val intersectionDetailsSorted = intersectionDetails.sortedBy { it.x }

        assertEqualsWithTolerance(
            expected = listOf(
                Point.of(56.4104, 121.4349),
                Point.of(125.0821, 138.2226),
                Point.of(191.6589, 154.4981),
            ),
            actual = intersectionDetailsSorted,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testFindIntersections_bezierCurve() {
        val extractedCurveSet = SvgCurveExtractionUtils.extractCurves(
            clazz = CubicBezierCurveTests::class.java,
            resourceName = "lineBezierIntersection1.svg",
        )

        val extractedBezier = extractedCurveSet.getShapeByColor(
            color = ExtractedShape.blue,
        ) as SvgCurveExtractionUtils.ExtractedOpenSpline

        val bezierCurve = extractedBezier.openSpline.subCurves.single() as CubicBezierCurve

        val extractedLine = extractedCurveSet.getShapeByColor(
            color = ExtractedShape.red,
        ) as SvgCurveExtractionUtils.ExtractedOpenSpline

        val lineSegment = extractedLine.openSpline.subCurves.single() as LineSegment

        val intersectionPoints = CubicBezierCurve.findIntersections(
            lineSegment = lineSegment,
            bezierCurve = bezierCurve,
        )

        val intersectionDetailsSorted = intersectionPoints.sortedBy { it.x }

        assertEqualsWithTolerance(
            expected = listOf(
                Point.of(56.4104, 121.4349),
                Point.of(125.0821, 138.2226),
                Point.of(191.6589, 154.4981),
            ),
            actual = intersectionDetailsSorted,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testSnapPoint() {
        val curveSet = SvgCurveExtractionUtils.extractCurves(
            clazz = CubicBezierCurveTests::class.java,
            resourceName = "curveAndPoints.svg",
        )

        val bezierCurve = curveSet.extractedShapes.filterIsInstance<ExtractedOpenSpline>().single().singleBezierCurve()

        val samples = bezierCurve.basisFormula.sample(
            strategy = RealFunction.SamplingStrategy(
                sampleCount = 20000,
            ),
        )

        fun snapPointNaively(
            point: Point,
        ): Point = samples.map { it.value }.minBy { sample ->
            sample.asPoint.distanceTo(point)
        }.asPoint

        val points = curveSet.extractedShapes.mapNotNull {
            (it as? ExtractedCircle)?.center
        }

        // Just ensure that the point loading succeeded
        assert(points.size > 8)

        points.forEach { point ->
            val snappedPoint = bezierCurve.snapPoint(point)
            val expectedSnappedPoint = snapPointNaively(point)

            assertEqualsWithTolerance(
                expected = expectedSnappedPoint,
                actual = snappedPoint,
                tolerance = NumericObject.Tolerance.Absolute(
                    absoluteTolerance = 10e-3,
                ),
            )
        }
    }
}
