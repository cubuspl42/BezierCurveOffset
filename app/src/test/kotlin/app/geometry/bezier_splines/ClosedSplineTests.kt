package app.geometry.bezier_splines

import app.assertEquals
import app.component6
import app.component7
import app.component8
import app.component9
import app.geometry.Point
import app.geometry.Subline
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

private val eps = 10e3

class ClosedSplineTests {
    @Test
    fun testInterconnect_singleSpline() {
        val knot0Start = Point.of(-0.5, 0.0)
        val control0 = Point.of(-1.0, -1.0)
        val control1 = Point.of(1.0, -1.0)
        val knot1End = Point.of(0.5, 0.0)

        val bezierSegment0 = Spline.Segment.bezier(
            startKnot = knot0Start,
            control0 = control0,
            control1 = control1,
        )

        val spline = OpenSpline(
            segments = listOf(
                bezierSegment0,
            ),
            terminator = Spline.Terminator(
                endKnot = knot1End,
            ),
        )

        val interconnectedSpline = ClosedSpline.interconnect(
            splines = listOf(spline),
        )

        assertEquals(
            expected = listOf(
                bezierSegment0,
                Spline.Segment.subline(
                    startKnot = knot1End,
                ),
                Spline.Segment.subline(
                    startKnot = Point.of(0.0, 1.0),
                ),
            ),
            actual = interconnectedSpline.segments,
        )
    }

    @Test
    fun testInterconnect_twoSplines() {
        val knot0 = Point.of(0.0, 0.5)
        val control0 = Point.of(1.0, 1.0)
        val control1 = Point.of(2.0, 1.0)
        val knot1 = Point.of(3.0, 0.5)

        val knot2 = Point.of(3.0, -0.5)
        val control2 = Point.of(2.0, -2.0)
        val control3 = Point.of(1.0, -2.0)
        val knot3 = Point.of(0.0, -0.5)

        val bezierSegment0 = Spline.Segment.bezier(
            startKnot = knot0,
            control0 = control0,
            control1 = control1,
        )

        val bezierSegment1 = Spline.Segment.bezier(
            startKnot = knot2,
            control0 = control2,
            control1 = control3,
        )

        val spline0 = OpenSpline(
            segments = listOf(
                bezierSegment0,
            ),
            terminator = Spline.Terminator(
                endKnot = knot1,
            ),
        )

        val spline1 = OpenSpline(
            segments = listOf(
                bezierSegment1,
            ),
            terminator = Spline.Terminator(
                endKnot = knot3,
            ),
        )

        val interconnectedSpline = ClosedSpline.interconnect(
            splines = listOf(spline0, spline1),
        )

        assertEquals(
            expected = listOf(
                bezierSegment0,
                Spline.Segment.subline(
                    startKnot = knot1,
                ),
                Spline.Segment.subline(
                    startKnot = Point.of(3.5, 0.25),
                ),
                bezierSegment1,
                Spline.Segment.subline(
                    startKnot = knot3,
                ),
                Spline.Segment.subline(
                    startKnot = Point.of(-0.5, 0.25),
                ),
            ),
            actual = interconnectedSpline.segments,
        )
    }

    @Test
    fun testInterconnect_threeSplines() {
        val knot0 = Point.of(-3.0, -1.0)
        val control0 = Point.of(-2.5, -2.0)
        val control1 = Point.of(-1.5, -3.0)
        val knot1 = Point.of(-0.5, 4.0)

        val knot2 = Point.of(0.5, 4.0)
        val control2 = Point.of(1.5, -3.0)
        val control3 = Point.of(2.5, -2.0)
        val knot3 = Point.of(3.0, -1.0)

        val knot4 = Point.of(2.0, 1.0)
        val control4 = Point.of(1.0, 2.0)
        val control5 = Point.of(-1.0, 2.0)
        val knot5 = Point.of(-2.0, 1.0)

        val bezierSegment0 = Spline.Segment.bezier(
            startKnot = knot0,
            control0 = control0,
            control1 = control1,
        )

        val bezierSegment1 = Spline.Segment.bezier(
            startKnot = knot2,
            control0 = control2,
            control1 = control3,
        )

        val bezierSegment2 = Spline.Segment.bezier(
            startKnot = knot4,
            control0 = control4,
            control1 = control5,
        )

        val spline0 = OpenSpline(
            segments = listOf(
                bezierSegment0,
            ),
            terminator = Spline.Terminator(
                endKnot = knot1,
            ),
        )

        val spline1 = OpenSpline(
            segments = listOf(
                bezierSegment1,
            ),
            terminator = Spline.Terminator(
                endKnot = knot3,
            ),
        )

        val spline2 = OpenSpline(
            segments = listOf(
                bezierSegment2,
            ),
            terminator = Spline.Terminator(
                endKnot = knot5,
            ),
        )

        val interconnectedSpline = ClosedSpline.interconnect(
            splines = listOf(spline0, spline1, spline2),
        )

        val (
            actualSegment0,
            actualSegment1,
            actualSegment2,
            actualSegment3,
            actualSegment4,
            actualSegment5,
            actualSegment6,
            actualSegment7,
            actualSegment8,
        ) = interconnectedSpline.segments

        assertEquals(
            expected = bezierSegment0,
            actual = actualSegment0,
        )

        assertEquals(
            expected = Spline.Segment.subline(
                startKnot = knot1,
            ),
            actual = actualSegment1,
        )

        assertEquals(
            expected = Spline.Segment.subline(
                startKnot = Point.of(0.0, 7.5),
            ),
            actual = actualSegment2,
        )

        assertEquals(
            expected = bezierSegment1,
            actual = actualSegment3,
        )

        assertEquals(
            expected = Spline.Segment.subline(
                startKnot = knot3,
            ),
            actual = actualSegment4,
        )

        assertIs<Subline.Edge>(
            actualSegment5.edge,
        )

        assertEquals(
            expected = Point.of(3.33, -0.33),
            actual = actualSegment5.startKnot,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = bezierSegment2,
            actual = actualSegment6,
        )

        assertEquals(
            expected = Spline.Segment.subline(
                startKnot = knot5,
            ),
            actual = actualSegment7,
        )

        assertIs<Subline.Edge>(
            actualSegment8.edge,
        )

        assertEquals(
            expected = Point.of(-3.33, -0.33),
            actual = actualSegment8.startKnot,
            absoluteTolerance = eps,
        )
    }
}
