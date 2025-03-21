package app.geometry

import app.fillCircle
import app.geometry.CompositeCubicBezierCurve.Node
import app.geometry.bezier_curves.CubicBezierCurve
import app.uncons
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D

interface CubicBezierSpline {
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
//                    val startPoint = requireEqual(prevSpline.endNode.point, spline.startNode.point)

                val (startNode, remainingNodes) = spline.nodes.uncons()!!

                listOf(
                    CompositeCubicBezierCurve.Node(
                        control0 = prevSpline.endNode.control0,
                        point = startNode.point,
                        control1 = startNode.control1,
                    )
                ) + remainingNodes.dropLast(1)
            }

            val nodesSuffix = listOf(lastNode)

            val joinedSpline = CompositeCubicBezierCurve(
                nodes = nodesPrefix + nodesInfix + nodesSuffix
            )

            return joinedSpline
        }
    }

    val nodes: List<CompositeCubicBezierCurve.Node>

    val subCurves: List<CubicBezierCurve>
}

val CubicBezierSpline.startNode: Node
    get() = nodes.first()

val CubicBezierSpline.endNode: Node
    get() = nodes.last()

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
