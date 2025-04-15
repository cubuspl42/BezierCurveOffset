package app.geometry.curves.bezier

import app.SVGGElementUtils
import app.algebra.NumericObject
import app.algebra.bezier_binomials.*
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.fill
import app.fillCircle
import app.geometry.*
import app.geometry.Curve.IntersectionDetails
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve
import app.geometry.curves.filterLineSegmentIntersection0
import app.geometry.curves.toSvgPath
import app.geometry.transformations.Rotation
import app.geometry.transformations.Transformation
import app.geometry.transformations.Translation
import app.stroke
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D

/**
 * A cubic Bézier curve (a Bézier curve of degree 3)
 */
@Suppress("DataClassPrivateConstructor")
data class CubicBezierCurve private constructor(
    internal val rawCubicBezierCurve: RawCubicBezierCurve,
) : BezierCurve() {
    data class Edge(
        val control0: Point,
        val control1: Point,
    ) : SegmentCurve.Edge<CubicBezierCurve>() {
        override fun bind(
            startKnot: Point,
            endKnot: Point,
        ): CubicBezierCurve = of(
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

        override fun transformVia(
            transformation: Transformation,
        ): Edge = Edge(
            control0 = control0.transformVia(transformation = transformation),
            control1 = control1.transformVia(transformation = transformation),
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            absoluteTolerance: Double,
        ): Boolean = when {
            other !is Edge -> false
            !control0.equalsWithTolerance(other.control0, absoluteTolerance = absoluteTolerance) -> false
            !control1.equalsWithTolerance(other.control1, absoluteTolerance = absoluteTolerance) -> false
            else -> true
        }
    }

    companion object {
        fun of(
            start: Point,
            control0: Point,
            control1: Point,
            end: Point,
        ): CubicBezierCurve = CubicBezierCurve(
            rawCubicBezierCurve = RawCubicBezierCurve.of(
                point0 = start,
                point1 = control0,
                point2 = control1,
                point3 = end,
            ),
        )

        fun findIntersections(
            lineSegment: LineSegment,
            bezierCurve: CubicBezierCurve,
        ): Set<IntersectionDetails<LineSegment, CubicBezierCurve>> {
            return RawCubicBezierCurve.findIntersections(
                rawLine = lineSegment.rawLine ?: return emptySet(),
                bezierCurve = bezierCurve.rawCubicBezierCurve,
            ).mapNotNull {
                it.filterLineSegmentIntersection0()?.filterCubicBezierCurveIntersection1()
            }.toSet()
        }

        fun findIntersections(
            bezierCurve0: CubicBezierCurve,
            bezierCurve1: CubicBezierCurve,
        ): Set<IntersectionDetails<CubicBezierCurve, CubicBezierCurve>> = RawCubicBezierCurve.findIntersections(
            rawBezierCurve0 = bezierCurve0.rawCubicBezierCurve,
            rawBezierCurve1 = bezierCurve1.rawCubicBezierCurve,
        ).mapNotNull {
            it.filterCubicBezierCurveIntersection0()?.filterCubicBezierCurveIntersection1()
        }.toSet()
    }

    override val start: Point
        get() = rawCubicBezierCurve.point0

    val control0: Point
        get() = rawCubicBezierCurve.point1

    val control1: Point
        get() = rawCubicBezierCurve.point2

    override val end: Point
        get() = rawCubicBezierCurve.point3

    override fun findBoundingBox(): BoundingBox {
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

        return BoundingBox.of(
            xMin = xMin,
            xMax = xMax,
            yMin = yMin,
            yMax = yMax,
        )
    }

    override fun splitAt(
        t: Double,
    ): Pair<CubicBezierCurve, CubicBezierCurve> {
        val skeleton0 = basisFormula.findSkeletonCubic(t = t)
        val skeleton1 = skeleton0.findSkeletonQuadratic(t = t)
        val midPoint = skeleton1.evaluateLinear(t = t).asPoint

        return Pair(
            of(
                start = start,
                control0 = skeleton0.point0,
                control1 = skeleton1.point0,
                end = midPoint,
            ),
            of(
                start = midPoint,
                control0 = skeleton1.point1,
                control1 = skeleton0.point2,
                end = end,
            ),
        )
    }

    override fun evaluateSegment(
        t: Double,
    ): Point = basisFormula.evaluate(t = t).asPoint

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

        basisFormula.lineSegments.forEach { segment ->
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
                val extremityPoint = basisFormula.evaluate(t = extremityT).asPoint

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

    override val edge: SegmentCurve.Edge<CubicBezierCurve>
        get() = Edge(
            control0 = control0,
            control1 = control1,
        )

    override val frontRay: Ray?
        get() = tangentRayFunction.startValue?.opposite

    override val backRay: Ray?
        get() = tangentRayFunction.endValue

    override val simplified: SegmentCurve<*>
        get() = when {
            start == control0 && control1 == end -> LineSegment.of(
                start = start,
                end = end,
            )

            else -> this
        }

    override val basisFormula: CubicBezierBinomial<RawVector>
        get() = rawCubicBezierCurve.basisFormula

    val lineSegment0: LineSegment
        get() = basisFormula.lineSegment0

    val lineSegment1: LineSegment
        get() = basisFormula.lineSegment1

    val lineSegment2: LineSegment
        get() = basisFormula.lineSegment2

    fun transformVia(
        transformation: Transformation,
    ): CubicBezierCurve = mapPointWise {
        it.transformVia(
            transformation = transformation,
        )
    }

    fun mapPointWise(
        transform: (Point) -> Point,
    ): CubicBezierCurve = CubicBezierCurve.of(
        start = transform(start),
        control0 = transform(control0),
        control1 = transform(control1),
        end = transform(end),
    )

    fun mapPointWiseOrNull(
        transform: (Point) -> Point?,
    ): CubicBezierCurve? {
        return CubicBezierCurve.of(
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
        it.translateInDirection(
            direction = direction,
            distance = distance,
        )
    }

    fun translate(
        translation: Translation,
    ): CubicBezierCurve = mapPointWise {
        it.transformVia(translation)
    }
}

internal fun <Curve1 : Curve> IntersectionDetails<RawCubicBezierCurve, Curve1>.filterCubicBezierCurveIntersection0(): IntersectionDetails<CubicBezierCurve, Curve1>? {
    @Suppress("UNCHECKED_CAST") return when {
        t0 in SegmentCurve.segmentTRange -> this as IntersectionDetails<CubicBezierCurve, Curve1>
        else -> null
    }
}

internal fun <Curve0 : Curve> IntersectionDetails<Curve0, RawCubicBezierCurve>.filterCubicBezierCurveIntersection1(): IntersectionDetails<Curve0, CubicBezierCurve>? {
    @Suppress("UNCHECKED_CAST") return when {
        t1 in SegmentCurve.segmentTRange -> this as IntersectionDetails<Curve0, CubicBezierCurve>
        else -> null
    }
}

fun CubicBezierCurve.toDebugControlSvgPathGroupCubic(
    document: SVGDocument,
): SVGGElement = SVGGElementUtils.of(
    document = document,
    elements = listOf(
        lineSegment0.toSvgPath(
            document = document,
        ).apply {
            fill = "none"
            stroke = "darkGray"
        },
//        lineSegment1.toSvgPath(
//            document = document,
//        ).apply {
//            fill = "none"
//            stroke = "lightGray"
//        },
        lineSegment2.toSvgPath(
            document = document,
        ).apply {
            fill = "none"
            stroke = "darkGray"
        },
    ),
)

fun CubicBezierCurve.toSvgPathSegCubic(
    pathElement: SVGPathElement,
): SVGPathSegCurvetoCubicAbs = pathElement.createSVGPathSegCurvetoCubicAbs(
    end.x.toFloat(),
    end.y.toFloat(),
    control0.x.toFloat(),
    control0.y.toFloat(),
    control1.x.toFloat(),
    control1.y.toFloat(),
)
