package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_binomials.DifferentiableBezierBinomial
import app.algebra.bezier_binomials.findFaster
import app.geometry.Direction
import app.geometry.Point
import app.geometry.Ray
import app.geometry.TimedPointSeries
import app.geometry.bezier_curves.ProperBezierCurve.OffsetSplineApproximationResult
import app.geometry.bezier_curves.ProperBezierCurve.OffsetStrategy
import app.geometry.bezier_splines.BezierSplineEdge
import app.geometry.bezier_splines.OpenSpline

sealed class BezierCurve<CurveT : BezierCurve<CurveT>> : SegmentCurve() {
    companion object {
        fun bindRay(
            pointFunction: TimeFunction<Point>,
            vectorFunction: TimeFunction<Direction?>,
        ): TimeFunction<Ray?> = TimeFunction.map2(
            functionA = pointFunction,
            functionB = vectorFunction,
        ) { point, direction ->
            direction?.let {
                Ray.inDirection(
                    point = point,
                    direction = it,
                )
            }
        }
    }

    val curveFunction: TimeFunction<Point> by lazy {
        basisFormula.findFaster().map { it.toPoint() }
    }

    fun findOffsetCurveFunction(
        offset: Double,
    ): TimeFunction<Point?> = normalRayFunction.map { normalRay ->
        normalRay?.startingPoint?.moveInDirection(
            direction = normalRay.direction,
            distance = offset,
        )
    }

    /**
     * The tangent direction function of the curve, based on the curve's
     * velocity. If this curve is degenerate, it might "slow down" at some point
     * to zero, so the tangent direction is non-existent (null). Theoretically,
     * for longitudinal (non-point) curves (even the otherwise degenerate ones),
     * the tangent should always be defined for t=0 and t=1, but even that is
     * difficult to guarantee from the numerical perspective.
     */
    val tangentFunction: TimeFunction<Direction?> by lazy {
        TimeFunction.wrap(basisFormula.findDerivative()).map {
            Direction.of(it)
        }
    }

    val tangentRayFunction: TimeFunction<Ray?> by lazy {
        bindRay(
            pointFunction = curveFunction,
            vectorFunction = tangentFunction,
        )
    }

    /**
     * The normal direction of the curve, i.e. the direction perpendicular to
     * the tangent direction.
     */
    val normalFunction: TimeFunction<Direction?> by lazy {
        tangentFunction.map {
            it?.perpendicular
        }
    }

    val normalRayFunction: TimeFunction<Ray?> by lazy {
        bindRay(
            pointFunction = curveFunction,
            vectorFunction = normalFunction,
        )
    }

    fun findOffsetTimedSeries(
        offset: Double,
    ): TimedPointSeries? {
        val offsetCurveFunction = findOffsetCurveFunction(offset = offset)

        return TimedPointSeries.sample(
            curveFunction = offsetCurveFunction,
            sampleCount = 6,
        )
    }

    /**
     * Find the best offset spline of this curve.
     *
     * @return The best found offset spline, or null if this curve is too tiny
     * to construct its offset spline
     */
    abstract fun findOffsetSpline(
        strategy: OffsetStrategy,
        offset: Double,
    ): OffsetSplineApproximationResult?

    abstract val firstControl: Point

    abstract val lastControl: Point

    abstract val basisFormula: DifferentiableBezierBinomial<Vector>

    abstract val asProper: ProperBezierCurve<*>?

    abstract val asLongitudinal: LongitudinalBezierCurve<*>?

    fun toSpline(): OpenSpline = OpenSpline.ofEdge(
        startKnot = start,
        edge = BezierSplineEdge(
            startControl = firstControl,
            endControl = lastControl,
        ),

        endKnot = end,
    )
}
