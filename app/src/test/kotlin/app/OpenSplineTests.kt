package app

import app.geometry.Point
import app.geometry.bezier_splines.BezierSplineEdge
import app.geometry.bezier_splines.BezierSpline.InnerLink
import app.geometry.bezier_splines.BezierSpline.TerminalLink
import app.geometry.bezier_splines.OpenSpline
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

        val plug1 = TerminalLink(endKnot = Point.of(6.0, 0.0))

        val spline1 = OpenSpline(
            links = listOf(link1, link2),
            terminalLink = plug1
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

        val plug2 = TerminalLink(endKnot = Point.of(12.0, 0.0))

        val spline2 = OpenSpline(
            links = listOf(link3, link4),
            terminalLink = plug2
        )

        val mergedSpline = OpenSpline.merge(listOf(spline1, spline2))

        assertEquals(
            expected = mergedSpline.links,
            actual = listOf(link1, link2, link3, link4),
        )

        assertEquals(
            expected = mergedSpline.terminalLink,
            actual = plug2,
        )
    }
}
