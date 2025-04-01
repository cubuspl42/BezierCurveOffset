package app.geometry.bezier_splines

import app.geometry.Point
import org.junit.jupiter.api.assertThrows
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class ClosedSplineTests {
    @Test
    fun testInterconnect_singleSpline() {
        val knot0Start = Point.of(0.0, 0.0)
        val control0 = Point.of(1.0, 1.0)
        val control1 = Point.of(2.0, 1.0)
        val knot1End = Point.of(3.0, 0.0)

        val segments = listOf(
            Spline.Segment.bezier(
                startKnot = knot0Start,
                control0 = control0,
                control1 = control1,
            ),
        )

        val terminator = Spline.Terminator(
            endKnot = knot1End,
        )

        val spline = OpenSpline(
            segments = segments,
            terminator = terminator,
        )

        assertThrows<IllegalArgumentException> {
            ClosedSpline.interconnect(
                splines = listOf(spline)
            )
        }
    }

    @Test
    @Ignore // TODO: Fix issues with interconnecting
    fun testInterconnect_twoSplines_singleCurve() {
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
            control1 = control1
        )

        val bezierSegment1 = Spline.Segment.bezier(
            startKnot = knot2,
            control0 = control2,
            control1 = control3
        )

        val spline0 = OpenSpline(
            segments = listOf(
                bezierSegment0,
            ),
            terminator = Spline.Terminator(
                endKnot = knot1,
            )
        )

        val spline1 = OpenSpline(
            segments = listOf(
                bezierSegment1,
            ),
            terminator = Spline.Terminator(
                endKnot = knot3,
            )
        )

        val interconnectedSpline = ClosedSpline.interconnect(
            splines = listOf(spline0, spline1)
        )

        // FIXME: Remove duplicates!
        // TODO: Expect the line segments?
        assertEquals(
            expected = listOf(
                bezierSegment0,
                bezierSegment1,
            ),
            actual = interconnectedSpline.segments,
        )
    }
}
