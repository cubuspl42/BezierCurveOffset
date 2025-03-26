package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_binomials.BezierBinomial
import app.geometry.*
import app.geometry.bezier_curves.ProperBezierCurve.OffsetSplineApproximationResult
import app.geometry.bezier_splines.OpenBezierSpline
import java.awt.geom.Path2D

/**
 * A linear Bézier curve (a line segment)
 */
@Suppress("DataClassPrivateConstructor")
data class LineSegmentBezierCurve private constructor(
    val segment: Segment,
) : LongitudinalBezierCurve<LineSegmentBezierCurve>() {
    companion object {
        /**
         * @return A non-degenerate cubic linear curve with the given points, or
         * a constant Bézier curve (a point) if they are the same point
         */
        fun of(
            start: Point,
            end: Point,
        ): BezierCurve<*> = when {
            start == end -> PointBezierCurve.of(
                point = start,
            )

            else -> LineSegmentBezierCurve(
                segment = Segment(
                    start = start,
                    end = end,
                ),
            )
        }
    }

    init {
        require(start != end)
    }

    override val start: Point
        get() = segment.start

    override val end: Point
        get() = segment.end

    override val firstControl: Point
        get() = start

    override val lastControl: Point
        get() = end

    override fun findOffsetSpline(
        strategy: ProperBezierCurve.OffsetStrategy,
        offset: Double,
    ): OffsetSplineApproximationResult = findOffsetSpline(
        offset = offset,
    )

    override fun findOffsetSplineRecursive(
        strategy: ProperBezierCurve.OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): OffsetSplineApproximationResult = findOffsetSpline(
        offset = offset,
    )

    private fun findOffsetSpline(
        offset: Double,
    ): OffsetSplineApproximationResult = OffsetSplineApproximationResult.precise(
        findOffsetLineSegmentCurve(
            offset = offset,
        ),
    )


    override fun splitAt(
        t: Double,
    ): Pair<BezierCurve<*>, BezierCurve<*>> {
        val midPoint = segment.linearlyInterpolate(t = t)

        return Pair(
            LineSegmentBezierCurve.of(
                start = start,
                end = midPoint,
            ),
            LineSegmentBezierCurve.of(
                start = midPoint,
                end = end,
            ),
        )
    }

    fun findOffsetLineSegmentCurve(
        offset: Double,
    ): LineSegmentBezierCurve {
        val offsetSegment = segment.moveInDirection(
            direction = segment.direction!!.perpendicular,
            distance = offset,
        )

        return LineSegmentBezierCurve(
            segment = offsetSegment,
        )
    }

    override fun toPath2D(): Path2D.Double = Path2D.Double().apply {
        moveTo(start)
        lineTo(end)
    }

    override val basisFormula: BezierBinomial<Vector>
        get() = TODO("Not yet implemented")

    override val asProper: Nothing?
        get() = null
}
