package app.geometry.splines

import app.algebra.assertEqualsWithTolerance
import app.assertEquals
import app.component10
import app.component11
import app.component12
import app.component13
import app.component14
import app.component15
import app.component16
import app.component17
import app.component18
import app.component19
import app.component20
import app.component21
import app.component22
import app.component23
import app.component24
import app.component25
import app.component26
import app.component27
import app.component6
import app.component7
import app.component8
import app.component9
import app.geometry.Point
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve.OffsetEdgeMetadata
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.splines.ClosedSpline.ContourEdgeMetadata
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

private val eps = 10e-3

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
            edgeMetadata = OffsetEdgeMetadata.Precise,
            knotMetadata = null,
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
                Spline.Segment(
                    startKnot = bezierSegment0.startKnot,
                    edge = bezierSegment0.edge,
                    edgeMetadata = ContourEdgeMetadata.Side(
                        offsetMetadata = OffsetEdgeMetadata.Precise,
                    ),
                    knotMetadata = null,
                ),
                Spline.Segment.lineSegment(
                    startKnot = knot1End,
                    edgeMetadata = ContourEdgeMetadata.Corner,
                    knotMetadata = null,
                ),
                Spline.Segment.lineSegment(
                    startKnot = Point.of(0.0, 1.0),
                    edgeMetadata = ContourEdgeMetadata.Corner,
                    knotMetadata = null,
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
            edgeMetadata = OffsetEdgeMetadata.Precise,
            knotMetadata = null,
        )

        val bezierSegment1 = Spline.Segment.bezier(
            startKnot = knot2,
            control0 = control2,
            control1 = control3,
            edgeMetadata = OffsetEdgeMetadata.Precise,
            knotMetadata = null,
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

        assertEqualsWithTolerance(
            expected = listOf(
                Spline.Segment(
                    startKnot = bezierSegment0.startKnot,
                    edge = bezierSegment0.edge,
                    edgeMetadata = ContourEdgeMetadata.Side(
                        offsetMetadata = OffsetEdgeMetadata.Precise,
                    ),
                    knotMetadata = null,
                ),
                Spline.Segment.lineSegment(
                    startKnot = knot1,
                    edgeMetadata = ContourEdgeMetadata.Corner,
                    knotMetadata = null,
                ),
                Spline.Segment.lineSegment(
                    startKnot = Point.of(3.5, 0.25),
                    edgeMetadata = ContourEdgeMetadata.Corner,
                    knotMetadata = null,
                ),
                Spline.Segment(
                    startKnot = bezierSegment1.startKnot,
                    edge = bezierSegment1.edge,
                    edgeMetadata = ContourEdgeMetadata.Side(
                        offsetMetadata = OffsetEdgeMetadata.Precise,
                    ),
                    knotMetadata = null,
                ),
                Spline.Segment.lineSegment(
                    startKnot = knot3,
                    edgeMetadata = ContourEdgeMetadata.Corner,
                    knotMetadata = null,
                ),
                Spline.Segment.lineSegment(
                    startKnot = Point.of(-0.5, 0.25),
                    edgeMetadata = ContourEdgeMetadata.Corner,
                    knotMetadata = null,
                ),
            ),
            actual = interconnectedSpline.segments,
            absoluteTolerance = eps,
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
            edgeMetadata = OffsetEdgeMetadata.Precise,
            knotMetadata = null,
        )

        val bezierSegment1 = Spline.Segment.bezier(
            startKnot = knot2,
            control0 = control2,
            control1 = control3,
            edgeMetadata = OffsetEdgeMetadata.Precise,
            knotMetadata = null,
        )

        val bezierSegment2 = Spline.Segment.bezier(
            startKnot = knot4,
            control0 = control4,
            control1 = control5,
            edgeMetadata = OffsetEdgeMetadata.Precise,
            knotMetadata = null,
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
            expected = Spline.Segment(
                startKnot = bezierSegment0.startKnot,
                edge = bezierSegment0.edge,
                edgeMetadata = ContourEdgeMetadata.Side(
                    offsetMetadata = OffsetEdgeMetadata.Precise,
                ),
                knotMetadata = null,
            ),
            actual = actualSegment0,
        )

        assertEquals(
            expected = Spline.Segment.lineSegment(
                startKnot = knot1,
                edgeMetadata = ContourEdgeMetadata.Corner,
                knotMetadata = null,
            ),
            actual = actualSegment1,
        )

        assertEquals(
            expected = Spline.Segment.lineSegment(
                startKnot = Point.of(0.0, 7.5),
                edgeMetadata = ContourEdgeMetadata.Corner,
                knotMetadata = null,
            ),
            actual = actualSegment2,
        )

        assertEquals(
            expected = Spline.Segment(
                startKnot = bezierSegment1.startKnot,
                edge = bezierSegment1.edge,
                edgeMetadata = ContourEdgeMetadata.Side(
                    offsetMetadata = OffsetEdgeMetadata.Precise,
                ),
                knotMetadata = null,
            ),
            actual = actualSegment3,
        )

        assertEquals(
            expected = Spline.Segment.lineSegment(
                startKnot = knot3,
                edgeMetadata = ContourEdgeMetadata.Corner,
                knotMetadata = null,
            ),
            actual = actualSegment4,
        )


        assertEqualsWithTolerance(
            expected = Spline.Segment.lineSegment(
                startKnot = Point.of(3.33, -0.33),
                edgeMetadata = ContourEdgeMetadata.Corner,
                knotMetadata = null,
            ),
            actual = actualSegment5,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = Spline.Segment(
                startKnot = bezierSegment2.startKnot,
                edge = bezierSegment2.edge,
                edgeMetadata = ContourEdgeMetadata.Side(
                    offsetMetadata = OffsetEdgeMetadata.Precise,
                ),
                knotMetadata = null,
            ),
            actual = actualSegment6,
        )

        assertEquals(
            expected = Spline.Segment.lineSegment(
                startKnot = knot5,
                edgeMetadata = ContourEdgeMetadata.Corner,
                knotMetadata = null,
            ),
            actual = actualSegment7,
        )

        assertEqualsWithTolerance(
            expected = Spline.Segment.lineSegment(
                startKnot = Point.of(-3.33, -0.33),
                edgeMetadata = ContourEdgeMetadata.Corner,
                knotMetadata = null,
            ),
            actual = actualSegment8,
            absoluteTolerance = eps,
        )
    }

    @Test
    @Ignore // TODO: Figure this out
    fun testFindOffset_complex() {
        val innerSpline = ClosedSpline(
            segments = listOf(
                Spline.Segment(
                    startKnot = Point.of(19.28, 19.36),
                    edge = CubicBezierCurve.Edge(
                        control0 = Point.of(13.32, 31.18),
                        control1 = Point.of(18.35, 72.59),
                    ),
                    edgeMetadata = null,
                    knotMetadata = null,
                ),
                Spline.Segment(
                    startKnot = Point.of(24.34, 78.87),
                    edge = CubicBezierCurve.Edge(
                        control0 = Point.of(53.47, 78.29),
                        control1 = Point.of(37.93, 65.40),
                    ),
                    edgeMetadata = null,
                    knotMetadata = null,
                ),
                Spline.Segment(
                    startKnot = Point.of(76.11, 65.90),
                    edge = CubicBezierCurve.Edge(
                        control0 = Point.of(71.17, 60.22),
                        control1 = Point.of(66.51, 49.56),
                    ),
                    edgeMetadata = null,
                    knotMetadata = null,
                ),
                Spline.Segment(
                    startKnot = Point.of(65.66, 43.91),
                    edge = CubicBezierCurve.Edge(
                        control0 = Point.of(55.22, 45.42),
                        control1 = Point.of(51.91, 43.18),
                    ),
                    edgeMetadata = null,
                    knotMetadata = null,
                ),
                Spline.Segment(
                    startKnot = Point.of(43.77, 46.24),
                    edge = CubicBezierCurve.Edge(
                        control0 = Point.of(42.30, 39.67),
                        control1 = Point.of(43.84, 25.10),
                    ),
                    edgeMetadata = null,
                    knotMetadata = null,
                ),
                Spline.Segment(
                    startKnot = Point.of(45.47, 20.40),
                    edge = CubicBezierCurve.Edge(
                        control0 = Point.of(35.84, 27.35),
                        control1 = Point.of(32.91, 26.87),
                    ),
                    edgeMetadata = null,
                    knotMetadata = null,
                ),
            ),
        )

        val contourSpline = assertNotNull(
            innerSpline.findContourSpline(
                offset = 10.0,
            ),
        )

        assertEquals(
            expected = contourSpline.globalOffsetDeviation,
            actual = 0.0,
            absoluteTolerance = eps,
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
            actualSegment9,
            actualSegment10,
            actualSegment11,
            actualSegment12,
            actualSegment13,
            actualSegment14,
            actualSegment15,
            actualSegment16,
            actualSegment17,
            actualSegment18,
            actualSegment19,
            actualSegment20,
            actualSegment21,
            actualSegment22,
            actualSegment23,
            actualSegment24,
            actualSegment25,
            actualSegment26,
        ) = contourSpline.segments

        // Segment #0
        val actualStartKnot0 = actualSegment0.startKnot

        assertEquals(
            expected = Point.of(10.34, 14.87),
            actual = actualStartKnot0,
            absoluteTolerance = eps,
        )

        val actualEdge0 = assertIs<CubicBezierCurve.Edge>(actualSegment0.edge)

        assertEquals(
            expected = Point.of(7.10, 21.76),
            actual = actualEdge0.control0,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = Point.of(6.74, 29.03),
            actual = actualEdge0.control1,
            absoluteTolerance = eps,
        )

        // Segment #1
        val actualStartKnot1 = actualSegment1.startKnot
        assertEquals(
            expected = Point.of(6.60, 37.46),
            actual = actualStartKnot1,
            absoluteTolerance = eps,
        )

        val actualEdge1 = assertIs<CubicBezierCurve.Edge>(actualSegment1.edge)
        assertEquals(
            expected = Point.of(6.61, 46.42),
            actual = actualEdge1.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(7.57, 56.12),
            actual = actualEdge1.control1,
            absoluteTolerance = eps,
        )

        // Segment #2
        val actualStartKnot2 = actualSegment2.startKnot
        assertEquals(
            expected = Point.of(9.10, 64.53),
            actual = actualStartKnot2,
            absoluteTolerance = eps,
        )

        val actualEdge2 = assertIs<CubicBezierCurve.Edge>(actualSegment2.edge)
        assertEquals(
            expected = Point.of(9.88, 68.74),
            actual = actualEdge2.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(10.79, 72.60),
            actual = actualEdge2.control1,
            absoluteTolerance = eps,
        )

        // Segment #3
        val actualStartKnot3 = actualSegment3.startKnot
        assertEquals(
            expected = Point.of(11.90, 76.01),
            actual = actualStartKnot3,
            absoluteTolerance = eps,
        )

        val actualEdge3 = assertIs<CubicBezierCurve.Edge>(actualSegment3.edge)
        assertEquals(
            expected = Point.of(13.15, 79.48),
            actual = actualEdge3.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(14.05, 82.35),
            actual = actualEdge3.control1,
            absoluteTolerance = eps,
        )

        // Segment #4
        val actualStartKnot4 = actualSegment4.startKnot
        assertEquals(
            expected = Point.of(17.09, 85.77),
            actual = actualStartKnot4,
            absoluteTolerance = eps,
        )

        assertIs<LineSegment.Edge>(actualSegment4.edge)

        // Segment #5
        val actualStartKnot5 = actualSegment5.startKnot
        assertEquals(
            expected = Point.of(19.93, 88.96),
            actual = actualStartKnot5,
            absoluteTolerance = eps,
        )

        assertIs<LineSegment.Edge>(actualSegment5.edge)

        // Segment #6
        val actualStartKnot6 = actualSegment6.startKnot
        assertEquals(
            expected = Point.of(24.54, 88.87),
            actual = actualStartKnot6,
            absoluteTolerance = eps,
        )

        val actualEdge6 = assertIs<CubicBezierCurve.Edge>(actualSegment6.edge)
        assertEquals(
            expected = Point.of(32.39, 88.71),
            actual = actualEdge6.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(38.08, 87.73),
            actual = actualEdge6.control1,
            absoluteTolerance = eps,
        )

        // Segment #7
        val actualStartKnot7 = actualSegment7.startKnot
        assertEquals(
            expected = Point.of(42.76, 85.93),
            actual = actualStartKnot7,
            absoluteTolerance = eps,
        )

        val actualEdge7 = assertIs<CubicBezierCurve.Edge>(actualSegment7.edge)
        assertEquals(
            expected = Point.of(47.45, 84.13),
            actual = actualEdge7.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(50.56, 81.60),
            actual = actualEdge7.control1,
            absoluteTolerance = eps,
        )

        // Segment #8
        val actualStartKnot8 = actualSegment8.startKnot
        assertEquals(
            expected = Point.of(52.42, 80.28),
            actual = actualStartKnot8,
            absoluteTolerance = eps,
        )

        val actualEdge8 = assertIs<CubicBezierCurve.Edge>(actualSegment8.edge)
        assertEquals(
            expected = Point.of(54.37, 78.91),
            actual = actualEdge8.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(55.22, 78.31),
            actual = actualEdge8.control1,
            absoluteTolerance = eps,
        )

        // Segment #9
        val actualStartKnot9 = actualSegment9.startKnot
        assertEquals(
            expected = Point.of(58.04, 77.45),
            actual = actualStartKnot9,
            absoluteTolerance = eps,
        )

        val actualEdge9 = assertIs<CubicBezierCurve.Edge>(actualSegment9.edge)
        assertEquals(
            expected = Point.of(60.83, 76.64),
            actual = actualEdge9.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(65.69, 75.88),
            actual = actualEdge9.control1,
            absoluteTolerance = eps,
        )

        // Segment #10
        val actualStartKnot10 = actualSegment10.startKnot
        assertEquals(
            expected = Point.of(73.99, 75.89),
            actual = actualStartKnot10,
            absoluteTolerance = eps,
        )

        val actualEdge10 = assertIs<CubicBezierCurve.Edge>(actualSegment10.edge)
        assertEquals(
            expected = Point.of(74.63, 75.89),
            actual = actualEdge10.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(75.30, 75.89),
            actual = actualEdge10.control1,
            absoluteTolerance = eps,
        )

        // Segment #11
        val actualStartKnot11 = actualSegment11.startKnot
        assertEquals(
            expected = Point.of(75.98, 75.90),
            actual = actualStartKnot11,
            absoluteTolerance = eps,
        )

        assertIs<LineSegment.Edge>(actualSegment11.edge)

        // Segment #12
        val actualStartKnot12 = actualSegment12.startKnot
        assertEquals(
            expected = Point.of(100.40, 76.22),
            actual = actualStartKnot12,
            absoluteTolerance = eps,
        )

        assertIs<LineSegment.Edge>(actualSegment12.edge)

        // Segment #13
        val actualStartKnot13 = actualSegment13.startKnot
        assertEquals(
            expected = Point.of(83.67, 59.33),
            actual = actualStartKnot13,
            absoluteTolerance = eps,
        )

        val actualEdge13 = assertIs<CubicBezierCurve.Edge>(actualSegment13.edge)
        assertEquals(
            expected = Point.of(80.43, 56.06),
            actual = actualEdge13.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(75.68, 45.15),
            actual = actualEdge13.control1,
            absoluteTolerance = eps,
        )

        // Segment #14
        val actualStartKnot14 = actualSegment14.startKnot
        assertEquals(
            expected = Point.of(75.56, 42.41),
            actual = actualStartKnot14,
            absoluteTolerance = eps,
        )

        assertIs<LineSegment.Edge>(actualSegment14.edge)

        // Segment #15
        val actualStartKnot15 = actualSegment15.startKnot
        assertEquals(
            expected = Point.of(75.11, 32.46),
            actual = actualStartKnot15,
            absoluteTolerance = eps,
        )

        assertIs<LineSegment.Edge>(actualSegment15.edge)

        // Segment #16
        val actualStartKnot16 = actualSegment16.startKnot
        assertEquals(
            expected = Point.of(64.23, 34.01),
            actual = actualStartKnot16,
            absoluteTolerance = eps,
        )

        val actualEdge16 = assertIs<CubicBezierCurve.Edge>(actualSegment16.edge)
        assertEquals(
            expected = Point.of(61.20, 34.45),
            actual = actualEdge16.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(59.10, 34.50),
            actual = actualEdge16.control1,
            absoluteTolerance = eps,
        )

        // Segment #17
        val actualStartKnot17 = actualSegment17.startKnot
        assertEquals(
            expected = Point.of(56.91, 34.51),
            actual = actualStartKnot17,
            absoluteTolerance = eps,
        )

        val actualEdge17 = assertIs<CubicBezierCurve.Edge>(actualSegment17.edge)
        assertEquals(
            expected = Point.of(56.03, 34.51),
            actual = actualEdge17.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(55.13, 34.49),
            actual = actualEdge17.control1,
            absoluteTolerance = eps,
        )

        // Segment #18
        val actualStartKnot18 = actualSegment18.startKnot
        assertEquals(
            expected = Point.of(54.16, 34.49),
            actual = actualStartKnot18,
            absoluteTolerance = eps,
        )

        val actualEdge18 = assertIs<CubicBezierCurve.Edge>(actualSegment18.edge)
        assertEquals(
            expected = Point.of(50.60, 34.44),
            actual = actualEdge18.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(45.63, 34.79),
            actual = actualEdge18.control1,
            absoluteTolerance = eps,
        )

        // Segment #19
        val actualStartKnot19 = actualSegment19.startKnot
        assertEquals(
            expected = Point.of(40.25, 36.88),
            actual = actualStartKnot19,
            absoluteTolerance = eps,
        )

        assertIs<LineSegment.Edge>(actualSegment19.edge)

        // Segment #20
        val actualStartKnot20 = actualSegment20.startKnot
        assertEquals(
            expected = Point.of(53.55, 44.06),
            actual = actualStartKnot20,
            absoluteTolerance = eps,
        )

        val actualEdge20 = assertIs<CubicBezierCurve.Edge>(actualSegment20.edge)
        assertEquals(
            expected = Point.of(52.44, 41.38),
            actual = actualEdge20.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(53.97, 25.16),
            actual = actualEdge20.control1,
            absoluteTolerance = eps,
        )

        // Segment #21
        val actualStartKnot21 = actualSegment21.startKnot
        assertEquals(
            expected = Point.of(54.94, 23.67),
            actual = actualStartKnot21,
            absoluteTolerance = eps,
        )

        assertIs<LineSegment.Edge>(actualSegment21.edge)

        // Segment #22
        val actualStartKnot22 = actualSegment22.startKnot
        assertEquals(
            expected = Point.of(82.94, -19.17),
            actual = actualStartKnot22,
            absoluteTolerance = eps,
        )

        assertIs<LineSegment.Edge>(actualSegment22.edge)

        // Segment #23
        val actualStartKnot23 = actualSegment23.startKnot
        assertEquals(
            expected = Point.of(39.63, 12.28),
            actual = actualStartKnot23,
            absoluteTolerance = eps,
        )

        val actualEdge23 = assertIs<CubicBezierCurve.Edge>(actualSegment23.edge)
        assertEquals(
            expected = Point.of(35.29, 15.44),
            actual = actualEdge23.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(34.27, 15.38),
            actual = actualEdge23.control1,
            absoluteTolerance = eps,
        )

        // Segment #24
        val actualStartKnot24 = actualSegment24.startKnot
        assertEquals(
            expected = Point.of(34.45, 15.31),
            actual = actualStartKnot24,
            absoluteTolerance = eps,
        )

        val actualEdge24 = assertIs<CubicBezierCurve.Edge>(actualSegment24.edge)
        assertEquals(
            expected = Point.of(34.49, 15.54),
            actual = actualEdge24.control0,
            absoluteTolerance = eps,
        )
        assertEquals(
            expected = Point.of(31.13, 14.62),
            actual = actualEdge24.control1,
            absoluteTolerance = eps,
        )

        // Segment #25
        val actualStartKnot25 = actualSegment25.startKnot
        assertEquals(
            expected = Point.of(24.10, 10.59),
            actual = actualStartKnot25,
            absoluteTolerance = eps,
        )

        assertIs<LineSegment.Edge>(actualSegment25.edge)

        // Segment #26
        val actualStartKnot26 = actualSegment26.startKnot
        assertEquals(
            expected = Point.of(14.84, 5.28),
            actual = actualStartKnot26,
            absoluteTolerance = eps,
        )

        assertIs<LineSegment.Edge>(actualSegment26.edge)
    }
}
