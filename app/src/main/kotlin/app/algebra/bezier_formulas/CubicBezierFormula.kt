package app.algebra.bezier_formulas

import app.algebra.Vector
import app.algebra.VectorSpace
import app.geometry.Point
import app.geometry.Segment
import app.invSafe
import org.ujmp.core.Matrix

data class CubicBezierFormula<V>(
    internal val vectorSpace: VectorSpace<V>,
    val weight0: V,
    val weight1: V,
    val weight2: V,
    val weight3: V,
) : BezierFormula<V>() {
    companion object {
        /**
         * The characteristic matrix of the cubic Bézier curve.
         */
        val characteristicMatrix: Matrix = Matrix.Factory.fill(0.0, 4, 4).apply {
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

        val characteristicInvertedMatrix = characteristicMatrix.invSafe()
    }

    override fun findDerivative(): QuadraticBezierFormula<V> {
        fun scale3(v: V) = vectorSpace.scale(3.0, v)

        return QuadraticBezierFormula(
            vectorSpace = vectorSpace,
            weight0 = scale3(vectorSpace.subtract(weight1, weight0)),
            weight1 = scale3(vectorSpace.subtract(weight2, weight1)),
            weight2 = scale3(vectorSpace.subtract(weight3, weight2)),
        )
    }

    override fun evaluate(t: Double): V {
        val u = 1.0 - t
        val c1 = vectorSpace.scale(u * u * u, weight0)
        val c2 = vectorSpace.scale(3.0 * u * u * t, weight1)
        val c3 = vectorSpace.scale(3.0 * u * t * t, weight2)
        val c4 = vectorSpace.scale(t * t * t, weight3)
        return vectorSpace.add(vectorSpace.add(c1, c2), vectorSpace.add(c3, c4))
    }
}

val CubicBezierFormula<Vector>.point0: Point
    get() = this.weight0.toPoint()

val CubicBezierFormula<Vector>.point1: Point
    get() = this.weight1.toPoint()

val CubicBezierFormula<Vector>.point2: Point
    get() = this.weight2.toPoint()

val CubicBezierFormula<Vector>.point3: Point
    get() = this.weight3.toPoint()

val CubicBezierFormula<Vector>.segmentsCubic: List<Segment>
    get() = listOf(segment0, segment1, segment2)

val CubicBezierFormula<Vector>.segment0: Segment
    get() = Segment(start = point0, end = point1)

val CubicBezierFormula<Vector>.segment1: Segment
    get() = Segment(start = point1, end = point2)

val CubicBezierFormula<Vector>.segment2: Segment
    get() = Segment(start = point2, end = point3)

val CubicBezierFormula<Vector>.componentXCubic
    get() = CubicBezierFormula(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.x,
        weight1 = weight1.x,
        weight2 = weight2.x,
        weight3 = weight3.x,
    )

val CubicBezierFormula<Vector>.componentYCubic
    get() = CubicBezierFormula(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.y,
        weight1 = weight1.y,
        weight2 = weight2.y,
        weight3 = weight3.y,
    )

fun CubicBezierFormula<Vector>.findSkeletonCubic(
    t: Double,
): QuadraticBezierFormula<Vector> {
    val subPoint0 = segment0.linearlyInterpolate(t = t)
    val subPoint1 = segment1.linearlyInterpolate(t = t)
    val subPoint2 = segment2.linearlyInterpolate(t = t)

    return QuadraticBezierFormula(
        vectorSpace = vectorSpace,
        weight0 = subPoint0.pv,
        weight1 = subPoint1.pv,
        weight2 = subPoint2.pv,
    )
}

fun CubicBezierFormula<Vector>.evaluateFastCubic(
    t: Double,
): Vector = findSkeletonCubic(t = t).evaluateFastQuadratic(t = t)
