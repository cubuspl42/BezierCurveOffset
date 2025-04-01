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
        // TODO: Nuke?
        fun fastenSegmentsSmoothly(
            prevSpline: OpenSpline,
            nextSpline: OpenSpline,
        ): Pair<Segment, Segment> {
            val prevNode = prevSpline.lastSegment
            val nextNode = nextSpline.firstSegment

            return Pair(prevNode, nextNode)

            val prevEdge = prevNode.edge as BezierCurve.Edge
            val prevTerminalNode = prevSpline.terminator
            val nextEdge = nextNode.edge as BezierCurve.Edge

            val fixedKnot = Point.midPoint(
                prevTerminalNode.endKnot,
                nextNode.startKnot,
            )

            val firstControl = prevEdge.control1
            val secondControl = nextEdge.control0

            val (fixedFirstControl, fixedSecondControl) = Point.makeCollinear(
                a = firstControl,
                b = secondControl,
                base = fixedKnot,
            )

            return Pair(
                prevNode.copy(
                    edge = prevEdge.copy(
                        control1 = fixedFirstControl,
                    ),
                ),
                nextNode.copy(
                    startKnot = fixedKnot,
                    edge = nextEdge.copy(
                        control0 = fixedSecondControl,
                    ),
                ),
            )
        }

        // TODO: Nuke?
        fun fastenSplines(
            splines: List<OpenSpline>,
        ): List<Segment> = splines.interleave(
            transform = {
                it.insideSegments
            },
            separate = { prevSpline, nextSpline ->
                fastenSegmentsSmoothly(
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
                    prevSpline != null -> spline.segments.mapFirst { firstNode ->
                        val prevSplineEndEndKnot = prevSpline.terminator.endKnot
                        val splineStartKnot = firstNode.startKnot

                        firstNode.copy(
                            startKnot = Point.midPoint(prevSplineEndEndKnot, splineStartKnot),
                        )

                    }

                    else -> spline.segments
                }
            }

            val lastSpline = splines.last()
            val terminalNode = lastSpline.terminator

            return OpenSpline(
                segments = segments,
                terminator = terminalNode,
            )
        }
    }

    init {
        require(segments.isNotEmpty())
    }

    val insideSegments: List<Segment>
        get() = segments.drop(1).dropLast(1)

    fun mergeWith(
        rightSubSplitCurve: OpenSpline,
    ): OpenSpline = OpenSpline.merge(
        splines = listOf(this, rightSubSplitCurve),
    )

    override val nodes: List<Node> = segments + terminator

    override val rightEdgeNode: Node
        get() = terminator
}
