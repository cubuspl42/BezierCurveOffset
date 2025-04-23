package app.geometry

import app.geometry.curves.bezier.BezierCurve

data class Polyline(
    val points: List<Point>,
) {
    init {
        require(points.size >= 2)
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
