package app.algebra.bezier_binomials

import app.algebra.linear.matrices.matrix4.Matrix4x4
import app.algebra.linear.Vector2
import app.algebra.linear.Vector4
import app.algebra.linear.VectorSpace
import app.geometry.Point
import app.geometry.curves.LineSegment

data class CubicBezierBinomial<V>(
    internal val vectorSpace: VectorSpace<V>,
    val weight0: V,
    val weight1: V,
    val weight2: V,
    val weight3: V,
) : DifferentiableBezierBinomial<V>() {
    companion object {
        /**
         * The characteristic matrix of the cubic Bézier curve.
         */
        val characteristicMatrix = Matrix4x4.rowMajor(
            row0 = Vector4.horizontal(-1.0, 3.0, -3.0, 1.0),
            row1 = Vector4.horizontal(3.0, -6.0, 3.0, 0.0),
            row2 = Vector4.horizontal(-3.0, 3.0, 0.0, 0.0),
            row3 = Vector4.horizontal(1.0, 0.0, 0.0, 0.0),
        )

        val characteristicInvertedMatrix = characteristicMatrix.invert() ?: error("Matrix is not invertible")
    }

    override fun findDerivative(): QuadraticBezierBinomial<V> {
        fun scale3(v: V) = vectorSpace.scale(3.0, v)

        return QuadraticBezierBinomial(
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

val CubicBezierBinomial<Vector2<*>>.point0: Point
    get() = this.weight0.toPoint()

val CubicBezierBinomial<Vector2<*>>.point1: Point
    get() = this.weight1.toPoint()

val CubicBezierBinomial<Vector2<*>>.point2: Point
    get() = this.weight2.toPoint()

val CubicBezierBinomial<Vector2<*>>.point3: Point
    get() = this.weight3.toPoint()

val CubicBezierBinomial<Vector2<*>>.segmentsCubic: List<LineSegment>
    get() = listOf(lineSegment0, lineSegment1, lineSegment2)

val CubicBezierBinomial<Vector2<*>>.lineSegment0: LineSegment
    get() = LineSegment(start = point0, end = point1)

val CubicBezierBinomial<Vector2<*>>.lineSegment1: LineSegment
    get() = LineSegment(start = point1, end = point2)

val CubicBezierBinomial<Vector2<*>>.lineSegment2: LineSegment
    get() = LineSegment(start = point2, end = point3)

val CubicBezierBinomial<Vector2<*>>.componentXCubic
    get() = CubicBezierBinomial(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.x,
        weight1 = weight1.x,
        weight2 = weight2.x,
        weight3 = weight3.x,
    )

val CubicBezierBinomial<Vector2<*>>.componentYCubic
    get() = CubicBezierBinomial(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.y,
        weight1 = weight1.y,
        weight2 = weight2.y,
        weight3 = weight3.y,
    )

fun CubicBezierBinomial<Vector2<*>>.findSkeletonCubic(
    t: Double,
): QuadraticBezierBinomial<Vector2<*>> {
    val subPoint0 = lineSegment0.linearlyInterpolate(t = t)
    val subPoint1 = lineSegment1.linearlyInterpolate(t = t)
    val subPoint2 = lineSegment2.linearlyInterpolate(t = t)

    return QuadraticBezierBinomial(
        vectorSpace = vectorSpace,
        weight0 = subPoint0.pv,
        weight1 = subPoint1.pv,
        weight2 = subPoint2.pv,
    )
}

fun CubicBezierBinomial<Vector2<*>>.evaluateFastCubic(
    t: Double,
): Vector2<*> = findSkeletonCubic(t = t).evaluateFastQuadratic(t = t)
