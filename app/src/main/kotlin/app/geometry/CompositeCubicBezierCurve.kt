package app.geometry

import app.geometry.bezier_curves.CubicBezierCurve

/**
 * A Composite Bézier curve, also called a poly-Bézier curve
 */
class CompositeCubicBezierCurve(
    /**
     * The nodes of this composite curve. The first control point of the first node and the last control point of the
     * last node are not effective.
     */
    override val nodes: List<Node>,
) : CubicBezierSpline {
    class Node(
        val control0: Point,
        val point: Point,
        val control1: Point,
    ) {
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

    init {
        require(nodes.size >= 2)
    }

    override val subCurves: List<CubicBezierCurve> by lazy {
        nodes.zipWithNext { node, nextNode ->
            CubicBezierCurve(
                start = node.point,
                control0 = node.control1,
                control1 = nextNode.control0,
                end = nextNode.point,
            )
        }
    }
}
