package app.geometry.bezier_splines

import app.geometry.bezier_curves.ProperBezierCurve

/**
 * A closed Bézier spline, i.e. such that forms a loop
 */
abstract class ClosedBezierSpline : BezierSpline<ClosedBezierSpline>() {
    abstract class ContourSplineApproximationResult(
        val contourSpline: ClosedBezierSpline,
    ) {
        companion object {
            fun wrap(
                subResults: List<ProperBezierCurve.OffsetSplineApproximationResult>,
            ): ContourSplineApproximationResult {
                require(subResults.isNotEmpty())

                val mergedContourSpline = ClosedBezierSpline.merge(
                    splines = subResults.map { it.offsetSpline },
                )

                return object : ContourSplineApproximationResult(
                    contourSpline = mergedContourSpline,
                ) {
                    override val globalDeviation: Double by lazy {
                        subResults.maxOf { it.globalDeviation }
                    }
                }
            }
        }

        /**
         * @return The calculated global deviation
         */
        abstract val globalDeviation: Double
    }

    companion object : Prototype<ClosedBezierSpline>() {
        override fun merge(
            splines: List<OpenBezierSpline>,
        ): ClosedBezierSpline {
            require(splines.isNotEmpty())

            val linkNode = fuseEdgeNodes(
                prevNode = splines.last().endNode,
                nextNode = splines.first().startNode,
            )

            val innerNodes = interconnectSplines(
                splines = splines,
            )

            val mergedSpline = ClosedPolyBezierCurve(
                innerNodes = innerNodes + linkNode,
            )

            return mergedSpline
        }
    }

    final override val prototype = ClosedBezierSpline

    final override val nodes: List<InnerNode>
        get() = innerNodes

    /**
     * Find the contour of this spline.
     *
     * @return The best found contour spline, or null if this spline is too tiny
     * to construct its contour
     */
    fun findContourSpline(
        strategy: ProperBezierCurve.OffsetStrategy,
        offset: Double,
    ): ContourSplineApproximationResult? {
        val subResults = subCurves.mapNotNull { subCurve ->
            subCurve.findOffsetSpline(
                strategy = strategy,
                offset = offset,
            )
        }

        if (subResults.isEmpty()) {
            // TODO: Return a circle?
            return null
        }

        return ContourSplineApproximationResult.wrap(
            subResults = subResults,
        )
    }
}
