package app.geometry

import java.awt.geom.Path2D

data class Polyline(
    val points: List<Point>,
) {
    init {
        assert(points.size >= 2)
    }

    fun toPath2D(): Path2D = Path2D.Double().apply {
        moveTo(points.first())
        points.drop(1).forEach { point ->
            lineTo(point)
        }
    }
}
