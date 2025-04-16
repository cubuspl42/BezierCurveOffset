package app.geometry.curves.bezier

import app.SVGGElementUtils
import app.algebra.NumericObject
import app.algebra.bezier_binomials.CubicBezierBinomial
import app.fill
import app.geometry.BoundingBox
import app.geometry.Direction
import app.geometry.Point
import app.geometry.Ray
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve
import app.geometry.curves.toSvgPath
import app.geometry.transformations.Transformation
import app.geometry.transformations.Translation
import app.stroke
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs

/**
 * A cubic Bézier curve (a Bézier curve of degree 3)
 */
@Suppress("DataClassPrivateConstructor")
data class CubicBezierCurve private constructor(
    override val start: Point,
    val control0: Point,
    val control1: Point,
    override val end: Point,
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
            start = start,
            control0 = control0,
            control1 = control1,
            end = end,
        )

        fun findIntersections(
            lineSegment: LineSegment,
            bezierCurve: CubicBezierCurve,
        ): Set<Point> {
            val tValues = bezierCurve.basisFormula.solve(
                lineFunction = lineSegment.toParametricLineFunction(),
            )

            return IntersectionDetails.build(
                tValues0 = tValues,
                curve0 = bezierCurve,
                lineSegment1 = lineSegment,
            )
        }

        fun findIntersections(
            bezierCurve0: CubicBezierCurve,
            bezierCurve1: CubicBezierCurve,
        ): Set<Point> = TODO()
    }

    override fun findBoundingBox(): BoundingBox {
        val startPoint = basis.startValue
        val endPoint = basis.endValue

        val criticalPointSet = findCriticalPoints()

        val criticalXValues = criticalPointSet.xRoots.map { t -> evaluate(t).x }
        val potentialXExtrema = criticalXValues + startPoint.x + endPoint.x
        val xMin = potentialXExtrema.min()
        val xMax = potentialXExtrema.max()

        val criticalYValues = criticalPointSet.yRoots.map { t -> evaluate(t).y }
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

    internal fun findSkeleton(
        t: Double,
    ): QuadraticBezierCurve {
        val subPoint0 = lineSegment0.evaluate(t = t)
        val subPoint1 = lineSegment1.evaluate(t = t)
        val subPoint2 = lineSegment2.evaluate(t = t)

        return QuadraticBezierCurve(
            start = subPoint0,
            control = subPoint1,
            end = subPoint2,
        )
    }

    override fun splitAt(
        t: Double,
    ): Pair<CubicBezierCurve, CubicBezierCurve> {
        val quadraticSkeleton = findSkeleton(t = t)
        val linearSkeleton = quadraticSkeleton.findSkeleton(t = t)
        val midPoint = linearSkeleton.evaluate(t = t)

        return Pair(
            of(
                start = start,
                control0 = quadraticSkeleton.start,
                control1 = linearSkeleton.start,
                end = midPoint,
            ),
            of(
                start = midPoint,
                control0 = linearSkeleton.end,
                control1 = quadraticSkeleton.end,
                end = end,
            ),
        )
    }

    override fun evaluateSegment(
        t: Double,
    ): Point = findSkeleton(t = t).evaluate(t = t)

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

    override val basisFormula: CubicBezierBinomial = CubicBezierBinomial(
        weight0 = start.pv,
        weight1 = control0.pv,
        weight2 = control1.pv,
        weight3 = end.pv,
    )

    val lineSegment0: LineSegment
        get() = LineSegment.of(
            start = start,
            end = control0,
        )

    val lineSegment1: LineSegment
        get() = LineSegment.of(
            start = control0,
            end = control1,
        )

    val lineSegment2: LineSegment
        get() = LineSegment.of(
            start = control1,
            end = end,
        )

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
