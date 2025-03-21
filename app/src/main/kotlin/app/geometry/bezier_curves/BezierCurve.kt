package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_formulas.BezierFormula
import app.algebra.bezier_formulas.RealFunction.SamplingStrategy
import app.algebra.bezier_formulas.findCriticalPoints
import app.algebra.bezier_formulas.toPath2D
import app.geometry.*
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D
import kotlin.math.roundToInt

abstract class BezierCurve {
    companion object {
        private val dashedStroke: BasicStroke = BasicStroke(
            1.5f,
            BasicStroke.CAP_ROUND,
            BasicStroke.JOIN_MITER,
            1.5f,
            floatArrayOf(5.0f, 5.0f),
            0f,
        )

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
        TimeFunction.wrap(basisFormula).map { it.toPoint() }
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

    fun findBoundingBox(): BoundingBox {
        val startPoint = curveFunction.startValue
        val endPoint = curveFunction.endValue

        val inRangeCriticalPointSet = basisFormula.findCriticalPoints().inRange()

        val criticalXValues = inRangeCriticalPointSet.criticalPointsX.map { t -> curveFunction.evaluate(t).x }
        val potentialXExtrema = criticalXValues + startPoint.x + endPoint.x
        val xMin = potentialXExtrema.min()
        val xMax = potentialXExtrema.max()

        val criticalYValues = inRangeCriticalPointSet.criticalPointsY.map { t -> curveFunction.evaluate(t).y }
        val potentialYExtrema = criticalYValues + startPoint.y + endPoint.y
        val yMin = potentialYExtrema.min()
        val yMax = potentialYExtrema.max()

        return BoundingBox.fromExtrema(
            xMin = xMin,
            xMax = xMax,
            yMin = yMin,
            yMax = yMax,
        )
    }

    fun findOffsetPolyline(
        offset: Double,
    ): TimedPolyline {
        val offsetCurveFunction = findOffsetCurveFunction(offset = offset)

        return TimedPolyline.sample(
            curveFunction = offsetCurveFunction,
            sampleCount = 12,
        )
    }

    fun findOffsetCurveNormal(
        offset: Double,
    ): BezierCurve {
        val startNormalRay = normalRayFunction.startValue
        val startNormalLine = startNormalRay.containingLine

        val endNormalRay = normalRayFunction.endValue
        val endNormalLine = endNormalRay.containingLine

        val normalIntersectionPoint = startNormalLine.intersect(endNormalLine) ?: return moveInDirectionPointWise(
            // If there's no intersection point, the start and end vectors are parallel. We could choose either.
            direction = startNormalRay.direction,
            distance = offset,
        )

        return moveAwayPointWise(
            origin = normalIntersectionPoint,
            distance = offset,
        )
    }

    fun findOffsetCurveBestFit(
        offset: Double,
    ): CubicBezierCurve {
        val offsetPolyline = findOffsetPolyline(offset = offset)
        return offsetPolyline.bestFitCurve()
    }

    fun draw(
        graphics2D: Graphics2D,
        innerColor: Color = Color.BLACK,
        outerColor: Color = Color.LIGHT_GRAY,
        outerSamplingStrategy: SamplingStrategy,
    ) {
        val outerPath = basisFormula.toPath2D(
            samplingStrategy = outerSamplingStrategy,
        )

        graphics2D.stroke = BasicStroke(1.0f)
        graphics2D.color = outerColor
        graphics2D.draw(outerPath)

        val innerPath = toPath2D()

        graphics2D.stroke = BasicStroke(2.0f)
        graphics2D.color = innerColor
        graphics2D.draw(innerPath)

        graphics2D.color = Color.PINK

        graphics2D.fillCircle(
            center = start,
            radius = 6.0,
        )
        graphics2D.fillCircle(
            center = end,
            radius = 6.0,
        )

        val criticalPointSet = basisFormula.findCriticalPoints()

        fun drawCriticalPoints(
            criticalPoints: Set<Double>,
            color: Color,
        ) {
            criticalPoints.forEach { extremityT ->
                val extremityPoint = basisFormula.evaluate(t = extremityT).toPoint()

                graphics2D.color = color
                graphics2D.fillCircle(
                    center = extremityPoint,
                    radius = 4.0,
                )
            }
        }

        drawCriticalPoints(
            criticalPoints = criticalPointSet.criticalPointsX,
            color = Color.RED,
        )

        drawCriticalPoints(
            criticalPoints = criticalPointSet.criticalPointsY,
            color = Color.GREEN,
        )
    }

    abstract val start: Point
    abstract val end: Point

    abstract val basisFormula: BezierFormula<Vector>

    abstract fun moveAwayPointWise(
        origin: Point,
        distance: Double,
    ): BezierCurve

    abstract fun moveInDirectionPointWise(
        direction: Direction,
        distance: Double,
    ): BezierCurve

    abstract fun toPath2D(): Path2D
}

fun Graphics2D.drawCircle(
    center: Point,
    radius: Double,
) {
    val diameter = (2 * radius).roundToInt()
    val x = center.x - radius
    val y = center.y - radius
    drawOval(x.roundToInt(), y.roundToInt(), diameter, diameter)
}

fun Graphics2D.fillCircle(
    center: Point,
    radius: Double,
) {
    val diameter = (2 * radius).roundToInt()
    val x = center.x - radius
    val y = center.y - radius
    fillOval(x.roundToInt(), y.roundToInt(), diameter, diameter)
}
