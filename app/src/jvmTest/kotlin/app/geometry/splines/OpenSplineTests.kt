package app.geometry.splines

import app.algebra.assertEqualsWithAbsoluteTolerance
import app.geometry.Point
import app.geometry.curves.SegmentCurve
import app.geometry.curves.bezier.CubicBezierCurve
import kotlin.test.Test

class OpenSplineTests {
    companion object {
        private val eps = 10e-3

        private fun <CurveT : SegmentCurve<CurveT>> SegmentCurve.Edge<CurveT>.withNoMetadata() = Spline.Edge(
            curveEdge = this,
            metadata = null,
        )

        private fun Point.withNoMetadata() = Spline.Knot(
            point = this,
            metadata = null,
        )
    }

    @Test
    fun testMerge_singleSpline_singleSubCurve() {
        val knot0 = Point.of(0.0, 0.0)

        val edge0 = CubicBezierCurve.Edge(
            control0 = Point.of(1.0, 1.0),
            control1 = Point.of(2.0, 1.0),
        )

        val knot1 = Point.of(3.0, 0.0)

        val spline = OpenSpline.of(
            leadingLinks = emptyList(),
            lastLink = Spline.CompleteLink(
                startKnot = knot0.withNoMetadata(),
                edge = edge0.withNoMetadata(),
                endKnot = knot1.withNoMetadata(),
            ),
        )

        val mergedSpline = OpenSpline.merge(listOf(spline))

        assertEqualsWithAbsoluteTolerance(
            expected = spline,
            actual = mergedSpline,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testMerge_singleSpline_multipleSubCurves() {
        val knot0 = Point.of(0.0, 0.0).withNoMetadata()

        val edge0 = CubicBezierCurve.Edge(
            control0 = Point.of(1.0, 1.0),
            control1 = Point.of(2.0, 1.0),
        ).withNoMetadata()

        val knot1 = Point.of(3.0, 0.0).withNoMetadata()

        val edge1 = CubicBezierCurve.Edge(
            control0 = Point.of(4.0, 1.0),
            control1 = Point.of(5.0, 1.0),
        ).withNoMetadata()

        val knot2 = Point.of(6.0, 0.0).withNoMetadata()

        val spline = OpenSpline.of(
            leadingLinks = listOf(
                Spline.PartialLink(
                    startKnot = knot0,
                    edge = edge0,
                ),
            ),
            lastLink = Spline.CompleteLink(
                startKnot = knot1,
                edge = edge1,
                endKnot = knot2,
            ),
        )

        val mergedSpline = OpenSpline.merge(listOf(spline))

        assertEqualsWithAbsoluteTolerance(
            expected = spline,
            actual = mergedSpline,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testMerge_twoSplines_singleSubCurve() {
        val knot0 = Point.of(0.0, 0.0).withNoMetadata()

        val edge0 = CubicBezierCurve.Edge(
            control0 = Point.of(1.0, 1.0),
            control1 = Point.of(2.0, 1.0),
        ).withNoMetadata()

        val knot1 = Point.of(3.0, 0.0).withNoMetadata()

        val edge1 = CubicBezierCurve.Edge(
            control0 = Point.of(5.0, 1.0),
            control1 = Point.of(6.0, 1.0),
        ).withNoMetadata()

        val knot3 = Point.of(7.0, 0.0)

        val lastLink = Spline.CompleteLink(
            startKnot = knot1,
            edge = edge1,
            endKnot = knot3.withNoMetadata(),
        )

        val spline0 = OpenSpline.of(
            leadingLinks = emptyList(),
            lastLink = Spline.CompleteLink(
                startKnot = knot0,
                edge = edge0,
                endKnot = knot1,
            ),
        )

        val spline1 = OpenSpline.of(
            leadingLinks = emptyList(),
            lastLink = lastLink,
        )

        val mergedSpline = OpenSpline.merge(
            splines = listOf(spline0, spline1),
        )

        assertEqualsWithAbsoluteTolerance(
            expected = OpenSpline.of(
                leadingLinks = listOf(
                    Spline.PartialLink(
                        startKnot = knot0,
                        edge = edge0,
                    ),
                ),
                lastLink = lastLink,
            ),
            actual = mergedSpline,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testMerge_twoSplines_multipleSubCurves() {
        // spline #0
        val knot0 = Point.of(0.0, 0.0).withNoMetadata()

        val edge0 = CubicBezierCurve.Edge(
            control0 = Point.of(1.0, 1.0),
            control1 = Point.of(2.0, 1.0),
        ).withNoMetadata()

        val knot1 = Point.of(3.0, 0.0).withNoMetadata()

        val edge1 = CubicBezierCurve.Edge(
            control0 = Point.of(4.0, 1.0),
            control1 = Point.of(5.0, 1.0),
        ).withNoMetadata()

        // (shared)
        val knot2 = Point.of(6.0, 0.0).withNoMetadata()

        // (spline #1)
        val edge2 = CubicBezierCurve.Edge(
            control0 = Point.of(7.0, 1.0),
            control1 = Point.of(8.0, 1.0),
        ).withNoMetadata()

        val knot3 = Point.of(9.0, 0.0).withNoMetadata()

        val edge3 = CubicBezierCurve.Edge(
            control0 = Point.of(10.0, 1.0),
            control1 = Point.of(11.0, 1.0),
        ).withNoMetadata()

        val knot4 = Point.of(12.0, 0.0).withNoMetadata()

        val spline0 = OpenSpline.of(
            leadingLinks = listOf(
                Spline.PartialLink(
                    startKnot = knot0,
                    edge = edge0,
                ),
            ),
            lastLink = Spline.CompleteLink(
                startKnot = knot1,
                edge = edge1,
                endKnot = knot2,
            ),
        )

        val spline1 = OpenSpline.of(
            leadingLinks = listOf(
                Spline.PartialLink(
                    startKnot = knot2,
                    edge = edge2,
                ),
            ),
            lastLink = Spline.CompleteLink(
                startKnot = knot3,
                edge = edge3,
                endKnot = knot4,
            ),
        )

        val mergedSpline = OpenSpline.merge(
            splines = listOf(spline0, spline1),
        )

        val expectedMergedSpline = OpenSpline.of(
            leadingLinks = listOf(
                Spline.PartialLink(
                    startKnot = knot0,
                    edge = edge0,
                ),
                Spline.PartialLink(
                    startKnot = knot1,
                    edge = edge1,
                ),
                Spline.PartialLink(
                    startKnot = knot2,
                    edge = edge2,
                ),
            ),
            lastLink = Spline.CompleteLink(
                startKnot = knot3,
                edge = edge3,
                endKnot = knot4,
            ),
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedMergedSpline,
            actual = mergedSpline,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testMerge_multipleSplines_multipleSubCurves() {
        // TODO: Use the Link API instead of the old Segment/Terminator API

        // spline #0
        val knot0 = Point.of(0.0, 0.0).withNoMetadata()

        val edge0 = CubicBezierCurve.Edge(
            control0 = Point.of(1.0, 1.0),
            control1 = Point.of(2.0, 1.0),
        ).withNoMetadata()

        val knot1 = Point.of(3.0, 0.0).withNoMetadata()

        val edge1 = CubicBezierCurve.Edge(
            control0 = Point.of(4.0, 1.0),
            control1 = Point.of(5.0, 1.0),
        ).withNoMetadata()

        // (shared)
        val knot2 = Point.of(6.0, 0.0).withNoMetadata()

        // spline #1
        val edge2 = CubicBezierCurve.Edge(
            control0 = Point.of(7.0, 1.0),
            control1 = Point.of(8.0, 1.0),
        ).withNoMetadata()

        val knot3 = Point.of(9.0, 0.0).withNoMetadata()

        val edge3 = CubicBezierCurve.Edge(
            control0 = Point.of(10.0, 1.0),
            control1 = Point.of(11.0, 1.0),
        ).withNoMetadata()

        // (shared)
        val knot4 = Point.of(12.0, 0.0).withNoMetadata()

        // spline #2
        val edge4 = CubicBezierCurve.Edge(
            control0 = Point.of(13.0, 1.0),
            control1 = Point.of(14.0, 1.0),
        ).withNoMetadata()

        val knot5 = Point.of(15.0, 0.0).withNoMetadata()

        val edge5 = CubicBezierCurve.Edge(
            control0 = Point.of(16.0, 1.0),
            control1 = Point.of(17.0, 1.0),
        ).withNoMetadata()

        val knot6 = Point.of(18.0, 0.0).withNoMetadata()

        val spline0 = OpenSpline.of(
            leadingLinks = listOf(
                Spline.PartialLink(
                    startKnot = knot0,
                    edge = edge0,
                ),
            ),
            lastLink = Spline.CompleteLink(
                startKnot = knot1,
                edge = edge1,
                endKnot = knot2,
            ),
        )

        val spline1 = OpenSpline.of(
            leadingLinks = listOf(
                Spline.PartialLink(
                    startKnot = knot2,
                    edge = edge2,
                ),
            ),
            lastLink = Spline.CompleteLink(
                startKnot = knot3,
                edge = edge3,
                endKnot = knot4,
            ),
        )

        val spline2 = OpenSpline.of(
            leadingLinks = listOf(
                Spline.PartialLink(
                    startKnot = knot4,
                    edge = edge4,
                ),
            ),
            lastLink = Spline.CompleteLink(
                startKnot = knot5,
                edge = edge5,
                endKnot = knot6,
            ),
        )

        val mergedSpline = OpenSpline.merge(
            splines = listOf(spline0, spline1, spline2),
        )

        val expectedMergedSpline = OpenSpline.of(
            leadingLinks = listOf(
                Spline.PartialLink(
                    startKnot = knot0,
                    edge = edge0,
                ),
                Spline.PartialLink(
                    startKnot = knot1,
                    edge = edge1,
                ),
                Spline.PartialLink(
                    startKnot = knot2,
                    edge = edge2,
                ),
                Spline.PartialLink(
                    startKnot = knot3,
                    edge = edge3,
                ),
                Spline.PartialLink(
                    startKnot = knot4,
                    edge = edge4,
                ),
            ),
            lastLink = Spline.CompleteLink(
                startKnot = knot5,
                edge = edge5,
                endKnot = knot6,
            ),
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedMergedSpline,
            actual = mergedSpline,
            absoluteTolerance = eps,
        )
    }
}
