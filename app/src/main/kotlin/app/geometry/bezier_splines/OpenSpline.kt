package app.geometry.bezier_splines

import app.geometry.Point
import app.interleave
import app.mapFirst
import app.withPreviousOrNull

class OpenSpline(
    /**
     * The path of links, must not be empty
     */
    override val segments: List<Segment<*>>,
    /**
     * The plug node that terminates the path of links
     */
    val terminator: Terminator,
) : Spline() {
    companion object {
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

    fun mergeWith(
        rightSubSplitCurve: OpenSpline,
    ): OpenSpline = OpenSpline.merge(
        splines = listOf(this, rightSubSplitCurve),
    )

    override val nodes: List<Node> = segments + terminator

    override val rightEdgeNode: Node
        get() = terminator
}
