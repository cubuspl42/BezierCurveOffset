package app

import app.algebra.bezier_formulas.RealFunction
import app.geometry.Point
import app.geometry.bezier_curves.CubicBezierCurve
import org.ujmp.core.Matrix
import org.ujmp.core.matrix.factory.DefaultDenseMatrixFactory
import kotlin.math.pow

fun Matrix.invSafe(): Matrix = when {
    det() == 0.0 -> invSPD()
    else -> inv()
}

fun <T> DefaultDenseMatrixFactory.fillFrom(
    collection: Collection<T>,
    rowWidth: Int,
    buildRow: (T) -> DoubleArray,
): Matrix = this.fill(0.0, collection.size.toLong(), rowWidth.toLong()).apply {
    collection.forEachIndexed { i, v ->
        val row = buildRow(v)

        if (row.size != rowWidth) {
            throw IllegalArgumentException("Row width must be $rowWidth, but was ${row.size}")
        }

        row.forEachIndexed { j, value ->
            setAsDouble(value, i.toLong(), j.toLong())
        }
    }
}

fun <T> DefaultDenseMatrixFactory.fillColumnFrom(
    collection: Collection<T>,
    buildValue: (T) -> Double,
): Matrix = fillFrom(
    collection = collection,
    rowWidth = 1,
    buildRow = { v -> doubleArrayOf(buildValue(v)) },
)

object BezierFit {
    private val mMatrix: Matrix = Matrix.Factory.fill(0.0, 4, 4).apply {
        setAsDouble(-1.0, 0, 0)
        setAsDouble(3.0, 0, 1)
        setAsDouble(-3.0, 0, 2)
        setAsDouble(1.0, 0, 3)

        setAsDouble(3.0, 1, 0)
        setAsDouble(-6.0, 1, 1)
        setAsDouble(3.0, 1, 2)
        setAsDouble(0.0, 1, 3)

        setAsDouble(-3.0, 2, 0)
        setAsDouble(3.0, 2, 1)
        setAsDouble(0.0, 2, 2)
        setAsDouble(0.0, 2, 3)

        setAsDouble(1.0, 3, 0)
        setAsDouble(0.0, 3, 1)
        setAsDouble(0.0, 3, 2)
        setAsDouble(0.0, 3, 3)
    }

    private val mInvMatrix = mMatrix.invSafe()

    fun bestFit(
        points: List<Point>,
    ): CubicBezierCurve {
        val uVector = buildUVector(points = points)
        val uTVector = uVector.transpose()

        val xVector = buildXVector(points = points)
        val yVector = buildYVector(points = points)

        val aMatrix = uTVector.mtimes(uVector)
        val bMatrix = aMatrix.invSafe()

        val cMatrix = mInvMatrix.mtimes(bMatrix)
        val dMatrix = cMatrix.mtimes(uTVector)
        val eMatrix = dMatrix.mtimes(xVector)
        val fMatrix = dMatrix.mtimes(yVector)

        fun getControlPoint(i: Long): Point {
            val x = eMatrix.getAsDouble(i, 0)
            val y = fMatrix.getAsDouble(i, 0)

            return Point(x, y)
        }

        return CubicBezierCurve(
            start = getControlPoint(0),
            control0 = getControlPoint(1),
            control1 = getControlPoint(2),
            end = getControlPoint(3),
        )
    }

    private fun buildYVector(
        points: List<Point>,
    ): Matrix = Matrix.Factory.fillColumnFrom(
        collection = points,
    ) { it.y }

    private fun buildXVector(
        points: List<Point>,
    ): Matrix = Matrix.Factory.fillColumnFrom(
        collection = points,
    ) { it.x }

    private fun buildUVector(
        points: List<Point>,
    ): Matrix = Matrix.Factory.fillFrom(
        collection = buildNormalizedPathLengthsVector(points = points),
        rowWidth = 4,
    ) { n ->
        doubleArrayOf(
            n * n * n,
            n * n,
            n,
            1.0,
        )
    }

    /** Computes the percentage of path length at each point. Can directly be used as t-indices into the bezier curve.  */
    private fun buildNormalizedPathLengthsVector(
        points: List<Point>,
    ): List<Double> {
        val pathLength = points.zipWithNext().scan(
            initial = 0.0,
        ) { accDistance, pointPair ->
            val (point, nextPoint) = pointPair

            accDistance + point.distanceTo(nextPoint)
        }

        val totalLength = pathLength.last()

        return pathLength.map { it / totalLength }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        bestFitTest(
            points = arrayListOf(
                Point(0.0, 0.0),
                Point(1.0, 1.0),
                Point(2.0, 0.0),
                Point(3.0, 2.0),
            ),
        )
    }

    private fun bestFitTest(points: List<Point>) {
        val bezierCurve = BezierFit.bestFit(points = points)

        print("X:")
        for (p in points) print(p.x.toString() + ",")
        println()

        print("Y:")
        for (p in points) print(p.y.toString() + ",")
        println()

        val samples = bezierCurve.basisFormula.sample(
            strategy = RealFunction.SamplingStrategy(
                x0 = 0.0,
                x1 = 1.0,
                xInterval = 0.01,
            ),
        )

        print("Bx:")
        samples.forEach { print("${it.value.x},") }

        println()

        print("By:")

        samples.forEach { print("${it.value.y},") }
        println()

        println(bezierCurve)
    }
}
