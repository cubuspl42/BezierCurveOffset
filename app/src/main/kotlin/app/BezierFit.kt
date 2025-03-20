package app

import app.geometry.Point
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

        val U: Matrix = Matrix.Factory.fill(0.0, npls.size.toLong(), 4)
        for (i in npls.indices) {
            U.setAsDouble(npls[i].pow(3.0), i.toLong(), 0)
            U.setAsDouble(npls[i].pow(2.0), i.toLong(), 1)
            U.setAsDouble(npls[i].pow(1.0), i.toLong(), 2)
            U.setAsDouble(npls[i].pow(0.0), i.toLong(), 3)
        }

        return U
    }

    private fun M(): Matrix {
        val M: Matrix = Matrix.Factory.fill(0.0, 4, 4)
        M.setAsDouble(-1.0, 0, 0)
        M.setAsDouble(3.0, 0, 1)
        M.setAsDouble(-3.0, 0, 2)
        M.setAsDouble(1.0, 0, 3)
        M.setAsDouble(3.0, 1, 0)
        M.setAsDouble(-6.0, 1, 1)
        M.setAsDouble(3.0, 1, 2)
        M.setAsDouble(0.0, 1, 3)
        M.setAsDouble(-3.0, 2, 0)
        M.setAsDouble(3.0, 2, 1)
        M.setAsDouble(0.0, 2, 2)
        M.setAsDouble(0.0, 2, 3)
        M.setAsDouble(1.0, 3, 0)
        M.setAsDouble(0.0, 3, 1)
        M.setAsDouble(0.0, 3, 2)
        M.setAsDouble(0.0, 3, 3)
        return M
    }

    /**
     * Computes b(t).
     */
    private fun pointOnCurve(t: Double, v1: Point, v2: Point, v3: Point, v4: Point): Point {
        val p: Point

        val x1: Double = v1.x
        val x2: Double = v2.x
        val x3: Double = v3.x
        val x4: Double = v4.x

        val y1: Double = v1.y
        val y2: Double = v2.y
        val y3: Double = v3.y
        val y4: Double = v4.y

        val xt =
            (x1 * (1 - t).pow(3.0) + 3 * x2 * t * (1 - t).pow(2.0) + 3 * x3 * t.pow(2.0) * (1 - t) + x4 * t.pow(3.0))

        val yt =
            (y1 * (1 - t).pow(3.0) + 3 * y2 * t * (1 - t).pow(2.0) + 3 * y3 * t.pow(2.0) * (1 - t) + y4 * t.pow(3.0))

        p = Point(xt, yt)

        return p
    }

    /** Computes the percentage of path length at each point. Can directly be used as t-indices into the bezier curve.  */
    private fun normalizedPathLengths(points: ArrayList<Point>): DoubleArray {
        val pathLength = DoubleArray(points.size)

        pathLength[0] = 0.0

        for (i in 1..<points.size) {
            val p1: Point = points[i]
            val p2: Point = points[i - 1]
            val distance = sqrt((p1.x - p2.x).pow(2.0) + (p1.y - p2.y).pow(2.0))
            pathLength[i] += pathLength[i - 1] + distance
        }

        val zpl = DoubleArray(pathLength.size)
        for (i in zpl.indices) zpl[i] = pathLength[i] / pathLength[pathLength.size - 1]

        return zpl
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

        print("X:")
        for (p in points) print(p.x.toString() + ",")
        println()

        print("Y:")
        for (p in points) print(p.y.toString() + ",")
        println()

        print("Bx:")
        run {
            var ti = 0.0
            while (ti <= 1) {
                print(
                    pointOnCurve(
                        ti,
                        controlPoints[0]!!,
                        controlPoints[1]!!,
                        controlPoints[2]!!,
                        controlPoints[3]!!,
                    ).x.toString() + ","
                )
                ti += 0.01
            }
        }
        println()

        print("By:")
        var ti = 0.0
        while (ti <= 1) {
            print(
                pointOnCurve(
                    ti,
                    controlPoints[0]!!,
                    controlPoints[1]!!,
                    controlPoints[2]!!,
                    controlPoints[3]!!,
                ).y.toString() + ","
            )
            ti += 0.01
        }
        println()

        print("Cx:")
        for (p in controlPoints) print(p!!.x.toString() + ",")
        println()

        print("Cy:")
        for (p in controlPoints) print(p!!.y.toString() + ",")
        println()
    }
}
