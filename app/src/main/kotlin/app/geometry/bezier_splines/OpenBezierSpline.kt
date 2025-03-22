package app.geometry.bezier_splines

import app.fillCircle
import app.geometry.*
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D

/**
 * An open Bézier spline, i.e. such that its start and end nodes are not
 * connected
 */
abstract class OpenBezierSpline : BezierSpline<OpenBezierSpline>() {
    class EndNode(
        override val point: Point,
        override val backwardControl: Point,
    ) : BackwardNode {
        override val forwardControl: Nothing? = null
    }

    companion object : Prototype<OpenBezierSpline>() {
        fun glueSplineExposedNodes(
            prevNode: BackwardNode,
            nextNode: ForwardNode,
        ): InnerNode {
            val startPoint = Point.midPoint(
                prevNode.point,
                nextNode.point,
            )

            val givenControl0 = prevNode.backwardControl
            val givenControl1 = nextNode.forwardControl

            val givenControlsBiRay = BiRay.fromPoints(
                basePoint = startPoint,
                directionPoint1 = givenControl0,
                directionPoint2 = givenControl1,
            )

            val projectionLine = givenControlsBiRay.tangentLine

            return when {
                // Control segments are not parallel, we can fix that
                projectionLine != null -> {
                    val projectedControl0 = givenControl0.projectOnto(projectionLine)
                    val projectedControl1 = givenControl1.projectOnto(projectionLine)

                    InnerNode(
                        backwardControl = projectedControl0,
                        point = startPoint,
                        forwardControl = projectedControl1,
                    )
                }

                // Control segments are already parallel, let's use them as they are
                else -> InnerNode(
                    backwardControl = givenControl0,
                    point = startPoint,
                    forwardControl = givenControl1,
                )
            }
        }

        fun glueSplinesInnerNodes(
            splines: List<OpenBezierSpline>
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
        start = point,
        end = backwardControl,
    )

val BezierSpline.ForwardNode.forwardControlSegment: Segment
    get() = Segment(
        start = point,
        end = forwardControl,
    )

fun OpenBezierSpline.mergeWith(
    rightSubSplitCurve: OpenBezierSpline,
): OpenBezierSpline = OpenBezierSpline.merge(
    splines = listOf(this, rightSubSplitCurve),
)
