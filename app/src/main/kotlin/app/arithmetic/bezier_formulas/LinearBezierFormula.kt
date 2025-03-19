package app.arithmetic.bezier_formulas

import app.arithmetic.polynomial_formulas.PolynomialFormula
import app.Vector
import app.VectorSpace
import app.arithmetic.polynomial_formulas.LinearFormula

data class LinearBezierFormula<V>(
    private val vectorSpace: VectorSpace<V>,
    val weight0: V,
    val weight1: V,
) : BezierFormula<V>() {
    override fun findDerivative(): BezierFormula<V> = throw NotImplementedError()

    override fun evaluate(t: Double): V {
        val u = 1.0 - t
        val c1 = vectorSpace.scale(u, weight0)
        val c2 = vectorSpace.scale(t, weight1)
        return vectorSpace.add(c1, c2)
    }
}

fun LinearBezierFormula<Double>.toPolynomialFormulaLinear(): PolynomialFormula = LinearFormula.of(
    a = weight1 - weight0,
    b = weight0,
)

val LinearBezierFormula<Vector>.componentXLinear
    get() = LinearBezierFormula(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.x,
        weight1 = weight1.x,
    )

val LinearBezierFormula<Vector>.componentYLinear
    get() = LinearBezierFormula(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.y,
        weight1 = weight1.y,
    )
