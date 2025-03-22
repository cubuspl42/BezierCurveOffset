package app.geometry.bezier_splines

import app.fillCircle
import app.geometry.*
import app.geometry.bezier_curves.CubicBezierCurve
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D

abstract class OpenCubicBezierSpline {
    sealed interface Node {
        val backwardControl: Point?
        val point: Point
        val forwardControl: Point?
    }

    sealed interface ForwardNode : Node {
        override val forwardControl: Point
    }

    sealed interface BackwardNode : Node {
        override val backwardControl: Point
    }

    class StartNode(
        override val point: Point,
        override val forwardControl: Point,
    ) : ForwardNode {
        override val backwardControl: Nothing? = null
    }

    class InnerNode(
        override val backwardControl: Point,
        override val point: Point,
        override val forwardControl: Point,
    ) : ForwardNode, BackwardNode {
        companion object {
            fun start(
                point: Point,
                control1: Point,
            ): StartNode = StartNode(
                point = point,
                forwardControl = control1,
            )

            fun end(
                control0: Point,
                point: Point,
            ): EndNode = EndNode(
                backwardControl = control0,
                point = point,
            )
        }
    }

    class EndNode(
        override val point: Point,
        override val backwardControl: Point,
    ) : BackwardNode {
        override val forwardControl: Nothing? = null
    }

    companion object {
        private fun glueSplines(
            prevSplineEndNode: OpenCubicBezierSpline.EndNode,
            nextSplineStartNode: OpenCubicBezierSpline.StartNode,
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
            splines: List<OpenCubicBezierSpline>,
        ): OpenCubicBezierSpline {
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

            val mergedSpline = PolyCubicBezierCurve(
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

    /**
     * The nodes of this spline. The first control point of the first node and
     * the last control point of the last node are not effective.
     */
    abstract val innerNodes: List<InnerNode>

    abstract val endNode: EndNode

    val forwardNodes: List<ForwardNode> by lazy {
        listOf(startNode) + innerNodes
    }

    val backwardNodes: List<BackwardNode> by lazy {
        innerNodes + endNode
    }

    val nodes by lazy {
        listOf(startNode) + innerNodes + endNode
    }

    abstract val subCurves: List<CubicBezierCurve>
}

val OpenCubicBezierSpline.BackwardNode.backwardControlSegment: Segment
    get() = Segment(
        start = point,
        end = backwardControl,
    )

val OpenCubicBezierSpline.ForwardNode.forwardControlSegment: Segment
    get() = Segment(
        start = point,
        end = forwardControl,
    )

fun OpenCubicBezierSpline.mergeWith(
    rightSubSplitCurve: OpenCubicBezierSpline,
): OpenCubicBezierSpline = OpenCubicBezierSpline.merge(
    splines = listOf(this, rightSubSplitCurve),
)

fun OpenCubicBezierSpline.mergeOf(
    transform: (CubicBezierCurve) -> OpenCubicBezierSpline,
): OpenCubicBezierSpline = OpenCubicBezierSpline.merge(
    splines = subCurves.map(transform),
)

fun OpenCubicBezierSpline.toPath2D(): Path2D.Double = Path2D.Double().apply {
    moveTo(startNode.point)
    subCurves.forEach { subCurve ->
        cubicTo(
            control1 = subCurve.control0, control2 = subCurve.control1, end = subCurve.end
        )
    }
}

fun OpenCubicBezierSpline.drawSpline(
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
        (node as? OpenCubicBezierSpline.BackwardNode)?.let {
            drawControlSegment(it.backwardControlSegment)
        }

        (node as? OpenCubicBezierSpline.ForwardNode)?.let {
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
