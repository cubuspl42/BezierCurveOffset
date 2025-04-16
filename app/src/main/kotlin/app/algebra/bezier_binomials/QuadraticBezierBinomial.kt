package app.algebra.bezier_binomials

import app.algebra.polynomials.ParametricPolynomial
import app.geometry.ParametricLineFunction
import app.geometry.RawVector
import app.geometry.curves.LineSegment
import app.geometry.times

class QuadraticBezierBinomial(
    val weight0: RawVector,
    val weight1: RawVector,
    val weight2: RawVector,
) : BezierBinomial() {
    override fun toParametricPolynomial() = ParametricPolynomial.quadratic(
        a = weight0 - 2.0 * weight1 + weight2,
        b = 2.0 * (weight1 - weight0),
        c = weight0,
    )

    override fun findDerivative(): ParametricCurveFunction = ParametricLineFunction(
        s = weight0,
        d = 2.0 * (weight1 - weight0),
    )

    override fun apply(x: Double): RawVector {
        val u = 1.0 - x
        val c1 = u * u * weight0
        val c2 = 2.0 * u * x * weight1
        val c3 = x * x * weight2
        return c1 + c2 + c3
    }
}
