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

    fun timeNaively(): TimedPolyline {
        val pathLengthByPointIndex = points.zipWithNext().scan(
            initial = 0.0,
        ) { accDistance, pointPair ->
            val (point, nextPoint) = pointPair

            accDistance + point.distanceTo(nextPoint)
        }

        val totalPathLength = pathLengthByPointIndex.last()

        return TimedPolyline(
            timedPoints = points.zip(pathLengthByPointIndex) { point, pathLength ->
                TimedPolyline.TimedPoint(
                    t = pathLength / totalPathLength,
                    point = point,
                )
            },
        )
    }
}
