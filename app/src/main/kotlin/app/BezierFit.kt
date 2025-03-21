package app

import app.algebra.bezier_formulas.RealFunction
import app.geometry.Point
import app.geometry.bezier_curves.CubicBezierCurve
import org.ujmp.core.Matrix
import org.ujmp.core.matrix.factory.DefaultDenseMatrixFactory

object BezierFit {
    /**
     * The characteristic matrix of the cubic Bézier curve.
     */
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
        // t (normalized path lengths)
        val smallTVector = buildNormalizedPathLengthsVector(points = points)

        // T
        val bigTMatrix = buildBigTMatrix(smallTVector = smallTVector)

        // T^t
        val bigTTransposedMatrix = bigTMatrix.transpose()

        // X (H.x)
        val xVector = buildXVector(points = points)

        // X (H.y)
        val yVector = buildYVector(points = points)

        // T^t * T
        val aMatrix = bigTTransposedMatrix.mtimes(bigTMatrix)

        // (T^t * T)^-1
        val bMatrix = aMatrix.invSafe()

        // (M^-1) * (T^t * T)^-1
        val cMatrix = mInvMatrix.mtimes(bMatrix)

        // (M^-1) * (T^t * T)^-1 * T^t
        val dMatrix = cMatrix.mtimes(bigTTransposedMatrix)

        // P_x (weight X)
        val weightXVector = dMatrix.mtimes(xVector)
        // P_y (weight Y)
        val weightYVector = dMatrix.mtimes(yVector)

        // T * M
        val eMatrix = bigTMatrix.mtimes(mMatrix)

        fun calculateFitError(
            /**
             * H component vector (X or Y)
             */
            hcVector: Matrix,
            /**
             * Weight vector for the given axis (P_x or P_y respectively)
             */
            weightVector: Matrix,
        ): Double {
            // T * M * P_x|y
            val fMatrix = eMatrix.mtimes(weightVector)

            // x|y - T * M * P_x|y
            val gMatrix = hcVector.minus(fMatrix)

            // (x|y - T * M * P_x|y)^t
            val hMatrix = gMatrix.transpose()

            val errorMatrix = hMatrix.mtimes(gMatrix)

            return errorMatrix.single()
        }

        val errorX = calculateFitError(
            hcVector = xVector,
            weightVector = weightXVector,
        )

        val errorY = calculateFitError(
            hcVector = yVector,
            weightVector = weightYVector,
        )

        fun getWeight(i: Long): Point {
            val x = weightXVector.getAsDouble(i, 0)
            val y = weightYVector.getAsDouble(i, 0)

            return Point(x, y)
        }

        val bezierCurve = CubicBezierCurve(
            start = getWeight(0),
            control0 = getWeight(1),
            control1 = getWeight(2),
            end = getWeight(3),
        )


        fun calculateFitError2(): Double = points.zip(smallTVector).sumOf { (p, ti) ->
            val bTi = bezierCurve.pathFunction.evaluate(t = ti)
            bTi.distanceSquaredTo(p)
        }

        val errorJoint = calculateFitError2()

        return bezierCurve
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

    private fun buildBigTMatrix(
        smallTVector: List<Double>,
    ): Matrix = Matrix.Factory.fillFrom(
        collection = smallTVector,
        rowWidth = 4,
    ) { t ->
        doubleArrayOf(
            t * t * t,
            t * t,
            t,
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

private fun Matrix.invSafe(): Matrix = when {
    det() == 0.0 -> invSPD()
    else -> inv()
}

private fun <T> DefaultDenseMatrixFactory.fillFrom(
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

private fun <T> DefaultDenseMatrixFactory.fillColumnFrom(
    collection: Collection<T>,
    buildValue: (T) -> Double,
): Matrix = fillFrom(
    collection = collection,
    rowWidth = 1,
    buildRow = { v -> doubleArrayOf(buildValue(v)) },
)

private fun Matrix.single(): Double {
    if (rowCount != 1L || columnCount != 1L) {
        throw IllegalArgumentException("Matrix must be 1x1, but was ${rowCount}x${columnCount}")
    }

    return getAsDouble(0, 0)
}
