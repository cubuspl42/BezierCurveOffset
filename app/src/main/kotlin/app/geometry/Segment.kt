package app.geometry

import app.fillCircle
import java.awt.Graphics2D
import java.awt.geom.Line2D
import kotlin.math.roundToInt

data class Segment(
    val start: Point,
    val end: Point,
) {
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
    ): Segment? {
        return Segment(
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
}
