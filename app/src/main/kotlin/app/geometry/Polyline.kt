package app.geometry

import app.geometry.bezier_curves.BezierCurve
import java.awt.geom.Path2D

data class Polyline(
    val points: List<Point>,
) {
    init {
        require(points.size >= 2)
    }

    fun toPath2D(): Path2D = Path2D.Double().apply {
        moveTo(points.first())
        points.drop(1).forEach { point ->
            lineTo(point)
        }
    }

    fun bestFitCurve(): BezierCurve = timeNaively().bestFitCurve()

    fun timeNaively(): TimedPointSeries {
        val pathLengthByPointIndex = points.zipWithNext().scan(
            initial = 0.0,
        ) { accDistance, pointPair ->
            val (point, nextPoint) = pointPair

            accDistance + point.distanceTo(nextPoint)
        }

        val totalPathLength = pathLengthByPointIndex.last()

        return TimedPointSeries(
            timedPoints = points.zip(pathLengthByPointIndex) { point, pathLength ->
                TimedPointSeries.TimedPoint(
                    t = pathLength / totalPathLength,
                    point = point,
                )
            },
        )
    }
}
