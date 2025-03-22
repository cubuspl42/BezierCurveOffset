package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_binomials.BezierBinomial
import app.geometry.Point
import app.geometry.Segment

/**
 * A linear Bézier curve (a line segment)
 */
@Suppress("DataClassPrivateConstructor")
data class LinearBezierCurve private constructor(
    override val start: Point,
    override val end: Point,
) : ProperBezierCurve<LinearBezierCurve>() {
    companion object {
        fun of(
            start: Point,
            end: Point,
        ): BezierCurve<*> = when {
            start == end -> ConstantBezierCurve.of(
                start = start,
                point = start,
                end = end,
            )

            else -> LinearBezierCurve(
                start = start,
                end = end,
            )
        }
    }

    init {
        require(start != end)
    }

    val segment: Segment
        get() = Segment(
            start = start,
            end = end,
        )

    override val firstControl: Point
        get() = start

    override val lastControl: Point
        get() = end

    override fun splitAt(
        t: Double,
    ): Pair<BezierCurve<*>, BezierCurve<*>> {
        val midPoint = segment.linearlyInterpolate(t = t)

        return Pair(
            LinearBezierCurve.of(
                start = start,
                end = midPoint,
            ),
            LinearBezierCurve.of(
                start = midPoint,
                end = end,
            ),
        )
    }

    override fun moveInNormalDirection(
        distance: Double,
    ): LinearBezierCurve {
        TODO("Not yet implemented")
    }

    override val basisFormula: BezierBinomial<Vector>
        get() = TODO("Not yet implemented")
}
