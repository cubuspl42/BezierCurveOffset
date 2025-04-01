package app.geometry.bezier_splines

import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.ProperBezierCurve
import app.geometry.bezier_curves.SegmentCurve

class ClosedSpline<CurveT: SegmentCurve>(
    /**
     * The cyclic chain of links, must not be empty
     */
    override val segments: List<Segment<CurveT>>,
) : Spline<CurveT>() {
    abstract class ContourSplineApproximationResult(
        val contourSpline: ClosedSpline<*>,
    ) {
        companion object {
            fun wrap(
                subResults: List<ProperBezierCurve.OffsetSplineApproximationResult>,
            ): ContourSplineApproximationResult {
                require(subResults.isNotEmpty())

                val interconnectedContourSpline = ClosedSpline.interconnect(
                    splines = subResults.map { it.offsetSpline },
                )

                return object : ContourSplineApproximationResult(
                    contourSpline = interconnectedContourSpline,
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
            splines: List<OpenSpline<BezierCurve<*>>>,
        ): ClosedSpline<*> {
            require(splines.size >= 2)

            // TODO
            return ClosedSpline<SegmentCurve>(
                segments = emptyList(),
            )
        }
    }

    init {
        require(segments.isNotEmpty())
    }

    override val nodes: List<Node> = segments

    override val rightEdgeNode: Node
        get() = segments.first()

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
