package app

import app.algebra.bezier_formulas.RealFunction
import app.geometry.Point
import app.geometry.bezier_curves.CubicBezierCurve
import org.ujmp.core.Matrix
import kotlin.math.pow
import kotlin.math.sqrt

object BezierFit {
    fun bestFit(points: ArrayList<Point>): Array<Point?> {
        val M = M()
        val Minv = if (M.det() == 0.0) M.invSPD()
        else M.inv()
        val U = U(points)
        val UT = U.transpose()
        val X = X(points)
        val Y = Y(points)

        val A = UT.mtimes(U)
        val B = if (A.det() == 0.0) A.invSPD()
        else A.inv()
        val C = Minv.mtimes(B)
        val D = C.mtimes(UT)
        val E = D.mtimes(X)
        val F = D.mtimes(Y)

        val P: Array<Point?> = arrayOfNulls<Point>(4)
        for (i in 0..3) {
            val x = E.getAsDouble(i.toLong(), 0)
            val y = F.getAsDouble(i.toLong(), 0)

            val p: Point = Point(x, y)
            P[i] = p
        }

        return P
    }

    private fun Y(points: ArrayList<Point>): Matrix {
        val Y: Matrix = Matrix.Factory.fill(0.0, points.size.toLong(), 1)

        for (i in points.indices) Y.setAsDouble(points[i].y, i.toLong(), 0)

        return Y
    }

    private fun X(points: ArrayList<Point>): Matrix {
        val X: Matrix = Matrix.Factory.fill(0.0, points.size.toLong(), 1)

        for (i in points.indices) X.setAsDouble(points[i].x, i.toLong(), 0)

        return X
    }

    private fun U(points: ArrayList<Point>): Matrix {
        val npls = normalizedPathLengths(points)

        return Matrix.Factory.fill(0.0, npls.size.toLong(), 4).apply {
            npls.forEachIndexed { i, nplI ->
                setAsDouble(nplI.pow(3.0), i.toLong(), 0)
                setAsDouble(nplI.pow(2.0), i.toLong(), 1)
                setAsDouble(nplI.pow(1.0), i.toLong(), 2)
                setAsDouble(nplI.pow(0.0), i.toLong(), 3)
            }
        }
    }

    private fun M(): Matrix = Matrix.Factory.fill(0.0, 4, 4).apply {
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

    /** Computes the percentage of path length at each point. Can directly be used as t-indices into the bezier curve.  */
    private fun normalizedPathLengths(points: ArrayList<Point>): DoubleArray {
        val initialDistance = 0.0

        val pathLength = points.zipWithNext().scan(
            initial = initialDistance,
        ) { accDistance, pointPair ->
            val (point, nextPoint) = pointPair

            accDistance + point.distanceTo(nextPoint)
        }

        val totalLength = pathLength.last()

        val relativePathLength = pathLength.map { it / totalLength }

        return relativePathLength.toDoubleArray()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val points: ArrayList<Point> = ArrayList<Point>()

        points.add(Point(0, 0))
        points.add(Point(1, 1))
        points.add(Point(2, 0))
        points.add(Point(3, 2))

        bestFitTest(points)
    }

    private fun bestFitTest(points: ArrayList<Point>) {
        val controlPoints = BezierFit.bestFit(points)

        val bezierCurve = CubicBezierCurve(
            start = controlPoints[0]!!,
            control0 = controlPoints[1]!!,
            control1 = controlPoints[2]!!,
            end = controlPoints[3]!!,
        )

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

        print("Cx:")
        for (p in controlPoints) print(p!!.x.toString() + ",")
        println()

        print("Cy:")
        for (p in controlPoints) print(p!!.y.toString() + ",")
        println()
    }
}
