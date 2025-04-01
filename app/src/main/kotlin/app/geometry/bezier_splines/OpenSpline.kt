package app.geometry.bezier_splines

import app.geometry.Point
import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.SegmentCurve
import app.interleave
import app.mapFirst
import app.withPreviousOrNull

class OpenSpline(
    /**
     * The path of links, must not be empty
     */
    override val segments: List<Segment>,
    /**
     * The plug node that terminates the path of links
     */
    val terminator: Terminator,
) : Spline() {
    companion object {
        fun ofEdge(
            startKnot: Point,
            edge: SegmentCurve.Edge,
            endKnot: Point,
        ): OpenSpline = OpenSpline(
            segments = listOf(
                Segment(
                    startKnot = startKnot,
                    edge = edge,
                ),
            ),
            terminator = Terminator(
                endKnot = endKnot,
            ),
        )

        fun fastenLinksSmoothly(
            prevSpline: OpenSpline,
            nextSpline: OpenSpline,
        ): Pair<Segment, Segment> {
            val prevLink = prevSpline.lastLink
            val nextLink = nextSpline.firstLink

            return Pair(prevLink, nextLink)

            val prevEdge = prevLink.edge as BezierCurve.Edge
            val prevTerminalLink = prevSpline.terminator
            val nextEdge = nextLink.edge as BezierCurve.Edge

            val fixedKnot = Point.midPoint(
                prevTerminalLink.endKnot,
                nextLink.startKnot,
            )

            val firstControl = prevEdge.endControl
            val secondControl = nextEdge.startControl

            val (fixedFirstControl, fixedSecondControl) = Point.makeCollinear(
                a = firstControl,
                b = secondControl,
                base = fixedKnot,
            )

            return Pair(
                prevLink.copy(
                    edge = prevEdge.copy(
                        endControl = fixedFirstControl,
                    ),
                ),
                nextLink.copy(
                    startKnot = fixedKnot,
                    edge = nextEdge.copy(
                        startControl = fixedSecondControl,
                    ),
                ),
            )
        }

        fun fastenSplines(
            splines: List<OpenSpline>,
        ): List<Segment> = splines.interleave(
            transform = {
                it.insideLinks
            },
            separate = { prevSpline, nextSpline ->
                fastenLinksSmoothly(
                    prevSpline = prevSpline,
                    nextSpline = nextSpline,
                ).toList()
            },
        ).flatten()

        fun merge(
            splines: List<OpenSpline>,
        ): OpenSpline {
            require(splines.isNotEmpty())

            if (splines.size == 1) {
                return splines.single()
            }

            val segments = splines.withPreviousOrNull().flatMap { (prevSpline, spline) ->
                when {
                    prevSpline != null -> spline.segments.mapFirst { firstLink ->
                        val prevSplineEndEndKnot = prevSpline.terminator.endKnot
                        val splineStartKnot = firstLink.startKnot

                        firstLink.copy(
                            startKnot = Point.midPoint(prevSplineEndEndKnot, splineStartKnot),
                        )

                    }

                    else -> spline.segments
                }
            }

            val lastSpline = splines.last()
            val terminalLink = lastSpline.terminator

            return OpenSpline(
                segments = segments,
                terminator = terminalLink,
            )
        }
    }

    init {
        require(segments.isNotEmpty())
    }

    val insideLinks: List<Segment>
        get() = segments.drop(1).dropLast(1)

    fun mergeWith(
        rightSubSplitCurve: OpenSpline,
    ): OpenSpline = OpenSpline.merge(
        splines = listOf(this, rightSubSplitCurve),
    )

    override val nodes: List<Link> = segments + terminator

    override val rightEdgeNode: Link
        get() = terminator
}
