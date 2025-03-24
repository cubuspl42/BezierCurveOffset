package app.geometry

import app.algebra.bezier_binomials.CubicBezierBinomial
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.algebra.bezier_binomials.sample
import app.fillColumnFrom
import app.fillFrom
import app.geometry.bezier_curves.CubicBezierCurve
import app.geometry.bezier_curves.TimeFunction
import app.fillCircle
import app.geometry.bezier_curves.BezierCurve
import app.invSafe
import org.ujmp.core.Matrix
import java.awt.Color
import java.awt.Graphics2D

data class TimedPointSeries(
    val timedPoints: List<TimedPoint>,
) {
    companion object {
        fun sample(
            /**
             * The curve function, where null indicates that the function is not
             * defined at this place.
             */
            curveFunction: TimeFunction<Point?>,
            sampleCount: Int,
        ): TimedPointSeries? {
            val timedPoints = curveFunction.sample(
                strategy = SamplingStrategy.withSampleCount(sampleCount = sampleCount),
            ).map { pointSample ->
                val t = pointSample.x
                val point = pointSample.value

                TimedPointSeries.TimedPoint(
                    t = t,
                    point = point,
                )
            }

            if (timedPoints.size < 2) {
                // This is an extreme numerical corner case, where not even two
                // samples ended up defined
                return null
            }

            return TimedPointSeries(
                timedPoints = timedPoints,
            )
        }
    }

    data class TimedPoint(
        val t: Double,
        val point: Point,
    )

    init {
        require(timedPoints.size >= 2)
    }

    fun draw(
        graphics2D: Graphics2D,
        color: Color,
    ) {
        graphics2D.color = color

        timedPoints.forEach { timedPoint ->
            graphics2D.fillCircle(
                center = timedPoint.point,
                radius = 1.5,
            )
        }
    }

    fun bestFitCurve(): BezierCurve<*> {
        // T
        val bigTMatrix = buildBigTMatrix()

        // T^t
        val bigTTransposedMatrix = bigTMatrix.transpose()

        // X (H.x)
        val xVector = buildXVector()

        // X (H.y)
        val yVector = buildYVector()

        // T^t * T
        val aMatrix = bigTTransposedMatrix.mtimes(bigTMatrix)

        // (T^t * T)^-1
        val bMatrix = aMatrix.invSafe()

        // (M^-1) * (T^t * T)^-1
        val cMatrix = CubicBezierBinomial.characteristicInvertedMatrix.mtimes(bMatrix)

        // (M^-1) * (T^t * T)^-1 * T^t
        val dMatrix = cMatrix.mtimes(bigTTransposedMatrix)

        // P_x (weight X)
        val weightXVector = dMatrix.mtimes(xVector)
        // P_y (weight Y)
        val weightYVector = dMatrix.mtimes(yVector)

        fun getWeight(i: Long): Point {
            val x = weightXVector.getAsDouble(i, 0)
            val y = weightYVector.getAsDouble(i, 0)

            return Point(x, y)
        }

        return CubicBezierCurve.of(
            start = getWeight(0),
            control0 = getWeight(1),
            control1 = getWeight(2),
            end = getWeight(3),
        )
    }

    fun calculateFitError(
        bezierCurve: CubicBezierCurve,
    ): Double = timedPoints.sumOf { timedPoint ->
        val t = timedPoint.t
        val point = timedPoint.point
        val curvePoint = bezierCurve.curveFunction.evaluate(t = t)

        curvePoint.distanceSquaredTo(point)
    }

    private fun buildXVector(): Matrix = Matrix.Factory.fillColumnFrom(
        collection = timedPoints,
    ) { it.point.x }

    private fun buildYVector(): Matrix = Matrix.Factory.fillColumnFrom(
        collection = timedPoints,
    ) { it.point.y }

    private fun buildBigTMatrix(): Matrix = Matrix.Factory.fillFrom(
        collection = timedPoints,
        rowWidth = 4,
    ) { timedPoint ->
        val t = timedPoint.t

        doubleArrayOf(
            t * t * t,
            t * t,
            t,
            1.0,
        )
    }
}
