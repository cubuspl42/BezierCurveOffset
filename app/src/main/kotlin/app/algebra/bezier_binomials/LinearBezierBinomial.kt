package app.algebra.bezier_binomials

import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.VectorSpace
import app.algebra.polynomials.LinearPolynomial
import app.algebra.polynomials.Polynomial
import app.geometry.Point
import app.geometry.curves.LineSegment

data class LinearBezierBinomial<V>(
    internal val vectorSpace: VectorSpace<V>,
    val weight0: V,
    val weight1: V,
) : BezierBinomial<V>() {
    override fun evaluate(t: Double): V = evaluateLinear(t = t)

    fun evaluateLinear(t: Double): V {
        val u = 1.0 - t
        val c1 = vectorSpace.scale(u, weight0)
        val c2 = vectorSpace.scale(t, weight1)
        return vectorSpace.add(c1, c2)
    }
}

val LinearBezierBinomial<Vector2<*>>.point0: Point
    get() = this.weight0.toPoint()

val LinearBezierBinomial<Vector2<*>>.point1: Point
    get() = this.weight1.toPoint()

val LinearBezierBinomial<Vector2<*>>.lineSegment0: LineSegment
    get() = LineSegment(start = point0, end = point1)

val LinearBezierBinomial<Vector2<*>>.segmentsLinear: List<LineSegment>
    get() = listOf(lineSegment0)

fun LinearBezierBinomial<Double>.toPolynomialFormulaLinear(): Polynomial? = LinearPolynomial.of(
    a = weight1 - weight0,
    b = weight0,
)

val LinearBezierBinomial<Vector2<*>>.componentXLinear
    get() = LinearBezierBinomial(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.x,
        weight1 = weight1.x,
    )

val LinearBezierBinomial<Vector2<*>>.componentYLinear
    get() = LinearBezierBinomial(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.y,
        weight1 = weight1.y,
    )
