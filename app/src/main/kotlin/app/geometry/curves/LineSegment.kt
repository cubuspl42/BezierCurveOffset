package app.geometry.curves

import app.algebra.NumericObject
import app.fillCircle
import app.geometry.BoundingBox
import app.geometry.Direction
import app.geometry.Point
import app.geometry.Ray
import app.geometry.curves.bezier.BezierCurve
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
        ): LineSegment = LineSegment(
            start = startKnot,
            end = endKnot,
        )

        override fun dump(): String = "LineSegment.Edge"

        override fun transformVia(
            transformation: Transformation,
        ): Edge = Edge

        override fun equalsWithTolerance(
            other: NumericObject,
            absoluteTolerance: Double,
        ): Boolean = other is Edge

        override fun toString(): String = "LineSegment.Edge"
    }

    val direction: Direction? = Direction.of(
        end.pv - start.pv,
    )

    fun linearlyInterpolate(t: Double): Point {
        if (t < 0 || t > 1) throw IllegalArgumentException("t must be in [0, 1], was: $t")

        return start.transformVia(
            transformation = Translation.of(
                tv = (end.pv - start.pv).scale(t),
            ),
        )
    }

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
        return LineSegment(
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
        strategy: BezierCurve.OffsetStrategy,
        offset: Double,
    ): OpenSpline<LineSegment, OffsetEdgeMetadata>? = findOffsetLineSegment(
        offset = offset,
    )?.toSpline(
        edgeMetadata = OffsetEdgeMetadata.Precise,
    )

    override fun findOffsetSplineRecursive(
        strategy: BezierCurve.OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): OpenSpline<*, OffsetEdgeMetadata>? {
        // We ignore the subdivision level, because lineSegment offset is always optimal and safe to compute (unless it's
        // a point)
        return findOffsetSpline(
            strategy = strategy,
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

        return LineSegment(
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

    override val frontRay: Ray
        get() = Ray.inDirection(
            point = start,
            direction = direction!!.opposite,
        )

    override val backRay: Ray
        get() = Ray.inDirection(
            point = end,
            direction = direction!!,
        )

    override val simplified: SegmentCurve<*>
        get() = this
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
