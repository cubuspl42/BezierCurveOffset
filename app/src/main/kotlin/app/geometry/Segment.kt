package app.geometry

import app.fillCircle
import java.awt.Graphics2D
import kotlin.math.roundToInt

data class Segment(
    val start: Point,
    val end: Point,
) {
    fun linearlyInterpolate(t: Double): Point {
        if (t < 0 || t > 1) throw IllegalArgumentException("t must be in [0, 1]")

        return start.translate(
            translation = Translation(
                tv = (end.p - start.p).scale(t),
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
}
