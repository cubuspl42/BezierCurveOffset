package app.geometry.splines

import app.algebra.assertEqualsWithTolerance
import app.geometry.Point
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve
import app.geometry.curves.SegmentCurve.OffsetEdgeMetadata
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.splines.ClosedSpline.ContourEdgeMetadata
import app.geometry.splines.ClosedSpline.ContourKnotMetadata
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val knot0 = Point.of(-0.5, 0.0)

        val edge0 = Spline.Edge(
            curveEdge = CubicBezierCurve.Edge(
                control0 = Point.of(-1.0, -1.0),
                control1 = Point.of(1.0, -1.0),
            ),
            metadata = OffsetEdgeMetadata.Precise,
        )

        val knot1 = Point.of(0.5, 0.0)

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

        val corner0 = Point.of(0.0, 1.0)

        assertEquals<List<Spline.PartialLink<SegmentCurve<*>, *, *>>>(
            expected = listOf(
                inputLink0.withSideMetadata(),
                knot1.withPreCornerMetadata(),
                corner0.withCornerMetadata(),
            ),
            actual = interconnectedSpline.cyclicLinks,
        )
    }

    @Test
    fun testInterconnect_twoSplines() {
        val knot0 = Point.of(0.0, 0.5)

        val edge0 = Spline.Edge(
            curveEdge = CubicBezierCurve.Edge(
                control0 = Point.of(1.0, 1.0),
                control1 = Point.of(2.0, 1.0),
            ),
            metadata = OffsetEdgeMetadata.Precise,
        )

        val knot1 = Point.of(3.0, 0.5)

        val inputLink0 = Spline.CompleteLink(
            startKnot = knot0.withNoMetadata(),
            edge = edge0,
            endKnot = knot1.withNoMetadata(),
        )

        val knot2 = Point.of(3.0, -0.5)

        val edge1 = Spline.Edge(
            curveEdge = CubicBezierCurve.Edge(
                control0 = Point.of(2.0, -1.0),
                control1 = Point.of(1.0, -1.0),
            ),
            metadata = OffsetEdgeMetadata.Precise,
        )

        val knot3 = Point.of(0.0, -0.5)

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
                knot0.withPreCornerMetadata(),
                knot1.withCornerMetadata(),
                inputLink1.withSideMetadata(),
                knot2.withPreCornerMetadata(),
                knot3.withCornerMetadata(),
            ),
            actual = interconnectedSpline.cyclicLinks,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testInterconnect_threeSplines() {
        val knot0 = Point.of(-3.0, -1.0)

        val edge0 = Spline.Edge(
            curveEdge = CubicBezierCurve.Edge(
                control0 = Point.of(-2.5, -2.0),
                control1 = Point.of(-1.5, -3.0),
            ),
            metadata = OffsetEdgeMetadata.Precise,
        )

        val knot1 = Point.of(-0.5, 4.0)

        val inputLink0 = Spline.CompleteLink(
            startKnot = knot0.withNoMetadata(),
            edge = edge0,
            endKnot = knot1.withNoMetadata(),
        )

        val knot2 = Point.of(0.5, 4.0)

        val edge1 = Spline.Edge(
            curveEdge = CubicBezierCurve.Edge(
                control0 = Point.of(1.5, -3.0),
                control1 = Point.of(2.5, -2.0),
            ),
            metadata = OffsetEdgeMetadata.Precise,
        )

        val knot3 = Point.of(3.0, -1.0)

        val inputLink1 = Spline.CompleteLink(
            startKnot = knot2.withNoMetadata(),
            edge = edge1,
            endKnot = knot3.withNoMetadata(),
        )

        val knot4 = Point.of(2.0, 1.0)

        val edge2 = Spline.Edge(
            curveEdge = CubicBezierCurve.Edge(
                control0 = Point.of(1.0, 2.0),
                control1 = Point.of(-1.0, 2.0),
            ),
            metadata = OffsetEdgeMetadata.Precise,
        )

        val knot5 = Point.of(-2.0, 1.0)

        val inputLink2 = Spline.CompleteLink(
            startKnot = knot4.withNoMetadata(),
            edge = edge2,
            endKnot = knot5.withNoMetadata(),
        )

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
                knot2.withCornerMetadata(),
                inputLink1.withSideMetadata(),
                knot3.withPreCornerMetadata(),
                knot4.withCornerMetadata(),
                inputLink2.withSideMetadata(),
                knot4.withPreCornerMetadata(),
                knot5.withCornerMetadata(),
            ),
        )

        assertEqualsWithTolerance(
            expected = expectedInterconnectedSpline,
            actual = interconnectedSpline,
            absoluteTolerance = eps,
        )
    }
}
