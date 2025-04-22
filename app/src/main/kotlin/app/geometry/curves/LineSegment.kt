package app.geometry.curves

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.fillCircle
import app.geometry.BoundingBox
import app.geometry.Direction
import app.geometry.ParametricLineFunction
import app.geometry.Point
import app.geometry.RawVector
import app.geometry.Ray
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.splines.OpenSpline
import app.geometry.transformations.Transformation
import app.geometry.transformations.Translation
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegLinetoAbs
import java.awt.Graphics2D
import java.awt.geom.Line2D
import kotlin.math.roundToInt

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

        fun findIntersection(
            lineSegment0: LineSegment,
            lineSegment1: LineSegment,
        ): Set<Point> {
            val t = lineSegment0.toParametricLineFunction().solveIntersection(
                other = lineSegment1.toParametricLineFunction(),
            )

            return IntersectionDetails.build(
                tValues0 = setOfNotNull(t),
                curve0 = lineSegment0,
                lineSegment1 = lineSegment1,
            )
        }
    }

    fun toGeneralLineFunction() = toParametricLineFunction().implicitize()

    fun toParametricLineFunction(): ParametricLineFunction = ParametricLineFunction(
        d = dv,
        s = start.pv,
    )

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

    fun draw(
        graphics2D: Graphics2D,
    ) {
        graphics2D.drawLine(
            start.x.roundToInt(),
            start.y.roundToInt(),
            end.x.roundToInt(),
            end.y.roundToInt(),
        )

        graphics2D.fillCircle(
            center = start,
            radius = 2.0,
        )

        graphics2D.fillCircle(
            center = end,
            radius = 2.0,
        )
    }

    fun toLine2D(): Line2D = Line2D.Double(
        start.x,
        start.y,
        end.x,
        end.y,
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


    fun containsPoint(
        point: Point,
    ): Boolean {
        val t = toParametricLineFunction().solvePoint(p = point.pv) ?: return false
        return t in segmentTRange
    }
}

private fun SegmentCurve<*>.toSvgPathSeg(
    pathElement: SVGPathElement,
): SVGPathSeg = when (this) {
    is LineSegment -> pathElement.createSVGPathSegLinetoAbs(
        end.x.toFloat(),
        end.y.toFloat(),
    )

    is CubicBezierCurve -> pathElement.createSVGPathSegCurvetoCubicAbs(
        end.x.toFloat(),
        end.y.toFloat(),
        control0.x.toFloat(),
        control0.y.toFloat(),
        control1.x.toFloat(),
        control1.y.toFloat(),
    )

    else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
}

fun LineSegment.toSvgPathSegLineSegment(
    pathElement: SVGPathElement,
): SVGPathSegLinetoAbs = pathElement.createSVGPathSegLinetoAbs(
    end.x.toFloat(),
    end.y.toFloat(),
)
