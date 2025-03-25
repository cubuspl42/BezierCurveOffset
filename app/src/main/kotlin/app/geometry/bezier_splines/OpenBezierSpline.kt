package app.geometry.bezier_splines

import app.geometry.*
import app.geometry.bezier_curves.ProperBezierCurve

/**
 * An open Bézier spline, i.e. such that its start and end nodes are not
 * connected
 */
abstract class OpenBezierSpline : BezierSpline<OpenBezierSpline>() {
    companion object : Prototype<OpenBezierSpline>() {
        fun glueSplineExposedNodes(
            prevNode: BackwardNode,
            nextNode: ForwardNode,
        ): InnerNode {
            val startPoint = Point.midPoint(
                prevNode.knotPoint,
                nextNode.knotPoint,
            )

            val (fixedControl0, fixedControl1) = Point.makeCollinear(
                prevNode.backwardControl,
                nextNode.forwardControl,
                base = startPoint,

            )

            return InnerNode(
                backwardControl = fixedControl0,
                knotPoint = startPoint,
                forwardControl = fixedControl1,
            )
        }

        fun glueSplinesInnerNodes(
            splines: List<OpenBezierSpline>,
        ): List<BezierSpline.InnerNode> {
            val firstSpline = splines.first()

            return firstSpline.innerNodes + splines.zipWithNext().flatMap { (prevSpline, nextSpline) ->
                val jointNode = OpenBezierSpline.glueSplineExposedNodes(
                    prevNode = prevSpline.endNode,
                    nextNode = nextSpline.startNode,
                )

                listOf(jointNode) + nextSpline.innerNodes
            }
        }

        override fun merge(
            splines: List<OpenBezierSpline>,
        ): OpenBezierSpline {
            require(splines.isNotEmpty())

            if (splines.size == 1) return splines.single()

            val gluedInnerNodes = glueSplinesInnerNodes(splines = splines)

            val mergedSpline = OpenPolyBezierCurve(
                startNode = splines.first().startNode,
                innerNodes = gluedInnerNodes,
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
