package app.geometry

import app.fillCircle
import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.CubicBezierCurve
import app.geometry.bezier_curves.ProperBezierCurve
import app.geometry.bezier_curves.SegmentCurve
import app.geometry.splines.OpenSpline
import java.awt.Graphics2D
import java.awt.geom.Line2D
import kotlin.math.roundToInt

/**
 * A line segment, called "subline" for naming reasons
 */
data class Subline(
    override val start: Point,
    override val end: Point,
) : SegmentCurve<Subline>() {
    object Edge : SegmentCurve.Edge<Subline>() {
        override fun bind(
            startKnot: Point,
            endKnot: Point,
        ): Subline = Subline(
            start = startKnot,
            end = endKnot,
        )

        override fun dump(): String = "Subline.Edge"

        override fun transformVia(
            transformation: Transformation,
        ): Edge = Edge

        override fun toString(): String = "Subline.Edge"
    }

    val direction: Direction? = Direction.of(
        end.pv - start.pv,
    )

    fun linearlyInterpolate(t: Double): Point {
        if (t < 0 || t > 1) throw IllegalArgumentException("t must be in [0, 1], was: $t")

        return start.translate(
            translation = Translation.of(
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
    ): Subline? {
        return Subline(
            start = start.moveInDirection(
                direction = direction,
                distance = distance,
            ) ?: return null,
            end = end.moveInDirection(
                direction = direction,
                distance = distance,
            ) ?: return null,
        )
    }

    override fun findOffsetSpline(
        strategy: ProperBezierCurve.OffsetStrategy,
        offset: Double,
    ): OffsetSplineApproximationResult<Subline>? =
        findOffsetSubline(offset = offset)?.let { offsetSubline ->
            object : OffsetSplineApproximationResult<Subline>() {
                override val offsetSpline: OpenSpline<Subline> = offsetSubline.toSpline()

                override val globalDeviation: Double = 0.0
            }
        }

    override val edge: SegmentCurve.Edge<Subline> = Edge

    fun findOffsetSubline(
        offset: Double,
    ): Subline? {
        val offsetDirection = direction?.perpendicular ?: return null

        return Subline(
            start = start.moveInDirection(
                direction = offsetDirection,
                distance = offset,
            ),
            end = end.moveInDirection(
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
}
