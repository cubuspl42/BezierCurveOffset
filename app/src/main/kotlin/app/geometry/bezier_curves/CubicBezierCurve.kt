package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_formulas.*
import app.algebra.bezier_formulas.RealFunction.SamplingStrategy
import app.fillCircle
import app.geometry.*
import app.geometry.bezier_splines.OpenBezierSpline
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D

/**
 * A cubic Bézier curve
 */
@Suppress("DataClassPrivateConstructor")
data class CubicBezierCurve private constructor(
    override val start: Point,
    val control0: Point,
    val control1: Point,
    override val end: Point,
) : ProperBezierCurve<CubicBezierCurve>() {
    companion object {
        fun of(
            start: Point,
            control0: Point,
            control1: Point,
            end: Point,
        ): BezierCurve<*> = when {
            start == control0 -> QuadraticBezierCurve.of(
                start = start,
                control = control1,
                end = end,
            )

            control0 == control1 -> QuadraticBezierCurve.of(
                start = start,
                control = control0,
                end = end,
            )

            control1 == end -> QuadraticBezierCurve.of(
                start = start,
                control = control0,
                end = end,
            )

            else -> CubicBezierCurve(
                start = start,
                control0 = control0,
                control1 = control1,
                end = end,
            )
        }
    }

    init {
        require(start != control0)
        require(control0 != control1)
        require(control1 != end)
    }


    fun findBoundingBox(): BoundingBox {
        val startPoint = curveFunction.startValue
        val endPoint = curveFunction.endValue

        val inRangeCriticalPointSet = basisFormula.findInterestingCriticalPoints()

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

    override fun splitAt(
        t: Double,
    ): Pair<BezierCurve<*>, BezierCurve<*>> {
        val skeleton0 = basisFormula.findSkeletonCubic(t = t)
        val skeleton1 = skeleton0.findSkeletonQuadratic(t = t)
        val midPoint = skeleton1.evaluateLinear(t = t).toPoint()

        return Pair(
            CubicBezierCurve.of(
                start = start,
                control0 = skeleton0.point0,
                control1 = skeleton1.point0,
                end = midPoint,
            ),
            CubicBezierCurve.of(
                start = midPoint,
                control0 = skeleton1.point1,
                control1 = skeleton0.point2,
                end = end,
            ),
        )
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

        val innerPath = basisFormula.findFaster().toPath2D(
            samplingStrategy = outerSamplingStrategy.copy(
                x0 = 0.0,
                x1 = 1.0,
            ),
        )

        graphics2D.stroke = BasicStroke(2.0f)
        graphics2D.color = innerColor
        graphics2D.draw(innerPath)

        graphics2D.stroke = BasicStroke(0.5f)
        graphics2D.color = Color.LIGHT_GRAY

        basisFormula.segments.forEach { segment ->
            segment.draw(graphics2D = graphics2D)
        }

        graphics2D.color = Color.PINK

        graphics2D.fillCircle(
            center = start,
            radius = 6.0,
        )
        graphics2D.fillCircle(
            center = end,
            radius = 6.0,
        )

        val criticalPointSet = basisFormula.findAllCriticalPoints()

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

    fun isSingularity(): Boolean = setOf(start, control0, control1, end).size == 1

    // FIXME: Use two origin points
    override fun moveInNormalDirection(
        distance: Double,
    ): CubicBezierCurve {
        val startNormalRay = normalRayFunction.startValue
        val startNormalLine = startNormalRay.containingLine

        val endNormalRay = normalRayFunction.endValue
        val endNormalLine = endNormalRay.containingLine

        val normalIntersectionPoint =
            startNormalLine.findIntersectionPoint(endNormalLine) ?: return moveInDirectionPointWise(
                // If there's no intersection point, the start and end vectors are parallel. We could choose either.
                direction = startNormalRay.direction,
                distance = distance,
            )

        return moveAwayPointWise(
            origin = normalIntersectionPoint,
            distance = distance,
        )
    }

    override val firstControl: Point
        get() = control0

    override val lastControl: Point
        get() = control1

    override val basisFormula = CubicBezierFormula(
        vectorSpace = Vector.VectorVectorSpace,
        weight0 = start.toVector(),
        weight1 = control0.toVector(),
        weight2 = control1.toVector(),
        weight3 = end.toVector(),
    )

    fun mapPointWise(
        transform: (Point) -> Point,
    ): CubicBezierCurve = CubicBezierCurve(
        start = transform(start),
        control0 = transform(control0),
        control1 = transform(control1),
        end = transform(end),
    )

    fun moveAwayPointWise(
        origin: Point,
        distance: Double,
    ): CubicBezierCurve = mapPointWise {
        it.moveAway(
            origin = origin,
            distance = distance,
        )
    }

    fun moveInDirectionPointWise(
        direction: Direction,
        distance: Double,
    ): CubicBezierCurve = mapPointWise {
        it.moveInDirection(
            direction = direction,
            distance = distance,
        )
    }

    fun splitAtCriticalPoints(): OpenBezierSpline {
        val criticalPoints = basisFormula.findInterestingCriticalPoints().criticalPoints

        val splitSpline = splitAtMultiple(
            tValues = criticalPoints,
        )

        return splitSpline
    }

    fun toPath2D(): Path2D.Double = Path2D.Double().apply {
        moveTo(start)
        cubicTo(control0, control1, end)
    }

    fun translate(
        translation: Translation,
    ): CubicBezierCurve = mapPointWise {
        it.translate(translation = translation)
    }
}
