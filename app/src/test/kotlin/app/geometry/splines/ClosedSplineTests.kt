package app.geometry.splines

import app.PatternOutline
import app.PatternOutline.PatternOutlineParams
import app.PatternOutline.PatternOutlineParams.EdgeHandle
import app.PatternOutline.PatternOutlineParams.SegmentParams
import app.PatternSvg
import app.SeamAllowanceKind
import app.algebra.assertEqualsWithTolerance
import app.geometry.Point
import app.geometry.SvgCurveExtractionUtils
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve
import app.geometry.curves.SegmentCurve.OffsetEdgeMetadata
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.splines.ClosedSpline.ContourEdgeMetadata
import app.geometry.splines.ClosedSpline.ContourKnotMetadata
import app.writeToFile
import kotlin.io.path.Path
import kotlin.test.Test

private val eps = 10e-3

class ClosedSplineTests {
    companion object {
        private val cornerEdge = Spline.Edge(
            curveEdge = LineSegment.Edge,
            metadata = ContourEdgeMetadata.CornerEdge,
        )

        private fun Spline.CompleteLink<CubicBezierCurve, OffsetEdgeMetadata.Precise, Nothing?>.withSideMetadata(): Spline.PartialLink<CubicBezierCurve, ContourEdgeMetadata.Side, Nothing?> =
            withoutEndKnot.mapEdgeMetadata {
                ContourEdgeMetadata.Side(offsetMetadata = it)
            }

        private fun Point.withNoMetadata() = Spline.Knot(
            point = this,
            metadata = null,
        )

        private fun Point.withPreCornerMetadata() = Spline.PartialLink(
            startKnot = Spline.Knot(
                point = this,
                metadata = null,
            ),
            edge = cornerEdge,
        )

        private fun Point.withCornerMetadata() = Spline.PartialLink(
            startKnot = Spline.Knot(
                point = this,
                metadata = ContourKnotMetadata.Corner,
            ),
            edge = cornerEdge,
        )
    }

    @Test
    fun testInterconnect_singleSpline() {
        val knot0 = Point.of(-50.0, 0.0)

        val edge0 = Spline.Edge(
            curveEdge = CubicBezierCurve.Edge(
                control0 = Point.of(-100.0, -100.0),
                control1 = Point.of(100.0, -100.0),
            ),
            metadata = OffsetEdgeMetadata.Precise,
        )

        val knot1 = Point.of(50.0, 0.0)

        val inputLink0 = Spline.CompleteLink(
            startKnot = knot0.withNoMetadata(),
            edge = edge0,
            endKnot = knot1.withNoMetadata(),
        )

        val spline = OpenSpline.of(
            leadingLinks = emptyList(),
            lastLink = inputLink0,
        )

        val interconnectedSpline = ClosedSpline.interconnect(
            splines = listOf(spline),
        )

        val corner0 = Point.of(0.0, 100.0)

        val expectedLinks = listOf(
            inputLink0.withSideMetadata(),
            knot1.withPreCornerMetadata(),
            corner0.withCornerMetadata(),
        )

        assertEqualsWithTolerance(
            expected = expectedLinks,
            actual = interconnectedSpline.cyclicLinks,
            tolerance = eps,
        )
    }

    @Test
    fun testInterconnect_twoSplines() {
        val knot0 = Point.of(100.0, 150.0)

        val edge0 = Spline.Edge(
            curveEdge = CubicBezierCurve.Edge(
                control0 = Point.of(200.0, 200.0),
                control1 = Point.of(300.0, 200.0),
            ),
            metadata = OffsetEdgeMetadata.Precise,
        )

        val knot1 = Point.of(400.0, 150.0)

        val expectedCorner0 = Point.of(500.0, 100.0)

        val inputLink0 = Spline.CompleteLink(
            startKnot = knot0.withNoMetadata(),
            edge = edge0,
            endKnot = knot1.withNoMetadata(),
        )

        val knot2 = Point.of(400.0, 50.0)

        val edge1 = Spline.Edge(
            curveEdge = CubicBezierCurve.Edge(
                control0 = Point.of(300.0, 0.0),
                control1 = Point.of(200.0, 0.0),
            ),
            metadata = OffsetEdgeMetadata.Precise,
        )

        val knot3 = Point.of(100.0, 50.0)

        val expectedCorner1 = Point.of(0.0, 100.0)

        val inputLink1 = Spline.CompleteLink(
            startKnot = knot2.withNoMetadata(),
            edge = edge1,
            endKnot = knot3.withNoMetadata(),
        )

        val spline0 = OpenSpline.of(
            leadingLinks = emptyList(),
            lastLink = inputLink0,
        )

        val spline1 = OpenSpline.of(
            leadingLinks = emptyList(),
            lastLink = inputLink1,
        )

        val interconnectedSpline = ClosedSpline.interconnect(
            splines = listOf(spline0, spline1),
        )

        assertEqualsWithTolerance(
            expected = listOf(
                inputLink0.withSideMetadata(),
                knot1.withPreCornerMetadata(),
                expectedCorner0.withCornerMetadata(),
                inputLink1.withSideMetadata(),
                knot3.withPreCornerMetadata(),
                expectedCorner1.withCornerMetadata(),
            ),
            actual = interconnectedSpline.cyclicLinks,
            tolerance = eps,
        )
    }

