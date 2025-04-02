package app.algebra.bezier_binomials

import app.algebra.polynomials.Polynomial
import app.algebra.polynomials.QuadraticPolynomial
import app.algebra.linear.Vector2
import app.algebra.linear.VectorSpace
import app.geometry.Point
import app.geometry.Subline

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

val QuadraticBezierBinomial<Vector2>.point0: Point
    get() = this.weight0.toPoint()

val QuadraticBezierBinomial<Vector2>.point1: Point
    get() = this.weight1.toPoint()

val QuadraticBezierBinomial<Vector2>.point2: Point
    get() = this.weight2.toPoint()

val QuadraticBezierBinomial<Vector2>.segmentsQuadratic: List<Subline>
    get() = listOf(subline0, subline1)

val QuadraticBezierBinomial<Vector2>.subline0: Subline
    get() = Subline(start = point0, end = point1)

val QuadraticBezierBinomial<Vector2>.subline1: Subline
    get() = Subline(start = point1, end = point2)

fun QuadraticBezierBinomial<Vector2>.findSkeletonQuadratic(
    t: Double,
): LinearBezierBinomial<Vector2> {
    val subPoint0 = subline0.linearlyInterpolate(t = t)
    val subPoint1 = subline1.linearlyInterpolate(t = t)

    return LinearBezierBinomial(
        vectorSpace = vectorSpace,
        weight0 = subPoint0.pv,
        weight1 = subPoint1.pv,
    )
}

fun QuadraticBezierBinomial<Vector2>.evaluateFastQuadratic(
    t: Double,
): Vector2 = findSkeletonQuadratic(t = t).evaluateLinear(t = t)

fun QuadraticBezierBinomial<Double>.toPolynomialFormulaQuadratic(): Polynomial? = QuadraticPolynomial.of(
    a = weight0 - 2.0 * weight1 + weight2,
    b = 2.0 * (weight1 - weight0),
    c = weight0,
)

val QuadraticBezierBinomial<Vector2>.componentXQuadratic
    get() = QuadraticBezierBinomial(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.x,
        weight1 = weight1.x,
        weight2 = weight2.x,
    )

val QuadraticBezierBinomial<Vector2>.componentYQuadratic
    get() = QuadraticBezierBinomial(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.y,
        weight1 = weight1.y,
        weight2 = weight2.y,
    )
