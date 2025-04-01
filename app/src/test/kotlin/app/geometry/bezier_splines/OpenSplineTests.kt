package app.geometry.bezier_splines

import app.geometry.Point
import app.geometry.bezier_splines.Spline.InnerLink
import app.geometry.bezier_splines.Spline.TerminalLink
import kotlin.test.Test
import kotlin.test.assertEquals

class OpenSplineTests {
    @Test
    fun testMerge_singleSpline_singleSubCurve() {
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
            innerLinks = links,
            terminalLink = terminalLink,
        )

        val mergedSpline = OpenSpline.merge(listOf(spline))

        assertEquals(
            expected = links,
            actual = mergedSpline.innerLinks,
        )

        assertEquals(
            expected = terminalLink,
            actual = mergedSpline.terminalLink,
        )
    }

    @Test
    fun testMerge_singleSpline_multipleSubCurves() {
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
            innerLinks = links,
            terminalLink = terminalLink,
        )

        val mergedSpline = OpenSpline.merge(listOf(spline))

        assertEquals(
            expected = links,
            actual = mergedSpline.innerLinks,
        )

        assertEquals(
            expected = terminalLink,
            actual = mergedSpline.terminalLink,
        )
    }

    @Test
    fun testMerge_twoSplines_singleSubCurve() {
        val start = Point.of(0.0, 0.0)
        val control0 = Point.of(1.0, 1.0)
        val control1 = Point.of(2.0, 1.0)
        val mid = Point.of(3.0, 0.0)
        val control2 = Point.of(4.0, 1.0)
        val control3 = Point.of(5.0, 1.0)
        val end = Point.of(6.0, 0.0)

        val firstSpline = OpenSpline(
            innerLinks = listOf(
                Spline.InnerLink.bezier(
                    startKnot = start,
                    control0 = control0,
                    control1 = control1,
                )
            ),
            terminalLink = Spline.TerminalLink(
                endKnot = mid,
            ),
        )

        val secondSpline = OpenSpline(
            innerLinks = listOf(
                Spline.InnerLink.bezier(
                    startKnot = mid,
                    control0 = control2,
                    control1 = control3,
                )
            ),
            terminalLink = Spline.TerminalLink(
                endKnot = end,
            ),
        )

        val mergedSpline = OpenSpline.merge(
            splines = listOf(firstSpline, secondSpline),
        )

        assertEquals(
            expected = listOf(
                Spline.InnerLink.bezier(
                    startKnot = start,
                    control0 = control0,
                    control1 = control1,
                ),
                Spline.InnerLink.bezier(
                    startKnot = mid,
                    control0 = control2,
                    control1 = control3,
                ),
            ),
            actual = mergedSpline.innerLinks,
        )

        assertEquals(
            expected = Spline.TerminalLink(
                endKnot = end,
            ),
            actual = mergedSpline.terminalLink,
        )
    }

    @Test
    fun testMerge_multipleSplines_multipleSubCurves() {
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
            innerLinks = listOf(link1, link2),
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
            innerLinks = listOf(link3, link4),
            terminalLink = terminalLink2,
        )

        val link5 = InnerLink(
            startKnot = Point.of(12.0, 0.0),
            edge = BezierSplineEdge(
                startControl = Point.of(13.0, -1.0),
                endControl = Point.of(14.0, -1.0)
            ),
        )

        val link6 = InnerLink(
            startKnot = Point.of(15.0, 0.0),
            edge = BezierSplineEdge(
                startControl = Point.of(16.0, 1.0),
                endControl = Point.of(17.0, 1.0)
            ),
        )

        val terminalLink3 = TerminalLink(endKnot = Point.of(18.0, 0.0))

        val spline3 = OpenSpline(
            innerLinks = listOf(link5, link6),
            terminalLink = terminalLink3,
        )

        val mergedSpline = OpenSpline.merge(listOf(spline1, spline2, spline3))

        assertEquals(
            expected = listOf(link1, link2, link3, link4, link5, link6),
            actual = mergedSpline.innerLinks,
        )

        assertEquals(
            expected = terminalLink3,
            actual = mergedSpline.terminalLink,
        )
    }
}
