package app.arithmetic.bezier_formulas

import app.arithmetic.polynomial_formulas.PolynomialFormula
import app.Vector
import app.VectorSpace

data class CubicBezierFormula<V>(
    private val vectorSpace: VectorSpace<V>,
    val weight0: V,
    val weight1: V,
    val weight2: V,
    val weight3: V,
) : BezierFormula<V>() {
    override fun findDerivative(): QuadraticBezierFormula<V> {
        fun scale3(v: V) = vectorSpace.scale(3.0, v)

        return QuadraticBezierFormula(
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

fun CubicBezierFormula<Double>.toPolynomialFormulaCubic(): PolynomialFormula = throw NotImplementedError()

val CubicBezierFormula<Vector>.componentXCubic
    get() = CubicBezierFormula(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.x,
        weight1 = weight1.x,
        weight2 = weight2.x,
        weight3 = weight3.x,
    )

val CubicBezierFormula<Vector>.componentYCubic
    get() = CubicBezierFormula(
        vectorSpace = VectorSpace.DoubleVectorSpace,
        weight0 = weight0.y,
        weight1 = weight1.y,
        weight2 = weight2.y,
        weight3 = weight3.y,
    )
