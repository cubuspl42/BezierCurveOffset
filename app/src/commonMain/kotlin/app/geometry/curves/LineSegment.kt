package app.geometry.curves

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.euclidean.ParametricLineFunction
import app.geometry.BoundingBox
import app.geometry.Direction
import app.geometry.Point
import app.geometry.RawVector
import app.geometry.Ray
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.splines.OpenSpline
import app.geometry.transformations.Transformation
import app.geometry.transformations.Translation

/**
 * A line segment
 */
data class LineSegment(
    override val start: Point,
    override val end: Point,
) : SegmentCurve<LineSegment>() {
    object Edge : SegmentCurve.Edge<LineSegment>() {
        override fun bind(
            startKnot: Point,
            endKnot: Point,
        ): LineSegment = LineSegment.of(
            start = startKnot,
            end = endKnot,
        )

        override fun dump(): String = "LineSegment.Edge"

        override fun transformVia(
            transformation: Transformation,
        ): Edge = Edge

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: Tolerance,
        ): Boolean = other is Edge

        override fun toString(): String = "LineSegment.Edge"
    }

    companion object {
        fun of(
            start: Point,
            end: Point,
        ): LineSegment = LineSegment(
            start = start,
            end = end,
        )

        fun findIntersections(
            lineSegment0: LineSegment,
            lineSegment1: LineSegment,
        ): Set<Point> = setOfNotNull(
            findIntersection(
                lineSegment0 = lineSegment0,
                lineSegment1 = lineSegment1,
            ),
        )

        fun findIntersection(
            lineSegment0: LineSegment,
            lineSegment1: LineSegment,
        ): Point? {
            val l0 = lineSegment0.basisFormula
            val l1 = lineSegment1.basisFormula

            val t0 = l0.solveIntersection(l1) ?: return null

            if (t0 !in segmentTRange) return null

            val potentialIntersectionPoint = l0.apply(t0)

            val t1 = l1.solvePoint(
                potentialIntersectionPoint,
                tolerance = Tolerance.Zero,
            ) ?: return null

            if (t1 !in segmentTRange) return null

            return potentialIntersectionPoint.asPoint
        }
    }

    private val dv: RawVector
        get() = end.pv - start.pv

    val direction: Direction? = Direction.of(
        end.pv - start.pv,
    )

    override fun evaluateSegment(
        t: Double,
    ): Point = start.transformVia(
        transformation = Translation.of(
            tv = (end.pv - start.pv) * t,
        ),
    )

    fun moveInDirection(
        direction: Direction,
        distance: Double,
    ): LineSegment? {
        return LineSegment.of(
            start = start.translateInDirection(
                direction = direction,
                distance = distance,
            ) ?: return null,
            end = end.translateInDirection(
                direction = direction,
                distance = distance,
            ) ?: return null,
        )
    }

    override fun findOffsetSpline(
        offset: Double,
    ): OpenSpline<LineSegment, OffsetEdgeMetadata, *>? = findOffsetLineSegment(
        offset = offset,
    )?.toSpline(
        edgeMetadata = OffsetEdgeMetadata.Precise,
    )

    override val basisFormula = ParametricLineFunction(
        d = dv,
        s = start.pv,
    )

    override fun findOffsetSplineRecursive(
        offset: Double,
        subdivisionLevel: Int,
    ): OpenSpline<*, OffsetEdgeMetadata, *>? {
        // We ignore the subdivision level, because lineSegment offset is always optimal and safe to compute (unless it's
        // a point)
        return findOffsetSpline(
            offset = offset,
        )
    }

    override fun findBoundingBox(): BoundingBox = BoundingBox.of(
        pointA = start,
        pointB = end,
    )

    override val edge: SegmentCurve.Edge<LineSegment> = Edge

    fun findOffsetLineSegment(
        offset: Double,
    ): LineSegment? {
        val offsetDirection = direction?.perpendicular ?: return null

        return LineSegment.of(
            start = start.translateInDirection(
                direction = offsetDirection,
                distance = offset,
            ),
            end = end.translateInDirection(
                direction = offsetDirection,
                distance = offset,
            ),
        )
    }

    override val frontRay: Ray?
        get() = direction?.opposite?.let {
            Ray.inDirection(
                point = start,
                direction = it,
            )
        }

    override val backRay: Ray?
        get() = direction?.let {
            Ray.inDirection(
                point = end,
                direction = it,
            )
        }

    override val simplified: SegmentCurve<*>
        get() = this

}
