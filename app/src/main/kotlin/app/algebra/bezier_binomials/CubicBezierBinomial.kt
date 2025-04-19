package app.algebra.bezier_binomials

import app.algebra.linear.matrices.matrix4.Matrix4x4
import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.polynomials.ParametricPolynomial
import app.geometry.ImplicitCubicPolynomial
import app.geometry.ImplicitLinearPolynomial
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

    /**
     * Solve the intersection of two cubic Bézier curves.
     *
     * @return A set of intersection parameter values t for the [other] curve.
     */
    fun solveIntersections(
        other: CubicBezierBinomial,
    ): Set<Double> {
        val thisImplicit = implicitize()
        val otherParametric = other.toParametricPolynomial()
        val intersectionPolynomial = thisImplicit.put(otherParametric)
        return intersectionPolynomial.findRoots()
    }

    fun implicitize(): ImplicitCubicPolynomial {
        val x0 = weight0.x
        val y0 = weight0.y
        val x1 = weight1.x
        val y1 = weight1.y
        val x2 = weight2.x
        val y2 = weight2.y
        val x3 = weight3.x
        val y3 = weight3.y

        val l32 = ImplicitLinearPolynomial(
            a1 = 3 * y3 - 3 * y2, b1 = 3 * x2 - 3 * x3, c = 3 * x3 * y2 - 3 * x2 * y3
        )

        val l31 = ImplicitLinearPolynomial(
            a1 = 3 * y3 - 3 * y1, b1 = 3 * x1 - 3 * x3, c = 3 * x3 * y1 - 3 * x1 * y3
        )

        val l30 = ImplicitLinearPolynomial(
            a1 = y3 - y0, b1 = x0 - x3, c = x3 * y0 - x0 * y3
        )

        val l21 = ImplicitLinearPolynomial(
            a1 = 9 * y2 - 9 * y1, b1 = 9 * x1 - 9 * x2, c = 9 * x2 * y1 - 9 * x1 * y2
        )

        val l20 = ImplicitLinearPolynomial(
            a1 = 3 * y2 - 3 * y0, b1 = 3 * x0 - 3 * x2, c = 3 * x2 * y0 - 3 * x0 * y2
        )

        val l10 = ImplicitLinearPolynomial(
            a1 = 3 * y1 - 3 * y0, b1 = 3 * x0 - 3 * x1, c = 3 * x1 * y0 - 3 * x0 * y1
        )

        return calculateDeterminant(
            a = l32, b = l31, c = l30, d = l31, e = l30 + l21, f = l20, g = l30, h = l20, i = l10
        )
    }
}

private fun calculateDeterminant(
    a: ImplicitLinearPolynomial, b: ImplicitLinearPolynomial, c: ImplicitLinearPolynomial,
    d: ImplicitLinearPolynomial, e: ImplicitLinearPolynomial, f: ImplicitLinearPolynomial,
    g: ImplicitLinearPolynomial, h: ImplicitLinearPolynomial, i: ImplicitLinearPolynomial,
): ImplicitCubicPolynomial = a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g)
