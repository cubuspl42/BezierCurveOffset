package app.geometry.splines

import app.geometry.Point
import app.geometry.bezier_curves.SegmentCurve
import app.mapFirst
import app.withPreviousOrNull

class OpenSpline<out CurveT: SegmentCurve<CurveT>>(
    /**
     * The path of links, must not be empty
     */
    override val segments: List<Segment<CurveT>>,
    /**
     * The plug node that terminates the path of links
     */
    val terminator: Terminator,
) : Spline<CurveT>() {
    companion object {
        fun <CurveT : SegmentCurve<CurveT>> merge(
            splines: List<OpenSpline<CurveT>>,
        ): OpenSpline<CurveT> {
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

    override val nodes: List<Node> = segments + terminator

    override val rightEdgeNode: Node
        get() = terminator
}

fun <CurveT : SegmentCurve<CurveT>> OpenSpline<CurveT>.mergeWith(
    rightSubSplitCurve: OpenSpline<CurveT>,
): OpenSpline<CurveT> = OpenSpline.merge(
    splines = listOf(this, rightSubSplitCurve),
)
