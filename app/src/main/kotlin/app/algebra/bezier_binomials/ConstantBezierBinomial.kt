package app.algebra.bezier_binomials

import app.algebra.Vector
import app.algebra.VectorSpace
import app.algebra.polynomials.ConstantPolynomial
import app.geometry.Point
import app.geometry.Segment

data class ConstantBezierBinomial<V>(
    private val vectorSpace: VectorSpace<V>,
    val weight0: V,
) : BezierBinomial<V>() {
    private val zero: ConstantBezierBinomial<V>
        get() = ConstantBezierBinomial(
            vectorSpace = vectorSpace,
            weight0 = vectorSpace.zero,
        )

    override fun findDerivative(): BezierBinomial<V> = zero

    override fun evaluate(t: Double): V = evaluateConstant(t = t)

    fun evaluateConstant(t: Double): V = weight0
}

val ConstantBezierBinomial<Vector>.point0: Point
    get() = this.weight0.toPoint()

val ConstantBezierBinomial<Vector>.segmentsConstant: List<Segment>
    get() = listOf(
        Segment(
            start = point0,
            end = point0,
        ),
    )

fun ConstantBezierBinomial<Double>.toPolynomialFormulaConstant(): ConstantPolynomial = ConstantPolynomial.of(
    a = weight0,
)

val ConstantBezierBinomial<Vector>.componentXLinear
    get() = ConstantBezierBinomial(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.x,
    )

val ConstantBezierBinomial<Vector>.componentYLinear
    get() = ConstantBezierBinomial(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.y,
    )
