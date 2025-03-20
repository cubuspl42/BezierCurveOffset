package app.algebra.bezier_formulas

import app.algebra.polynomial_formulas.PolynomialFormula
import app.algebra.polynomial_formulas.QuadraticFormula
import app.algebra.Vector
import app.VectorSpace

class QuadraticBezierFormula<V>(
    private val vectorSpace: VectorSpace<V>,
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
