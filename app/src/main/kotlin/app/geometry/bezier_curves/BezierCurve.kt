package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_binomials.BezierBinomial
import app.algebra.bezier_binomials.findFaster
import app.geometry.Direction
import app.geometry.Point
import app.geometry.Ray
import app.geometry.TimedPointSeries
import app.geometry.bezier_curves.ProperBezierCurve.OffsetStrategy
import app.geometry.bezier_splines.BezierSpline
import app.geometry.bezier_splines.MonoBezierCurve
import app.geometry.bezier_splines.OpenBezierSpline

sealed class BezierCurve<CurveT : BezierCurve<CurveT>> {
    companion object {
        fun interConnect(
            prevNode: BezierSpline.InnerNode,
            nextNode: BezierSpline.InnerNode,
        ): BezierCurve<*> = CubicBezierCurve.of(
            start = prevNode.knotPoint,
            control0 = prevNode.forwardControl,
            control1 = nextNode.backwardControl,
            end = nextNode.knotPoint,
        )

        fun interConnectAll(
            innerNodes: List<BezierSpline.InnerNode>,
        ): List<BezierCurve<*>> = innerNodes.zipWithNext { prevNode, nextNode ->
            interConnect(
                prevNode = prevNode,
                nextNode = nextNode,
            )
        }

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
     * velocity. In a corner case, the curve might "slow down" at some points to
     * zero, so the tangent direction is non-existent (null).
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

    /**
     * Find the offset curved timed point series for this curve, assuming it's
     * velocity never comes down to 0
     */
    fun findOffsetTimedSeries(
        offset: Double,
    ): TimedPointSeries? {
        val offsetCurveFunction = findOffsetCurveFunction(offset = offset)

        return TimedPointSeries.sample(
            curveFunction = offsetCurveFunction,
            sampleCount = 6,
        )
    }

    abstract fun findOffsetSpline(
        strategy: OffsetStrategy,
        offset: Double,
    ): OpenBezierSpline?

    abstract val start: Point

    abstract val end: Point

    abstract val firstControl: Point

    abstract val lastControl: Point

    abstract val basisFormula: BezierBinomial<Vector>

    abstract val asProper: ProperBezierCurve<*>?

    abstract val asLongitudinal: LongitudinalBezierCurve<*>?

    abstract fun splitAt(
        t: Double,
    ): Pair<BezierCurve<*>, BezierCurve<*>>

    abstract fun splitAtMultiple(
        tValues: Set<Double>,
    ): OpenBezierSpline?

    fun toSpline(): OpenBezierSpline = MonoBezierCurve(
        curve = this,
    )
}