    @Test
    fun testInterconnect_threeSplines() {
        val knot0 = Point.of(100.0, 200.0)

        val edge0 = Spline.Edge(
            curveEdge = CubicBezierCurve.Edge(
                control0 = Point.of(150.0, 100.0),
                control1 = Point.of(250.0, 0.0),
            ),
            metadata = OffsetEdgeMetadata.Precise,
        )

        val knot1 = Point.of(350.0, 100.0)

        val inputLink0 = Spline.CompleteLink(
            startKnot = knot0.withNoMetadata(),
            edge = edge0,
            endKnot = knot1.withNoMetadata(),
        )

        val expectedCorner0 = Point.of(400.0, 150.0)

        val knot2 = Point.of(450.0, 100.0)

        val edge1 = Spline.Edge(
            curveEdge = CubicBezierCurve.Edge(
                control0 = Point.of(550.0, 0.0),
                control1 = Point.of(650.0, 100.0),
            ),
            metadata = OffsetEdgeMetadata.Precise,
        )

        val knot3 = Point.of(700.0, 200.0)

        val inputLink1 = Spline.CompleteLink(
            startKnot = knot2.withNoMetadata(),
            edge = edge1,
            endKnot = knot3.withNoMetadata(),
        )

        val expectedCorner1 = Point.of(733.33, 266.66)

        val knot4 = Point.of(600.0, 400.0)

        val edge2 = Spline.Edge(
            curveEdge = CubicBezierCurve.Edge(
                control0 = Point.of(500.0, 500.0),
                control1 = Point.of(300.0, 500.0),
            ),
            metadata = OffsetEdgeMetadata.Precise,
        )

        val knot5 = Point.of(200.0, 400.0)

        val inputLink2 = Spline.CompleteLink(
            startKnot = knot4.withNoMetadata(),
            edge = edge2,
            endKnot = knot5.withNoMetadata(),
        )

        val expectedCorner2 = Point.of(66.67, 266.67)

        val spline0 = OpenSpline.of(
            leadingLinks = emptyList(),
            lastLink = inputLink0,
        )

        val spline1 = OpenSpline.of(
            leadingLinks = emptyList(),
            lastLink = inputLink1,
        )

        val spline2 = OpenSpline.of(
            leadingLinks = emptyList(),
            lastLink = inputLink2,
        )

        val interconnectedSpline = ClosedSpline.interconnect(
            splines = listOf(spline0, spline1, spline2),
        )

        val expectedInterconnectedSpline = ClosedSpline(
            listOf(
                inputLink0.withSideMetadata(),
                knot1.withPreCornerMetadata(),
                expectedCorner0.withCornerMetadata(),
                inputLink1.withSideMetadata(),
                knot3.withPreCornerMetadata(),
                expectedCorner1.withCornerMetadata(),
                inputLink2.withSideMetadata(),
                knot5.withPreCornerMetadata(),
                expectedCorner2.withCornerMetadata(),
            ),
        )

        assertEqualsWithTolerance(
            expected = expectedInterconnectedSpline,
            actual = interconnectedSpline,
            tolerance = eps,
        )
    }

    @Test
    fun testFindContourSpline1() {
        val reader = ClosedSplineTests::class.java.getResourceAsStream("testFindContourSpline1.svg")!!.reader()
        val markedSpline = PatternSvg.extractFromReader(reader = reader)

        val patternOutline = PatternOutline.fromMarkedSpline(
            markedSpline = markedSpline,
            params = PatternOutlineParams(
                segmentParamsByEdgeHandle = mapOf(
                    EdgeHandle(
                        firstKnotName = "B",
                        secondKnotName = "C",
                    ) to SegmentParams(
                        seamAllowanceKind = SeamAllowanceKind.Edging,
                    ),
                    EdgeHandle(
                        firstKnotName = "C",
                        secondKnotName = "D",
                    ) to SegmentParams(
                        seamAllowanceKind = SeamAllowanceKind.None,
                    ),

                    EdgeHandle(
                        firstKnotName = "D",
                        secondKnotName = "E",
                    ) to SegmentParams(
                        seamAllowanceKind = SeamAllowanceKind.Edging,
                    ),
                ),
            ),
        )

        val spline = patternOutline.closedSpline

        val contourSpline = spline.findContourSpline(
            offsetStrategy = object : ClosedSpline.ContourOffsetStrategy<SeamAllowanceKind>() {
                override fun determineOffsetParams(
                    edgeMetadata: SeamAllowanceKind,
                ): SegmentCurve.OffsetSplineParams {
                    val seamAllowanceKind = edgeMetadata
                    return SegmentCurve.OffsetSplineParams(
                        offset = seamAllowanceKind.widthMm,
                    )
                }
            },
        )!!
    }
}

