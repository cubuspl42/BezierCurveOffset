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
        // Spline #0
        val start = Point.of(0.0, 0.0)
        val control0 = Point.of(1.0, 1.0)
        val control1 = Point.of(2.0, 1.0)

        // The joint between splines #0 and #1
        val knotJoint = Point.of(3.0, 0.0)

        // Spline #1
        val control2 = Point.of(4.0, 1.0)
        val control3 = Point.of(5.0, 1.0)
        val end = Point.of(6.0, 0.0)

        val link0 = Spline.InnerLink.bezier(
            startKnot = start,
            control0 = control0,
            control1 = control1,
        )

        val link1 = Spline.InnerLink.bezier(
            startKnot = knotJoint,
            control0 = control2,
            control1 = control3,
        )

        val spline0 = OpenSpline(
            innerLinks = listOf(
                link0
            ),
            terminalLink = Spline.TerminalLink(
                endKnot = knotJoint,
            ),
        )

        val spline1 = OpenSpline(
            innerLinks = listOf(
                link1
            ),
            terminalLink = Spline.TerminalLink(
                endKnot = end,
            ),
        )

        val mergedSpline = OpenSpline.merge(
            splines = listOf(spline0, spline1),
        )

        assertEquals(
            expected = listOf(link0, link1),
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
    fun testMerge_twoSplines_multipleSubCurves() {
        // Spline #0
        val start = Point.of(0.0, 0.0)
        val control0 = Point.of(1.0, 1.0)
        val control1 = Point.of(2.0, 1.0)
        val knot0 = Point.of(3.0, 0.0)
        val control2 = Point.of(4.0, 1.0)
        val control3 = Point.of(5.0, 1.0)

        // The joint between splines #0 and #1
        val knot1Joint = Point.of(6.0, 0.0)

        // Spline #1
        val control4 = Point.of(7.0, 1.0)
        val control5 = Point.of(8.0, 1.0)
        val knot2 = Point.of(9.0, 0.0)
        val control6 = Point.of(10.0, 1.0)
        val control7 = Point.of(11.0, 1.0)
        val end = Point.of(12.0, 0.0)

        val link0 = InnerLink(
            startKnot = start,
            edge = BezierSplineEdge(
                startControl = control0,
                endControl = control1,
            ),
        )

        val link1 = InnerLink(
            startKnot = knot0,
            edge = BezierSplineEdge(
                startControl = control2,
                endControl = control3,
            ),
        )

        val terminalLink1 = TerminalLink(
            endKnot = knot1Joint,
        )

        val spline0 = OpenSpline(
            innerLinks = listOf(link0, link1),
            terminalLink = terminalLink1,
        )

        val link2 = InnerLink(
            startKnot = knot1Joint,
            edge = BezierSplineEdge(
                startControl = control4,
                endControl = control5,
            ),
        )

        val link3 = InnerLink(
            startKnot = knot2,
            edge = BezierSplineEdge(
                startControl = control6,
                endControl = control7,
            ),
        )

        val terminalLink2 = TerminalLink(
            endKnot = end,
        )

        val spline1 = OpenSpline(
            innerLinks = listOf(link2, link3),
            terminalLink = terminalLink2,
        )

        val mergedSpline = OpenSpline.merge(
            splines = listOf(spline0, spline1),
        )

        assertEquals(
            expected = listOf(link0, link1, link2, link3),
            actual = mergedSpline.innerLinks,
        )

        assertEquals(
            expected = terminalLink2,
            actual = mergedSpline.terminalLink,
        )
    }

    @Test
    fun testMerge_multipleSplines_multipleSubCurves() {
        // Spline #0
        val start = Point.of(0.0, 0.0)
        val control0 = Point.of(1.0, 1.0)
        val control1 = Point.of(2.0, 1.0)
        val knot0 = Point.of(3.0, 0.0)
        val control2 = Point.of(4.0, 1.0)
        val control3 = Point.of(5.0, 1.0)

        // The joint between splines #0 and #1
        val knot1Joint = Point.of(6.0, 0.0)

        // Spline #1
        val control4 = Point.of(7.0, 1.0)
        val control5 = Point.of(8.0, 1.0)
        val knot2 = Point.of(9.0, 0.0)
        val control6 = Point.of(10.0, 1.0)
        val control7 = Point.of(11.0, 1.0)

        // The joint between splines #1 and #2
        val knot3Joint = Point.of(12.0, 0.0)

        // Spline #2
        val control8 = Point.of(13.0, 1.0)
        val control9 = Point.of(14.0, 1.0)
        val knot4 = Point.of(15.0, 0.0)
        val control10 = Point.of(16.0, 1.0)
        val control11 = Point.of(17.0, 1.0)
        val end = Point.of(18.0, 0.0)

        val link1 = InnerLink(
            startKnot = start,
            edge = BezierSplineEdge(
                startControl = control0,
                endControl = control1,
            ),
        )

        val link2 = InnerLink(
            startKnot = knot0,
            edge = BezierSplineEdge(
                startControl = control2,
                endControl = control3,
            ),
        )

        val terminalLink1 = TerminalLink(
            endKnot = knot1Joint,
        )

        val spline0 = OpenSpline(
            innerLinks = listOf(link1, link2),
            terminalLink = terminalLink1,
        )

        val link3 = InnerLink(
            startKnot = knot1Joint,
            edge = BezierSplineEdge(
                startControl = control4,
                endControl = control5,
            ),
        )

        val link4 = InnerLink(
            startKnot = knot2,
            edge = BezierSplineEdge(
                startControl = control6,
                endControl = control7,
            ),
        )

        val terminalLink2 = TerminalLink(
            endKnot = knot3Joint,
        )

        val spline1 = OpenSpline(
            innerLinks = listOf(link3, link4),
            terminalLink = terminalLink2,
        )

        val link5 = InnerLink(
            startKnot = knot3Joint,
            edge = BezierSplineEdge(
                startControl = control8,
                endControl = control9,
            ),
        )

        val link6 = InnerLink(
            startKnot = knot4,
            edge = BezierSplineEdge(
                startControl = control10,
                endControl = control11,
            ),
        )

        val terminalLink3 = TerminalLink(
            endKnot = end,
        )

        val spline2 = OpenSpline(
            innerLinks = listOf(link5, link6),
            terminalLink = terminalLink3,
        )

        val mergedSpline = OpenSpline.merge(
            listOf(spline0, spline1, spline2),
        )

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
