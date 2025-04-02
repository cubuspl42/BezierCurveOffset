package app.geometry.bezier_curves

import app.algebra.linear.Vector
import app.algebra.bezier_binomials.*
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.fillCircle
import app.geometry.*
import app.geometry.splines.OpenSpline
import app.geometry.splines.Spline
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
    data class Edge(
        val control0: Point,
        val control1: Point,
    ) : SegmentCurve.Edge<CubicBezierCurve>() {
        override fun bind(
            startKnot: Point,
            endKnot: Point,
        ): CubicBezierCurve = CubicBezierCurve.of(
            start = startKnot,
            control0 = control0,
            control1 = control1,
            end = endKnot,
        )

        override fun dump(): String = """
            CubicBezierCurve.Edge(
                control0 = ${control0.dump()},
                control1 = ${control1.dump()},
            )
        """.trimIndent()
    }

    companion object {
        fun of(
            start: Point,
            control0: Point,
            control1: Point,
            end: Point,
        ): CubicBezierCurve = CubicBezierCurve(
            start = start,
            control0 = control0,
            control1 = control1,
            end = end,
        )
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

        basisFormula.sublines.forEach { segment ->
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

    override fun toSpline(): OpenSpline<CubicBezierCurve> = OpenSpline(
        segments = listOf(
            Spline.Segment.bezier(
                startKnot = start,
                control0 = control0,
                control1 = control1,
            ),
        ),
        terminator = Spline.Terminator(
            endKnot = end,
        ),
    )

    override val edge: SegmentCurve.Edge<CubicBezierCurve>
        get() = CubicBezierCurve.Edge(
            control0 = control0,
            control1 = control1,
        )

    override val basisFormula = CubicBezierBinomial(
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

    fun mapPointWiseOrNull(
        transform: (Point) -> Point?,
    ): CubicBezierCurve? {
        return CubicBezierCurve(
            start = transform(start) ?: return null,
            control0 = transform(control0) ?: return null,
            control1 = transform(control1) ?: return null,
            end = transform(end) ?: return null,
        )
    }

    /**
     * The curve point-wise moved away from [origin] or null if [origin] was
     * one of the control points
     */
    fun moveAwayPointWise(
        origin: Point,
        distance: Double,
    ): CubicBezierCurve? = mapPointWiseOrNull {
        it.moveAway(
            origin = origin,
            distance = distance,
        )
    }

    fun moveInDirectionPointWise(
        direction: Direction,
        distance: Double,
    ): CubicBezierCurve? = mapPointWise {
        it.moveInDirection(
            direction = direction,
            distance = distance,
        )
    }

    override fun toPath2D(): Path2D.Double = Path2D.Double().apply {
        moveTo(start)
        cubicTo(control0, control1, end)
    }

    fun translate(
        translation: Translation,
    ): CubicBezierCurve = mapPointWise {
        it.translate(translation = translation)
    }
}
