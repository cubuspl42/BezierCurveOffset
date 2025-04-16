package app.algebra.bezier_binomials

import app.algebra.linear.matrices.matrix4.Matrix4x4
import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.polynomials.ParametricPolynomial
import app.geometry.ParametricLineFunction
import app.geometry.RawVector
import app.geometry.times

data class CubicBezierBinomial(
    val weight0: RawVector,
    val weight1: RawVector,
    val weight2: RawVector,
    val weight3: RawVector,
) : BezierBinomial() {
    companion object {
        /**
         * The characteristic matrix of the cubic Bézier curve.
         */
        val characteristicMatrix = Matrix4x4.rowMajor(
            row0 = Vector4.horizontal(-1.0, 3.0, -3.0, 1.0),
            row1 = Vector4.horizontal(3.0, -6.0, 3.0, 0.0),
            row2 = Vector4.horizontal(-3.0, 3.0, 0.0, 0.0),
            row3 = Vector4.horizontal(1.0, 0.0, 0.0, 0.0),
        )

        val characteristicInvertedMatrix = characteristicMatrix.invert() ?: error("Matrix is not invertible")
    }

    /**
     * Solve B(t) = L(t') for t
     */
    fun solve(
        lineFunction: ParametricLineFunction,
    ): Set<Double> = lineFunction.toGeneralLineFunction().put(
        toParametricPolynomial(),
    ).findRoots()

    override fun findDerivative(): QuadraticBezierBinomial = QuadraticBezierBinomial(
        3.0 * (weight1 - weight0),
        3.0 * (weight2 - weight1),
        3.0 * (weight3 - weight2),
    )

    override fun toParametricPolynomial() = ParametricPolynomial.cubic(
        a = -weight0 + 3.0 * weight1 - 3.0 * weight2 + weight3,
        b = 3.0 * weight0 - 6.0 * weight1 + 3.0 * weight2,
        c = -3.0 * weight0 + 3.0 * weight1,
        d = weight0,
    )

    override fun apply(x: Double): RawVector {
        val u = 1.0 - x
        val c1 = u * u * u * weight0
        val c2 = 3.0 * u * u * x * weight1
        val c3 = 3.0 * u * x * x * weight2
        val c4 = x * x * x * weight3
        return c1 + c2 + c3 + c4
    }
}



