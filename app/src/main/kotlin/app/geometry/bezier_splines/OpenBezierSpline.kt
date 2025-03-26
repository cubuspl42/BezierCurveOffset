package app.geometry.bezier_splines

import app.geometry.*

/**
 * An open Bézier spline, i.e. such that its start and end nodes are not
 * connected
 */
abstract class OpenBezierSpline : BezierSpline<OpenBezierSpline>() {
    companion object : Prototype<OpenBezierSpline>() {

        override fun merge(
            splines: List<OpenBezierSpline>,
        ): OpenBezierSpline {
            require(splines.isNotEmpty())

            if (splines.size == 1) return splines.single()

            val innerNodes = interconnectSplines(splines = splines)

            val mergedSpline = OpenPolyBezierCurve(
                startNode = splines.first().startNode,
                innerNodes = innerNodes,
                endNode = splines.last().endNode,
            )

            return mergedSpline
        }
    }

    val secondNode: BackwardNode
        get() = innerNodes.firstOrNull() ?: endNode

    val oneBeforeEndNode: ForwardNode
        get() = innerNodes.lastOrNull() ?: startNode

    abstract val startNode: StartNode

    abstract val endNode: EndNode

    final override val prototype = OpenBezierSpline

    final override val nodes: List<Node> by lazy {
        listOf(startNode) + innerNodes + endNode
    }
}

val BezierSpline.BackwardNode.backwardControlSegment: Segment
    get() = Segment(
        start = knotPoint,
        end = backwardControl,
    )

val BezierSpline.ForwardNode.forwardControlSegment: Segment
    get() = Segment(
        start = knotPoint,
        end = forwardControl,
    )

fun OpenBezierSpline.mergeWith(
    rightSubSplitCurve: OpenBezierSpline,
): OpenBezierSpline = OpenBezierSpline.merge(
    splines = listOf(this, rightSubSplitCurve),
)
