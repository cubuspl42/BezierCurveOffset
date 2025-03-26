package app.algebra.bezier_binomials

import app.algebra.polynomials.Polynomial
import app.algebra.polynomials.QuadraticPolynomial
import app.algebra.Vector
import app.algebra.VectorSpace
import app.geometry.Point
import app.geometry.Segment

class QuadraticBezierBinomial<V>(
    internal val vectorSpace: VectorSpace<V>,
    val weight0: V,
    val weight1: V,
    val weight2: V,
) : BezierBinomial<V>() {
    override fun evaluate(t: Double): V {
        val u = 1.0 - t
        val c1 = vectorSpace.scale(u * u, weight0)
        val c2 = vectorSpace.scale(2.0 * u * t, weight1)
        val c3 = vectorSpace.scale(t * t, weight2)
        return vectorSpace.add(vectorSpace.add(c1, c2), c3)
    }
}

val QuadraticBezierBinomial<Vector>.point0: Point
    get() = this.weight0.toPoint()

val QuadraticBezierBinomial<Vector>.point1: Point
    get() = this.weight1.toPoint()

val QuadraticBezierBinomial<Vector>.point2: Point
    get() = this.weight2.toPoint()

val QuadraticBezierBinomial<Vector>.segmentsQuadratic: List<Segment>
    get() = listOf(segment0, segment1)

val QuadraticBezierBinomial<Vector>.segment0: Segment
    get() = Segment(start = point0, end = point1)

val QuadraticBezierBinomial<Vector>.segment1: Segment
    get() = Segment(start = point1, end = point2)

fun QuadraticBezierBinomial<Vector>.findSkeletonQuadratic(
    t: Double,
): LinearBezierBinomial<Vector> {
    val subPoint0 = segment0.linearlyInterpolate(t = t)
    val subPoint1 = segment1.linearlyInterpolate(t = t)

    return LinearBezierBinomial(
        vectorSpace = vectorSpace,
        weight0 = subPoint0.pv,
        weight1 = subPoint1.pv,
    )
}

fun QuadraticBezierBinomial<Vector>.evaluateFastQuadratic(
    t: Double,
): Vector = findSkeletonQuadratic(t = t).evaluateLinear(t = t)

fun QuadraticBezierBinomial<Double>.toPolynomialFormulaQuadratic(): Polynomial? = QuadraticPolynomial.of(
    a = weight0 - 2.0 * weight1 + weight2,
    b = 2.0 * (weight1 - weight0),
    c = weight0,
)

val QuadraticBezierBinomial<Vector>.componentXQuadratic
    get() = QuadraticBezierBinomial(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.x,
        weight1 = weight1.x,
        weight2 = weight2.x,
    )

val QuadraticBezierBinomial<Vector>.componentYQuadratic
    get() = QuadraticBezierBinomial(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.y,
        weight1 = weight1.y,
        weight2 = weight2.y,
    )
