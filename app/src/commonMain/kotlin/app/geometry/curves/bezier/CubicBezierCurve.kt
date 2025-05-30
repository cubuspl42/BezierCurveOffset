package app.geometry.curves.bezier

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsZeroWithTolerance
import app.algebra.euclidean.bezier_binomials.CubicBezierBinomial
import app.geometry.BoundingBox
import app.geometry.Constants
import app.geometry.Direction
import app.geometry.Point
import app.geometry.Ray
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve
import app.geometry.transformations.Transformation
import app.geometry.transformations.Translation

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
            tolerance: Tolerance,
        ): Boolean = when {
            other !is Edge -> false
            !control0.equalsWithTolerance(other.control0, tolerance = tolerance) -> false
            !control1.equalsWithTolerance(other.control1, tolerance = tolerance) -> false
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
        ): Set<Point> = SegmentCurve.findSegmentCurveIntersections(
            segmentCurve0 = bezierCurve,
            segmentCurve1 = lineSegment,
        )

        fun findIntersections(
            bezierCurve0: CubicBezierCurve,
            bezierCurve1: CubicBezierCurve,
        ): Set<Point> = SegmentCurve.findSegmentCurveIntersections(
            segmentCurve0 = bezierCurve0,
            segmentCurve1 = bezierCurve1,
        )

        fun findIntersectionsBb(
            bezierCurve0: CubicBezierCurve,
            bezierCurve1: CubicBezierCurve,
        ): Set<Point> {
            val bb0 = bezierCurve0.findBoundingBox()
            val bb1 = bezierCurve1.findBoundingBox()

            if (!bb0.overlaps(bb1)) {
                return emptySet()
            }

            val boundingBoxAreaThreshold = 0.1

            if (bb0.area < boundingBoxAreaThreshold && bb1.area < boundingBoxAreaThreshold) {
                return setOf(
                    Point.midPoint(
                        bb0.center,
                        bb1.center,
                    ),
                )
            }

            val (bezierCurve0A, bezierCurve0B) = bezierCurve0.splitAt(t = 0.5)
            val (bezierCurve1A, bezierCurve1B) = bezierCurve1.splitAt(t = 0.5)

            val intersectionPoints0 = findIntersectionsBb(
                bezierCurve0A,
                bezierCurve1A,
            )

            val intersectionPoints1 = findIntersectionsBb(
                bezierCurve0A,
                bezierCurve1B,
            )

            val intersectionPoints2 = findIntersectionsBb(
                bezierCurve0B,
                bezierCurve1A,
            )

            val intersectionPoints3 = findIntersectionsBb(
                bezierCurve0B,
                bezierCurve1B,
            )

            return intersectionPoints0 + intersectionPoints1 + intersectionPoints2 + intersectionPoints3
        }
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

    fun snapPoint(
        point: Point,
    ): Point {
        val invertedBasis = basisFormula.invert()

        val invertedTValue = invertedBasis?.apply(point.pv)?.valueOrNull?.coerceIn(segmentTRange) ?: 0.5

        val projectionPolynomial = basisFormula.findPointProjectionPolynomial(g = point.pv)

        val tolerance = Tolerance.Absolute(absoluteTolerance = 10e-4)

        val roots = projectionPolynomial.findRoots(
            guessedRoot = invertedTValue,
            areClose = { t0, t1 ->
                val p0 = basisFormula.apply(t0).asPoint
                val p1 = basisFormula.apply(t1).asPoint

                val distance = p0.distanceTo(p1)
                distance.equalsZeroWithTolerance(tolerance = tolerance)
            }
        )

        val rootPoints = roots.mapNotNull {
            when {
                it in segmentTRange -> basisFormula.apply(it).asPoint
                else -> null
            }
        }

        val closestPoint = (rootPoints + listOf(start, end)).minBy {
            it.distanceTo(point)
        }

        return closestPoint
    }
}
