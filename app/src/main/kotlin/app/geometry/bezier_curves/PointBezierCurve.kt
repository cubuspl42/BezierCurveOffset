package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_binomials.ConstantBezierBinomial
import app.geometry.Point
import app.geometry.bezier_curves.ProperBezierCurve.OffsetSplineApproximationResult

/**
 * A constant Bézier curve (a point)
 */
@Suppress("DataClassPrivateConstructor")
data class PointBezierCurve private constructor(
    val point: Point,
) : BezierCurve<PointBezierCurve>() {
    companion object {
        fun of(
            point: Point,
        ): PointBezierCurve = PointBezierCurve(
            point = point,
        )
    }

    override fun findOffsetSpline(
        strategy: ProperBezierCurve.OffsetStrategy,
        offset: Double,
    ): OffsetSplineApproximationResult? {
        // A point is definitely too tiny to construct its offset spline, as we
        // wouldn't know in which direction to offset
        return null
    }

    override val start: Point
        get() = point

    override val end: Point
        get() = point

    override val firstControl: Point
        get() = point

    override val lastControl: Point
        get() = point

    override val basisFormula = ConstantBezierBinomial(
        vectorSpace = Vector.VectorVectorSpace,
        weight0 = point.pv,
    )

    override val asProper: Nothing?
        get() = null

    override val asLongitudinal: Nothing?
        get() = null
}
