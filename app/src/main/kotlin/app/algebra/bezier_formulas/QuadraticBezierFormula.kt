package app.algebra.bezier_formulas

import app.algebra.polynomial_formulas.PolynomialFormula
import app.algebra.polynomial_formulas.QuadraticFormula
import app.algebra.Vector
import app.algebra.VectorSpace
import app.geometry.Point
import app.geometry.Segment

class QuadraticBezierFormula<V>(
    internal val vectorSpace: VectorSpace<V>,
    val weight0: V,
    val weight1: V,
    val weight2: V,
) : BezierFormula<V>() {
    override fun findDerivative(): LinearBezierFormula<V> {
        fun scale2(v: V) = vectorSpace.scale(2.0, v)

        return LinearBezierFormula(
            vectorSpace = vectorSpace,
            weight0 = scale2(vectorSpace.subtract(weight1, weight0)),
            weight1 = scale2(vectorSpace.subtract(weight2, weight1)),
        )
    }

    override fun evaluate(t: Double): V {
        val u = 1.0 - t
        val c1 = vectorSpace.scale(u * u, weight0)
        val c2 = vectorSpace.scale(2.0 * u * t, weight1)
        val c3 = vectorSpace.scale(t * t, weight2)
        return vectorSpace.add(vectorSpace.add(c1, c2), c3)
    }
}

val QuadraticBezierFormula<Vector>.point0: Point
    get() = this.weight0.toPoint()

val QuadraticBezierFormula<Vector>.point1: Point
    get() = this.weight1.toPoint()

val QuadraticBezierFormula<Vector>.point2: Point
    get() = this.weight2.toPoint()

val QuadraticBezierFormula<Vector>.segmentsQuadratic: List<Segment>
    get() = listOf(segment0, segment1)

val QuadraticBezierFormula<Vector>.segment0: Segment
    get() = Segment(start = point0, end = point1)

val QuadraticBezierFormula<Vector>.segment1: Segment
    get() = Segment(start = point1, end = point2)

fun QuadraticBezierFormula<Vector>.findSkeletonQuadratic(
    t: Double,
): LinearBezierFormula<Vector> {
    val subPoint0 = segment0.linearlyInterpolate(t = t)
    val subPoint1 = segment1.linearlyInterpolate(t = t)

    return LinearBezierFormula(
        vectorSpace = vectorSpace,
        weight0 = subPoint0.p,
        weight1 = subPoint1.p,
    )
}

fun QuadraticBezierFormula<Vector>.evaluateFastQuadratic(
    t: Double,
): Vector = findSkeletonQuadratic(t = t).evaluateLinear(t = t)

fun QuadraticBezierFormula<Double>.toPolynomialFormulaQuadratic(): PolynomialFormula = QuadraticFormula.of(
    a = weight0 - 2.0 * weight1 + weight2,
    b = 2.0 * (weight1 - weight0),
    c = weight0,
)

val QuadraticBezierFormula<Vector>.componentXQuadratic
    get() = QuadraticBezierFormula(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.x,
        weight1 = weight1.x,
        weight2 = weight2.x,
    )

val QuadraticBezierFormula<Vector>.componentYQuadratic
    get() = QuadraticBezierFormula(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.y,
        weight1 = weight1.y,
        weight2 = weight2.y,
    )
