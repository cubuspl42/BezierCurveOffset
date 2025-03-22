package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_formulas.BezierFormula
import app.algebra.bezier_formulas.findFaster
import app.geometry.Direction
import app.geometry.Point
import app.geometry.Ray
import app.geometry.TimedPointSeries
import app.geometry.bezier_curves.ProperBezierCurve.OffsetStrategy
import app.geometry.bezier_splines.BezierSpline
import app.geometry.bezier_splines.MonoBezierCurve
import app.geometry.bezier_splines.OpenBezierSpline
import app.partitionSorted

sealed class BezierCurve<CurveT : BezierCurve<CurveT>> {
    companion object {
        fun interConnect(
            prevNode: BezierSpline.InnerNode,
            nextNode: BezierSpline.InnerNode,
        ): BezierCurve<*> = CubicBezierCurve.of(
            start = prevNode.point,
            control0 = prevNode.forwardControl,
            control1 = nextNode.backwardControl,
            end = nextNode.point,
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
            vectorFunction: TimeFunction<Direction>,
        ): TimeFunction<Ray> = TimeFunction.map2(
            functionA = pointFunction,
            functionB = vectorFunction,
        ) { point, direction ->
            Ray.inDirection(
                point = point,
                direction = direction,
            )
        }
    }

    val curveFunction: TimeFunction<Point> by lazy {
        basisFormula.findFaster().map { it.toPoint() }
    }

    fun findOffsetCurveFunction(
        offset: Double,
    ): TimeFunction<Point> = normalRayFunction.map { normalRay ->
        normalRay.startingPoint.moveInDirection(
            direction = normalRay.direction,
            distance = offset,
        )
    }

    val tangentFunction: TimeFunction<Direction> by lazy {
        TimeFunction.wrap(basisFormula.findDerivative()).map {
            // TODO: This might actually be zero
            Direction(d = it)
        }
    }

    val tangentRayFunction: TimeFunction<Ray> by lazy {
        bindRay(
            pointFunction = curveFunction,
            vectorFunction = tangentFunction,
        )
    }

    val normalFunction: TimeFunction<Direction> by lazy {
        tangentFunction.map { it.perpendicular }
    }

    val normalRayFunction by lazy {
        bindRay(
            pointFunction = curveFunction,
            vectorFunction = normalFunction,
        )
    }

    fun findOffsetTimedSeries(
        offset: Double,
    ): TimedPointSeries {
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

    abstract val basisFormula: BezierFormula<Vector>

    abstract val asProper: ProperBezierCurve<*>?

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