package app.geometry.bezier_splines

import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.ProperBezierCurve
import app.geometry.bezier_splines.OpenSpline.Companion.fastenLinksSmoothly

class ClosedSpline(
    /**
     * The cyclic chain of links, must not be empty
     */
    override val innerLinks: List<InnerLink>,
) : Spline() {
    abstract class ContourSplineApproximationResult(
        val contourSpline: ClosedSpline,
    ) {
        companion object {
            fun wrap(
                subResults: List<ProperBezierCurve.OffsetSplineApproximationResult>,
            ): ContourSplineApproximationResult {
                require(subResults.isNotEmpty())

                val mergedContourSpline = ClosedSpline.interconnect(
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

    companion object {
        fun interconnect(
            splines: List<OpenSpline>,
        ): ClosedSpline {
            require(splines.isNotEmpty())

            val firstSpline = splines.first()
            val lastSpline = splines.last()

            val (lastLink, firstLink) = fastenLinksSmoothly(
                prevSpline = lastSpline,
                nextSpline = firstSpline,
            )

            val insideLinks = OpenSpline.fastenSplines(
                splines = splines
            )

            return ClosedSpline(
                innerLinks = listOf(firstLink) + insideLinks + lastLink,
            )
        }
    }

    init {
        require(innerLinks.isNotEmpty())
    }

    override val nodes: List<Link> = innerLinks

    override val rightEdgeNode: Link
        get() = innerLinks.first()

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
            val subBezierCurve = subCurve as BezierCurve<*>

            subBezierCurve.findOffsetSpline(
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
