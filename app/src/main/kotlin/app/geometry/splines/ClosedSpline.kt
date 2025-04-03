package app.geometry.splines

import app.dump
import app.geometry.Transformation
import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.SegmentCurve
import app.withNextCyclic

class ClosedSpline<out CurveT : SegmentCurve<CurveT>>(
    /**
     * The cyclic chain of links, must not be empty
     */
    override val segments: List<Segment<CurveT>>,
) : Spline<CurveT>() {
    abstract class ContourSplineApproximationResult {
        companion object {
            fun interconnect(
                offsetResults: List<SegmentCurve.OffsetSplineApproximationResult<*>>,
            ): ContourSplineApproximationResult {
                require(offsetResults.isNotEmpty())

                return object : ContourSplineApproximationResult() {
                    override val offsetResults = offsetResults

                    override val contourSpline: ClosedSpline<*> by lazy {
                        ClosedSpline.interconnect(
                            splines = offsetResults.map { it.offsetSpline },
                        )
                    }

                    override val globalOffsetDeviation: Double by lazy {
                        offsetResults.maxOf { it.globalDeviation }
                    }
                }
            }
        }

        abstract val offsetResults: List<SegmentCurve.OffsetSplineApproximationResult<*>>

        abstract val contourSpline: ClosedSpline<*>

        /**
         * @return The calculated global deviation
         */
        abstract val globalOffsetDeviation: Double
    }

    companion object {
        fun interconnect(
            splines: List<OpenSpline<*>>,
        ): ClosedSpline<*> {
            require(splines.isNotEmpty())

            val segments = splines.withNextCyclic().flatMap { (spline, nextSpline) ->
                spline.segments + Segment.subline(
                    startKnot = spline.terminator.endKnot,
                ) + listOfNotNull(
                    spline.backRay!!.intersect(nextSpline.frontRay!!)?.let { intersectionPoint ->
                        Segment.subline(
                            startKnot = intersectionPoint,
                        )
                    },
                )
            }

            return ClosedSpline(
                segments = segments,
            )
        }
    }

    init {
        require(segments.isNotEmpty())
    }

    override val nodes: List<Node> = segments

    override val rightEdgeNode: Node
        get() = segments.first()

    fun transformVia(
        transformation: Transformation,
    ) = ClosedSpline(
        segments = segments.map { it.transformVia(transformation) })

    /**
     * Find the contour of this spline.
     *
     * @return The best found contour spline, or null if this spline is too tiny
     * to construct its contour
     */
    fun findContourSpline(
        strategy: BezierCurve.OffsetStrategy,
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

        return ContourSplineApproximationResult.interconnect(
            offsetResults = subResults,
        )
    }

    fun dump() = """
        ClosedSpline(
            segments = ${segments.dump()},
        )
    """.trimIndent()
}
