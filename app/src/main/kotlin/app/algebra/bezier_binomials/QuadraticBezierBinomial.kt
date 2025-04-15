package app.algebra.bezier_binomials

import app.algebra.linear.VectorSpace
import app.algebra.polynomials.Polynomial
import app.algebra.polynomials.QuadraticPolynomial
import app.geometry.Point
import app.geometry.RawVector
import app.geometry.curves.LineSegment

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

val QuadraticBezierBinomial<RawVector>.point0: Point
    get() = this.weight0.asPoint

val QuadraticBezierBinomial<RawVector>.point1: Point
    get() = this.weight1.asPoint

val QuadraticBezierBinomial<RawVector>.point2: Point
    get() = this.weight2.asPoint

val QuadraticBezierBinomial<RawVector>.segmentsQuadratic: List<LineSegment>
    get() = listOf(lineSegment0, lineSegment1)

val QuadraticBezierBinomial<RawVector>.lineSegment0: LineSegment
    get() = LineSegment.of(start = point0, end = point1)

val QuadraticBezierBinomial<RawVector>.lineSegment1: LineSegment
    get() = LineSegment.of(start = point1, end = point2)

fun QuadraticBezierBinomial<RawVector>.findSkeletonQuadratic(
    t: Double,
): LinearBezierBinomial<RawVector> {
    val subPoint0 = lineSegment0.evaluate(t = t)
    val subPoint1 = lineSegment1.evaluate(t = t)

    return LinearBezierBinomial(
        vectorSpace = vectorSpace,
        weight0 = subPoint0.pv,
        weight1 = subPoint1.pv,
    )
}

fun QuadraticBezierBinomial<RawVector>.evaluateFastQuadratic(
    t: Double,
): RawVector = findSkeletonQuadratic(t = t).evaluateLinear(t = t)

fun QuadraticBezierBinomial<Double>.toPolynomialFormulaQuadratic(): Polynomial? = QuadraticPolynomial.of(
    a = weight0 - 2.0 * weight1 + weight2,
    b = 2.0 * (weight1 - weight0),
    c = weight0,
)

val QuadraticBezierBinomial<RawVector>.componentXQuadratic
    get() = QuadraticBezierBinomial(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.x,
        weight1 = weight1.x,
        weight2 = weight2.x,
    )

val QuadraticBezierBinomial<RawVector>.componentYQuadratic
    get() = QuadraticBezierBinomial(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.y,
        weight1 = weight1.y,
        weight2 = weight2.y,
    )
