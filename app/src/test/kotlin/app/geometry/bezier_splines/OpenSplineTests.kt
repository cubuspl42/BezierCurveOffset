package app.geometry.bezier_splines

import app.geometry.Point
import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_splines.Spline.Segment
import app.geometry.bezier_splines.Spline.Terminator
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenSplineTests {
    @Test
    fun testMerge_singleSpline_singleSubCurve() {
        val knot0Start = Point.of(0.0, 0.0)
        val control0 = Point.of(1.0, 1.0)
        val control1 = Point.of(2.0, 1.0)
        val knot1End = Point.of(3.0, 0.0)

        val links = listOf(
            Segment(
                startKnot = knot0Start,
                edge = BezierCurve.Edge(
                    startControl = control0,
                    endControl = control1
                ),
            ),
        )

        val terminator = Terminator(
            endKnot = knot1End,
        )

        val spline = OpenSpline(
            segments = links,
            terminator = terminator,
        )

        val mergedSpline = OpenSpline.merge(listOf(spline))

        assertEquals(
            expected = links,
            actual = mergedSpline.segments,
        )

        assertEquals(
            expected = terminator,
            actual = mergedSpline.terminator,
        )
    }

    @Test
    fun testMerge_singleSpline_multipleSubCurves() {
        val links = listOf(
            Segment(
                startKnot = Point.of(0.0, 0.0),
                edge = BezierCurve.Edge(
                    startControl = Point.of(1.0, 1.0),
                    endControl = Point.of(2.0, 1.0),
                ),
            ),
            Segment(
                startKnot = Point.of(3.0, 0.0),
                edge = BezierCurve.Edge(
                    startControl = Point.of(4.0, 1.0),
                    endControl = Point.of(5.0, 1.0)
                ),
            ),
        )

        val terminator = Terminator(
            endKnot = Point.of(6.0, 0.0),
        )

        val spline = OpenSpline(
            segments = links,
            terminator = terminator,
        )

        val mergedSpline = OpenSpline.merge(listOf(spline))

        assertEquals(
            expected = links,
            actual = mergedSpline.segments,
        )

        assertEquals(
            expected = terminator,
            actual = mergedSpline.terminator,
        )
    }

    @Test
    fun testMerge_twoSplines_singleSubCurve() {
        // Spline #0
        val start = Point.of(0.0, 0.0)
        val control0 = Point.of(1.0, 1.0)
        val control1 = Point.of(2.0, 1.0)

        // The joint between splines #0 and #1
        val knot0Joint = Point.of(3.0, 0.0)

        // Spline #1
        val control2 = Point.of(4.0, 1.0)
        val control3 = Point.of(5.0, 1.0)
        val end = Point.of(6.0, 0.0)

        val link0 = Spline.Segment.bezier(
            startKnot = start,
            control0 = control0,
            control1 = control1,
        )

        val link1 = Spline.Segment.bezier(
            startKnot = knot0Joint,
            control0 = control2,
            control1 = control3,
        )

        val spline0 = OpenSpline(
            segments = listOf(
                link0
            ),
            terminator = Spline.Terminator(
                endKnot = knot0Joint,
            ),
        )

        val spline1 = OpenSpline(
            segments = listOf(
                link1
            ),
            terminator = Spline.Terminator(
                endKnot = end,
            ),
        )

        val mergedSpline = OpenSpline.merge(
            splines = listOf(spline0, spline1),
        )

        assertEquals(
            expected = listOf(link0, link1),
            actual = mergedSpline.segments,
        )

        assertEquals(
            expected = Spline.Terminator(
                endKnot = end,
            ),
            actual = mergedSpline.terminator,
        )
    }

    @Test
    fun testMerge_twoSplines_multipleSubCurves() {
        // Spline #0
        val knot0Start = Point.of(0.0, 0.0)
        val control0 = Point.of(1.0, 1.0)
        val control1 = Point.of(2.0, 1.0)
        val knot1 = Point.of(3.0, 0.0)
        val control2 = Point.of(4.0, 1.0)
        val control3 = Point.of(5.0, 1.0)

        // The joint between splines #0 and #1
        val knot2Joint = Point.of(6.0, 0.0)

        // Spline #1
        val control4 = Point.of(7.0, 1.0)
        val control5 = Point.of(8.0, 1.0)
        val knot3 = Point.of(9.0, 0.0)
        val control6 = Point.of(10.0, 1.0)
        val control7 = Point.of(11.0, 1.0)
        val knot4End = Point.of(12.0, 0.0)

        val link0 = Segment(
            startKnot = knot0Start,
            edge = BezierCurve.Edge(
                startControl = control0,
                endControl = control1,
            ),
        )

        val link1 = Segment(
            startKnot = knot1,
            edge = BezierCurve.Edge(
                startControl = control2,
                endControl = control3,
            ),
        )

        val terminator1 = Terminator(
            endKnot = knot2Joint,
        )

        val spline0 = OpenSpline(
            segments = listOf(link0, link1),
            terminator = terminator1,
        )

        val link2 = Segment(
            startKnot = knot2Joint,
            edge = BezierCurve.Edge(
                startControl = control4,
                endControl = control5,
            ),
        )

        val link3 = Segment(
            startKnot = knot3,
            edge = BezierCurve.Edge(
                startControl = control6,
                endControl = control7,
            ),
        )

        val terminator2 = Terminator(
            endKnot = knot4End,
        )

        val spline1 = OpenSpline(
            segments = listOf(link2, link3),
            terminator = terminator2,
        )

        val mergedSpline = OpenSpline.merge(
            splines = listOf(spline0, spline1),
        )

        assertEquals(
            expected = listOf(link0, link1, link2, link3),
            actual = mergedSpline.segments,
        )

        assertEquals(
            expected = terminator2,
            actual = mergedSpline.terminator,
        )
    }

    @Test
    fun testMerge_multipleSplines_multipleSubCurves() {
        // Spline #0
        val knot0Start = Point.of(0.0, 0.0)
        val control0 = Point.of(1.0, 1.0)
        val control1 = Point.of(2.0, 1.0)
        val knot1 = Point.of(3.0, 0.0)
        val control2 = Point.of(4.0, 1.0)
        val control3 = Point.of(5.0, 1.0)

        // The joint between splines #0 and #1
        val knot2Joint = Point.of(6.0, 0.0)

        // Spline #1
        val control4 = Point.of(7.0, 1.0)
        val control5 = Point.of(8.0, 1.0)
        val knot3 = Point.of(9.0, 0.0)
        val control6 = Point.of(10.0, 1.0)
        val control7 = Point.of(11.0, 1.0)

        // The joint between splines #1 and #2
        val knot4Joint = Point.of(12.0, 0.0)

        // Spline #2
        val control8 = Point.of(13.0, 1.0)
        val control9 = Point.of(14.0, 1.0)
        val knot5 = Point.of(15.0, 0.0)
        val control10 = Point.of(16.0, 1.0)
        val control11 = Point.of(17.0, 1.0)
        val knot6End = Point.of(18.0, 0.0)

        val link1 = Segment(
            startKnot = knot0Start,
            edge = BezierCurve.Edge(
                startControl = control0,
                endControl = control1,
            ),
        )

        val link2 = Segment(
            startKnot = knot1,
            edge = BezierCurve.Edge(
                startControl = control2,
                endControl = control3,
            ),
        )

        val terminator1 = Terminator(
            endKnot = knot2Joint,
        )

        val spline0 = OpenSpline(
            segments = listOf(link1, link2),
            terminator = terminator1,
        )

        val link3 = Segment(
            startKnot = knot2Joint,
            edge = BezierCurve.Edge(
                startControl = control4,
                endControl = control5,
            ),
        )

        val link4 = Segment(
            startKnot = knot3,
            edge = BezierCurve.Edge(
                startControl = control6,
                endControl = control7,
            ),
        )

        val terminator2 = Terminator(
            endKnot = knot4Joint,
        )

        val spline1 = OpenSpline(
            segments = listOf(link3, link4),
            terminator = terminator2,
        )

        val link5 = Segment(
            startKnot = knot4Joint,
            edge = BezierCurve.Edge(
                startControl = control8,
                endControl = control9,
            ),
        )

        val link6 = Segment(
            startKnot = knot5,
            edge = BezierCurve.Edge(
                startControl = control10,
                endControl = control11,
            ),
        )

        val terminator3 = Terminator(
            endKnot = knot6End,
        )

        val spline2 = OpenSpline(
            segments = listOf(link5, link6),
            terminator = terminator3,
        )

        val mergedSpline = OpenSpline.merge(
            listOf(spline0, spline1, spline2),
        )

        assertEquals(
            expected = listOf(link1, link2, link3, link4, link5, link6),
            actual = mergedSpline.segments,
        )

        assertEquals(
            expected = terminator3,
            actual = mergedSpline.terminator,
        )
    }
}
