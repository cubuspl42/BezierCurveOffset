package app.algebra.bezier_formulas

import app.algebra.Vector
import app.algebra.VectorSpace
import app.algebra.polynomial_formulas.ConstantFormula
import app.geometry.Point
import app.geometry.Segment

data class ConstantBezierFormula<V>(
    private val vectorSpace: VectorSpace<V>,
    val weight0: V,
) : BezierFormula<V>() {
    private val zero: ConstantBezierFormula<V>
        get() = ConstantBezierFormula(
            vectorSpace = vectorSpace,
            weight0 = vectorSpace.zero,
        )

    override fun findDerivative(): BezierFormula<V> = zero

    override fun evaluate(t: Double): V = evaluateConstant(t = t)

    fun evaluateConstant(t: Double): V = weight0
}

val ConstantBezierFormula<Vector>.point0: Point
    get() = this.weight0.toPoint()

val ConstantBezierFormula<Vector>.segmentsConstant: List<Segment>
    get() = listOf(
        Segment(
            start = point0,
            end = point0,
        ),
    )


fun ConstantBezierFormula<Double>.toPolynomialFormulaConstant(): ConstantFormula = ConstantFormula.of(
    a = weight0,
)

val ConstantBezierFormula<Vector>.componentXLinear
    get() = ConstantBezierFormula(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.x,
    )

val ConstantBezierFormula<Vector>.componentYLinear
    get() = ConstantBezierFormula(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.y,
    )
