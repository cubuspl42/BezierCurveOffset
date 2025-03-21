package app.geometry

import app.algebra.bezier_formulas.CubicBezierFormula
import app.fillColumnFrom
import app.fillFrom
import app.geometry.bezier_curves.CubicBezierCurve
import app.invSafe
import org.ujmp.core.Matrix

data class TimedPolyline(
    val timedPoints: List<TimedPoint>,
) {
    data class TimedPoint(
        val t: Double,
        val point: Point,
    )

    init {
        assert(timedPoints.size >= 2)
    }

    fun toPolyline(): Polyline = Polyline(
        points = timedPoints.map { it.point },
    )

    fun bestFitCurve(): CubicBezierCurve {
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
        val cMatrix = CubicBezierFormula.characteristicInvertedMatrix.mtimes(bMatrix)

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

        return CubicBezierCurve(
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
        val curvePoint = bezierCurve.pathFunction.evaluate(t = t)

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
