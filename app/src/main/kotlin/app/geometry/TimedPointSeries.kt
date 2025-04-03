package app.geometry

import app.algebra.bezier_binomials.CubicBezierBinomial
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.algebra.bezier_binomials.sample
import app.algebra.linear.MatrixNx4
import app.algebra.linear.Vector1x4
import app.algebra.linear.Vector4x1
import app.fillCircle
import app.fillColumnFrom
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.curves.bezier.BezierCurve
import app.geometry.curves.bezier.TimeFunction
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
        val bMatrix = aMatrix.toUjmpMatrix().invSafe()

        // (M^-1) * (T^t * T)^-1
        val cMatrix = CubicBezierBinomial.characteristicInvertedMatrix.mtimes(bMatrix)

        // (M^-1) * (T^t * T)^-1 * T^t
        val dMatrix = cMatrix.mtimes(bigTTransposedMatrix.toUjmpMatrix())

        // P_x (weight X)
        val weightXVector = dMatrix.mtimes(xVector)
        // P_y (weight Y)
        val weightYVector = dMatrix.mtimes(yVector)

        fun getWeight(i: Long): Point {
            val x = weightXVector.getAsDouble(i, 0)
            val y = weightYVector.getAsDouble(i, 0)

            return Point.of(x, y)
        }

        return CubicBezierCurve.of(
            start = getWeight(0),
            control0 = getWeight(1),
            control1 = getWeight(2),
            end = getWeight(3),
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

    private fun buildXVector(): Matrix = Matrix.Factory.fillColumnFrom(
        collection = timedPoints,
    ) { it.point.x }

    private fun buildYVector(): Matrix = Matrix.Factory.fillColumnFrom(
        collection = timedPoints,
    ) { it.point.y }

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
