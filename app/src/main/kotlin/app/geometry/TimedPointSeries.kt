package app.geometry

import app.algebra.bezier_binomials.CubicBezierBinomial
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.algebra.bezier_binomials.sample
import app.algebra.linear.MatrixNx4
import app.algebra.linear.Vector1x4
import app.algebra.linear.VectorNx1
import app.fillCircle
import app.geometry.curves.bezier.BezierCurve
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.curves.bezier.TimeFunction
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
                strategy = SamplingStrategy(sampleCount = sampleCount),
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

    fun bestFitCurve(): BezierCurve {
        // T
        val bigTMatrix = buildBigTMatrix()

        // T^t
        val bigTTransposedMatrix = bigTMatrix.transposed

        // X (H.x)
        val xVector = buildXVector()

        // X (H.y)
        val yVector = buildYVector()

        // T^t * T
        val aMatrix = bigTTransposedMatrix * bigTMatrix

        // (T^t * T)^-1
        val bMatrix = aMatrix.invert() ?: throw AssertionError("Matrix is not invertible")

        // (M^-1) * (T^t * T)^-1
        val cMatrix = CubicBezierBinomial.characteristicInvertedMatrix * bMatrix.calculate()

        // (M^-1) * (T^t * T)^-1 * T^t
        val dMatrix = cMatrix * bigTTransposedMatrix

        // P_x (weight X)
        val weightXVector = dMatrix * xVector
        // P_y (weight Y)
        val weightYVector = dMatrix * yVector

        val w0 = Point.of(weightXVector.x, weightYVector.x)
        val w1 = Point.of(weightXVector.y, weightYVector.y)
        val w2 = Point.of(weightXVector.z, weightYVector.z)
        val w3 = Point.of(weightXVector.w, weightYVector.w)

        return CubicBezierCurve.of(
            start = w0,
            control0 = w1,
            control1 = w2,
            end = w3,
        )
    }

    fun calculateFitError(
        bezierCurve: BezierCurve,
    ): Double = timedPoints.sumOf { timedPoint ->
        val t = timedPoint.t
        val point = timedPoint.point
        val curvePoint = bezierCurve.curveFunction.evaluate(t = t)

        curvePoint.distanceSquaredTo(point)
    }

    private fun buildXVector(): VectorNx1 = VectorNx1(
        xs = timedPoints.map { it.point.x },
    )

    private fun buildYVector(): VectorNx1 = VectorNx1(
        xs = timedPoints.map { it.point.y },
    )

    private fun buildBigTMatrix(): MatrixNx4 = MatrixNx4(
        rows = timedPoints.map { timedPoint ->
            val t = timedPoint.t

            Vector1x4.of(
                x = t * t * t,
                y = t * t,
                z = t,
                w = 1.0,
            )
        },
    )
}
