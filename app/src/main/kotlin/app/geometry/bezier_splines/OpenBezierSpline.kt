package app.geometry.bezier_splines

import app.fillCircle
import app.geometry.*
import app.geometry.bezier_curves.BezierCurve
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D

/**
 * An open Bézier spline, i.e. such that its start and end nodes are not
 * connected
 */
abstract class OpenBezierSpline : BezierSpline() {
    class EndNode(
        override val point: Point,
        override val backwardControl: Point,
    ) : BackwardNode {
        override val forwardControl: Nothing? = null
    }

    companion object {
        private fun glueSplines(
            prevSplineEndNode: OpenBezierSpline.EndNode,
            nextSplineStartNode: BezierSpline.StartNode,
        ): InnerNode {
            val startPoint = Point.midPoint(
                prevSplineEndNode.point,
                nextSplineStartNode.point,
            )

            val givenControl0 = prevSplineEndNode.backwardControl
            val givenControl1 = nextSplineStartNode.forwardControl

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

        fun merge(
            splines: List<OpenBezierSpline>,
        ): OpenBezierSpline {
            require(splines.isNotEmpty())

            if (splines.size == 1) return splines.single()

            val firstSpline = splines.first()
            val lastSpline = splines.last()

            val firstSplineStartNode = firstSpline.startNode

            val newInnerNodes = splines.zipWithNext().flatMap { (prevSpline, nextSpline) ->
                val jointNode = glueSplines(
                    prevSplineEndNode = prevSpline.endNode,
                    nextSplineStartNode = nextSpline.startNode,
                )

                listOf(jointNode) + nextSpline.innerNodes
            }

            val lastSplineEndNode = lastSpline.endNode

            val mergedSpline = OpenPolyBezierCurve(
                startNode = firstSplineStartNode,
                innerNodes = firstSpline.innerNodes + newInnerNodes,
                endNode = lastSplineEndNode,
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

fun OpenBezierSpline.mergeOf(
    transform: (BezierCurve) -> OpenBezierSpline,
): OpenBezierSpline = OpenBezierSpline.merge(
    splines = subCurves.map(transform),
)

fun OpenBezierSpline.toPath2D(): Path2D.Double = Path2D.Double().apply {
    moveTo(startNode.point)
    subCurves.forEach { subCurve ->
        cubicTo(
            control1 = subCurve.control0, control2 = subCurve.control1, end = subCurve.end
        )
    }
}

fun OpenBezierSpline.drawSpline(
    graphics2D: Graphics2D,
) {
    fun drawControlSegment(
        controlSegment: Segment,
    ) {
        graphics2D.draw(controlSegment.toLine2D())
        graphics2D.fillCircle(
            center = controlSegment.end,
            radius = 2.0,
        )
    }

    graphics2D.color = Color.LIGHT_GRAY

    nodes.forEach { node ->
        (node as? BezierSpline.BackwardNode)?.let {
            drawControlSegment(it.backwardControlSegment)
        }

        (node as? BezierSpline.ForwardNode)?.let {
            drawControlSegment(it.forwardControlSegment)
        }
    }

    graphics2D.color = Color.BLACK
    graphics2D.draw(toPath2D())

    nodes.forEach {
        graphics2D.fillCircle(
            center = it.point,
            radius = 2.0,
        )
    }
}
