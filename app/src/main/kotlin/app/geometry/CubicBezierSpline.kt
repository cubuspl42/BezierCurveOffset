package app.geometry

import app.fillCircle
import app.geometry.CubicBezierSpline.Node
import app.geometry.bezier_curves.CubicBezierCurve
import app.uncons
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D

interface CubicBezierSpline {
    class Node(
        val control0: Point,
        val point: Point,
        val control1: Point,
    ) {
        companion object {
            fun start(
                point: Point,
                control1: Point,
            ): Node = Node(
                control0 = point,
                point = point,
                control1 = control1,
            )

            fun end(
                control0: Point,
                point: Point,
            ): Node = Node(
                control0 = control0,
                point = point,
                control1 = point,
            )
        }

        val firstControlSegment: Segment
            get() = Segment(
                start = point,
                end = control0,
            )

        val secondControlSegment: Segment
            get() = Segment(
                start = point,
                end = control1,
            )
    }

    companion object {
        fun join(
            splines: List<CubicBezierSpline>,
        ): CubicBezierSpline {
            require(splines.isNotEmpty())

            if (splines.size == 1) return splines.single()

            val firstSpline = splines.first()
            val lastSpline = splines.last()

            val lastNode = lastSpline.nodes.last()

            val nodesPrefix = firstSpline.nodes.dropLast(1)

            val nodesInfix = splines.zipWithNext().flatMap { (prevSpline, spline) ->
                val startPoint = Point.midPoint(
                    prevSpline.endNode.point,
                    spline.startNode.point,
                )

                val (startNode, remainingNodes) = spline.nodes.uncons()!!

                val givenControl0 = prevSpline.endNode.control0
                val givenControl1 = startNode.control1

                val givenControlsBiRay = BiRay.fromPoints(
                    basePoint = startPoint,
                    directionPoint1 = givenControl0,
                    directionPoint2 = givenControl1,
                )

                val projectionLine = givenControlsBiRay.tangentLine

                val joinedStartNode = when {
                    // Control segments are not parallel, we can fix that
                    projectionLine != null -> {
                        val projectedControl0 = givenControl0.projectOnto(projectionLine)
                        val projectedControl1 = givenControl1.projectOnto(projectionLine)

                        Node(
                            control0 = projectedControl0,
                            point = startPoint,
                            control1 = projectedControl1,
                        )
                    }

                    // Control segments are already parallel, let's use them as they are
                    else -> Node(
                        control0 = givenControl0,
                        point = startPoint,
                        control1 = givenControl1,
                    )
                }

                listOf(joinedStartNode) + remainingNodes.dropLast(1)
            }

            val nodesSuffix = listOf(lastNode)

            val joinedSpline = PolyCubicBezierCurve(
                nodes = nodesPrefix + nodesInfix + nodesSuffix,
            )

            return joinedSpline
        }
    }

    val nodes: List<Node>

    val subCurves: List<CubicBezierCurve>
}

val CubicBezierSpline.startNode: Node
    get() = nodes.first()

val CubicBezierSpline.endNode: Node
    get() = nodes.last()

fun CubicBezierSpline.joinWith(
    rightSubSplitCurve: CubicBezierSpline,
): CubicBezierSpline = CubicBezierSpline.join(
    splines = listOf(this, rightSubSplitCurve),
)

fun CubicBezierSpline.joinOf(
    transform: (CubicBezierCurve) -> CubicBezierSpline,
): CubicBezierSpline = CubicBezierSpline.join(
    splines = subCurves.map(transform),
)

fun CubicBezierSpline.toPath2D(): Path2D.Double = Path2D.Double().apply {
    moveTo(startNode.point)
    subCurves.forEach { subCurve ->
        cubicTo(
            control1 = subCurve.control0, control2 = subCurve.control1, end = subCurve.end
        )
    }
}

fun CubicBezierSpline.drawSpline(
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

    nodes.forEach {
        drawControlSegment(it.firstControlSegment)
        drawControlSegment(it.secondControlSegment)
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
