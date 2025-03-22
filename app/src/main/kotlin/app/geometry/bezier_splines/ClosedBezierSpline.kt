package app.geometry.bezier_splines

/**
 * A closed Bézier spline, i.e. such that forms a loops
 */
abstract class ClosedBezierSpline : BezierSpline<ClosedBezierSpline>() {
    companion object : Prototype<ClosedBezierSpline>() {
        override fun merge(
            splines: List<OpenBezierSpline>,
        ): ClosedBezierSpline {
            require(splines.isNotEmpty())

            val gluedExposedNodes = OpenBezierSpline.glueSplineExposedNodes(
                prevNode = splines.last().endNode,
                nextNode = splines.first().startNode,
            )

            val gluedInnerNodes = OpenBezierSpline.glueSplinesInnerNodes(
                splines = splines,
            )

            val mergedSpline = ClosedPolyBezierCurve(
                innerNodes = gluedInnerNodes + gluedExposedNodes,
            )

            return mergedSpline
        }
    }

    final override val prototype = ClosedBezierSpline

    final override val nodes: List<InnerNode>
        get() = innerNodes
}
