package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_binomials.BezierBinomial
import app.geometry.Point
import app.geometry.Segment

/**
 * A linear Bézier curve (a line segment)
 */
@Suppress("DataClassPrivateConstructor")
data class LineSegmentBezierCurve private constructor(
    val segment: Segment,
) : ProperBezierCurve<LineSegmentBezierCurve>() {
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

    override val start: Point
        get() = segment.start


    override val end: Point
        get() = TODO("Not yet implemented")


    init {
        require(start != end)
    }


    override val firstControl: Point
        get() = start

    override val lastControl: Point
        get() = end

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

    override fun moveInNormalDirection(
        distance: Double,
    ): LineSegmentBezierCurve {
        TODO("Not yet implemented")
    }

    override val basisFormula: BezierBinomial<Vector>
        get() = TODO("Not yet implemented")
}
