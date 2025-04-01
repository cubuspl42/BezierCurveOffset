package app.geometry.bezier_splines

import app.geometry.Point
import app.geometry.bezier_splines.BezierSpline.InnerLink
import app.geometry.bezier_splines.BezierSpline.TerminalLink
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenSplineTests {
    @Test
    fun testMerge_singleSpline_singleLink() {
        val links = listOf(
            InnerLink(
                startKnot = Point.of(0.0, 0.0),
                edge = BezierSplineEdge(
                    startControl = Point.of(1.0, 1.0),
                    endControl = Point.of(2.0, 1.0)
                ),
            ),
        )

        val terminalLink = TerminalLink(
            endKnot = Point.of(3.0, 0.0),
        )

        val spline = OpenSpline(
            links = links,
            terminalLink = terminalLink,
        )

        val mergedSpline = OpenSpline.merge(listOf(spline))

        assertEquals(
            expected = links,
            actual = mergedSpline.links,
        )

        assertEquals(
            expected = terminalLink,
            actual = mergedSpline.terminalLink,
        )
    }

    @Test
    fun testMerge_singleSpline_multipleLinks() {
        val links = listOf(
            InnerLink(
                startKnot = Point.of(0.0, 0.0),
                edge = BezierSplineEdge(
                    startControl = Point.of(1.0, 1.0),
                    endControl = Point.of(2.0, 1.0),
                ),
            ),
            InnerLink(
                startKnot = Point.of(3.0, 0.0),
                edge = BezierSplineEdge(
                    startControl = Point.of(4.0, 1.0),
                    endControl = Point.of(5.0, 1.0)
                ),
            ),
        )

        val terminalLink = TerminalLink(
            endKnot = Point.of(6.0, 0.0),
        )

        val spline = OpenSpline(
            links = links,
            terminalLink = terminalLink,
        )

        val mergedSpline = OpenSpline.merge(listOf(spline))

        assertEquals(
            expected = links,
            actual = mergedSpline.links,
        )

        assertEquals(
            expected = terminalLink,
            actual = mergedSpline.terminalLink,
        )
    }

    @Test
    @Ignore // TODO: Fix bugs related to merging one-segment splines
    fun testMerge_twoSplines() {
        val start = Point.of(0.0, 0.0)
        val control0 = Point.of(1.0, 1.0)
        val control1 = Point.of(2.0, 1.0)
        val mid = Point.of(3.0, 0.0)
        val control2 = Point.of(4.0, 1.0)
        val control3 = Point.of(5.0, 1.0)
        val end = Point.of(6.0, 0.0)

        val firstSpline = OpenSpline(
            links = listOf(
                BezierSpline.InnerLink.bezier(
                    startKnot = start,
                    control0 = control0,
                    control1 = control1,
                )
            ),
            terminalLink = BezierSpline.TerminalLink(
                endKnot = mid,
            ),
        )

        val secondSpline = OpenSpline(
            links = listOf(
                BezierSpline.InnerLink.bezier(
                    startKnot = mid,
                    control0 = control2,
                    control1 = control3,
                )
            ),
            terminalLink = BezierSpline.TerminalLink(
                endKnot = end,
            ),
        )

        val mergedSpline = OpenSpline.merge(
            splines = listOf(firstSpline, secondSpline),
        )

        assertEquals(
            expected = listOf(
                BezierSpline.InnerLink.bezier(
                    startKnot = start,
                    control0 = control0,
                    control1 = control1,
                ),
                BezierSpline.InnerLink.bezier(
                    startKnot = mid,
                    control0 = control2,
                    control1 = control3,
                ),
            ),
            actual = mergedSpline.links,
        )

        assertEquals(
            expected = BezierSpline.TerminalLink(
                endKnot = end,
            ),
            actual = mergedSpline.terminalLink,
        )
    }

    @Test
    fun testMerge_multipleSplines() {
        val link1 = InnerLink(
            startKnot = Point.of(0.0, 0.0),
            edge = BezierSplineEdge(
                startControl = Point.of(1.0, 1.0),
                endControl = Point.of(2.0, 1.0)
            ),
        )

        val link2 = InnerLink(
            startKnot = Point.of(3.0, 0.0),
            edge = BezierSplineEdge(
                startControl = Point.of(4.0, 1.0),
                endControl = Point.of(5.0, 1.0)
            ),
        )

        val terminalLink1 = TerminalLink(endKnot = Point.of(6.0, 0.0))

        val spline1 = OpenSpline(
            links = listOf(link1, link2),
            terminalLink = terminalLink1,
        )

        val link3 = InnerLink(
            startKnot = Point.of(6.0, 0.0),
            edge = BezierSplineEdge(
                startControl = Point.of(7.0, -1.0),
                endControl = Point.of(8.0, -1.0)
            ),
        )

        val link4 = InnerLink(
            startKnot = Point.of(9.0, 0.0),
            edge = BezierSplineEdge(
                startControl = Point.of(10.0, 1.0),
                endControl = Point.of(11.0, 1.0)
            ),
        )

        val terminalLink2 = TerminalLink(endKnot = Point.of(12.0, 0.0))

        val spline2 = OpenSpline(
            links = listOf(link3, link4),
            terminalLink = terminalLink2,
        )

        val mergedSpline = OpenSpline.merge(listOf(spline1, spline2))

        assertEquals(
            expected = listOf(link1, link2, link3, link4),
            actual = mergedSpline.links,
        )

        assertEquals(
            expected = terminalLink2,
            actual = mergedSpline.terminalLink,
        )
    }
}
