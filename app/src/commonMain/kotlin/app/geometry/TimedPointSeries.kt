package app.geometry

import app.algebra.euclidean.bezier_binomials.CubicBezierBinomial
import app.algebra.euclidean.bezier_binomials.RealFunction.SamplingStrategy
import app.algebra.euclidean.bezier_binomials.sample
import app.algebra.linear.matrices.matrix4.MatrixNx4
import app.algebra.linear.matrices.matrix4.RectangularMatrix4
import app.algebra.linear.matrices.matrix4.times
import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.linear.vectors.vectorN.VectorNx1
import app.geometry.curves.bezier.BezierCurve
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.curves.bezier.TimeFunction

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

    fun bestFitCurve(): CubicBezierCurve {
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
        val aMatrixInverted = aMatrix.invert() ?: throw AssertionError("Matrix is not invertible")

        // (M^-1) * (T^t * T)^-1
        val cMatrix = CubicBezierBinomial.characteristicInvertedMatrix * aMatrixInverted

        // (M^-1) * (T^t * T)^-1 * T^t
        val dMatrix = cMatrix * bigTTransposedMatrix

        // P_x (weight X)
        val weightXVector = dMatrix * xVector
        // P_y (weight Y)
        val weightYVector = dMatrix * yVector

        val w0 = Point.of(weightXVector.a0, weightYVector.a0)
        val w1 = Point.of(weightXVector.a1, weightYVector.a1)
        val w2 = Point.of(weightXVector.a2, weightYVector.a2)
        val w3 = Point.of(weightXVector.a3, weightYVector.a3)

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
        val curvePoint = bezierCurve.evaluate(t = t)

        curvePoint.distanceSquaredTo(point)
    }

    private fun buildXVector(): VectorNx1 = VectorNx1(
        elements = timedPoints.map { it.point.x },
    )

    private fun buildYVector(): VectorNx1 = VectorNx1(
        elements = timedPoints.map { it.point.y },
    )

    private fun buildBigTMatrix(): MatrixNx4 = RectangularMatrix4.vertical(
        rows = timedPoints.map { timedPoint ->
            val t = timedPoint.t

            Vector4.horizontal(
                a00 = t * t * t,
                a01 = t * t,
                a02 = t,
                a03 = 1.0,
            )
        },
    )
}
