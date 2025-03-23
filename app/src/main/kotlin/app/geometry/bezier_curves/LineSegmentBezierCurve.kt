package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_binomials.BezierBinomial
import app.geometry.Point
import app.geometry.Segment
import app.geometry.bezier_splines.OpenBezierSpline

/**
 * A linear Bézier curve (a line segment)
 */
@Suppress("DataClassPrivateConstructor")
data class LineSegmentBezierCurve private constructor(
    val segment: Segment,
) : LongitudinalBezierCurve<LineSegmentBezierCurve>() {
    companion object {
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
    ): OpenBezierSpline {
        val offsetSegment = segment.moveInDirection(
            direction = segment.direction!!.perpendicular,
            distance = offset,
        )

        return LineSegmentBezierCurve(
            segment = offsetSegment,
        ).toSpline()
    }

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

    override val basisFormula: BezierBinomial<Vector>
        get() = TODO("Not yet implemented")

    override val asProper: Nothing?
        get() = null
}
