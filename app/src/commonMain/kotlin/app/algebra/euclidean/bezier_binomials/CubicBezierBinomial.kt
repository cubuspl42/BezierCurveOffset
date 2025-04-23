package app.algebra.euclidean.bezier_binomials

import app.algebra.NumericObject
import app.algebra.implicit_polynomials.ImplicitCubicPolynomial
import app.algebra.implicit_polynomials.ImplicitLinearPolynomial
import app.algebra.linear.matrices.matrix4.Matrix4x4
import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.polynomials.ParametricPolynomial
import app.algebra.implicit_polynomials.RationalImplicitPolynomial
import app.algebra.implicit_polynomials.times
import app.algebra.linear.matrices.matrix3.Matrix3x3
import app.algebra.polynomials.Polynomial
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

    val x0: Double
        get() = weight0.x

    val y0: Double
        get() = weight0.y

    val x1: Double
        get() = weight1.x

    val y1: Double
        get() = weight1.y

    val x2: Double
        get() = weight2.x

    val y2: Double
        get() = weight2.y

    val x3: Double
        get() = weight3.x

    val y3: Double
        get() = weight3.y

    val l32: ImplicitLinearPolynomial
        get() = ImplicitLinearPolynomial(
            a1 = 3 * y3 - 3 * y2,
            b1 = 3 * x2 - 3 * x3,
            c = 3 * x3 * y2 - 3 * x2 * y3,
        )

    val l31: ImplicitLinearPolynomial
        get() = ImplicitLinearPolynomial(
            a1 = 3 * y3 - 3 * y1,
            b1 = 3 * x1 - 3 * x3,
            c = 3 * x3 * y1 - 3 * x1 * y3,
        )

    val l30: ImplicitLinearPolynomial
        get() = ImplicitLinearPolynomial(
            a1 = y3 - y0,
            b1 = x0 - x3,
            c = x3 * y0 - x0 * y3,
        )

    val l21: ImplicitLinearPolynomial
        get() = ImplicitLinearPolynomial(
            a1 = 9 * y2 - 9 * y1,
            b1 = 9 * x1 - 9 * x2,
            c = 9 * x2 * y1 - 9 * x1 * y2,
        )

    val l20: ImplicitLinearPolynomial
        get() = ImplicitLinearPolynomial(
            a1 = 3 * y2 - 3 * y0,
            b1 = 3 * x0 - 3 * x2,
            c = 3 * x2 * y0 - 3 * x0 * y2,
        )

    val l10: ImplicitLinearPolynomial
        get() = ImplicitLinearPolynomial(
            a1 = 3 * y1 - 3 * y0,
            b1 = 3 * x0 - 3 * x1,
            c = 3 * x1 * y0 - 3 * x0 * y1,
        )

    override fun findDerivative(): QuadraticBezierBinomial = QuadraticBezierBinomial(
        3.0 * (weight1 - weight0),
        3.0 * (weight2 - weight1),
        3.0 * (weight3 - weight2),
    )

    fun findPointProjectionPolynomial(
        g: RawVector,
    ): Polynomial<*> {
        val p0 = weight0 - g
        val p1 = weight1 - g
        val p2 = weight2 - g
        val p3 = weight3 - g

        val a = p3 - 3.0 * p2 + 3.0 * p1 - p0
        val b = 3.0 * p2 - 6.0 * p1 + 3.0 * p0
        val c = 3.0 * (p1 - p0)
        val d = p0

        return Polynomial.of(
            c.dot(d),
            c.dot(c) + 2.0 * b.dot(d),
            3.0 * b.dot(c) + 3.0 * a.dot(d),
            4.0 * a.dot(c) + 2.0 * b.dot(b),
            5.0 * a.dot(b),
            3.0 * a.dot(a),
        )
    }

    override fun toParametricPolynomial() = ParametricPolynomial.cubic(
        a3 = -weight0 + 3.0 * weight1 - 3.0 * weight2 + weight3,
        a2 = 3.0 * weight0 - 6.0 * weight1 + 3.0 * weight2,
        a1 = -3.0 * weight0 + 3.0 * weight1,
        a0 = weight0,
    )

    override fun apply(x: Double): RawVector {
        val u = 1.0 - x
        val c1 = u * u * u * weight0
        val c2 = 3.0 * u * u * x * weight1
        val c3 = 3.0 * u * x * x * weight2
        val c4 = x * x * x * weight3
        return c1 + c2 + c3 + c4
    }

    override fun solvePoint(
        p: RawVector, tolerance: NumericObject.Tolerance
    ): Double? {
        TODO()
    }

    fun invert(): RationalImplicitPolynomial? {
        val d = 3.0 * Matrix3x3.rowMajor(
            row0 = weight1.horizontal.toVec3(),
            row1 = weight2.horizontal.toVec3(),
            row2 = weight3.horizontal.toVec3(),
        ).determinant

        if (d == 0.0) {
            return null
        }

        val n1 = Matrix3x3.rowMajor(
            row0 = weight0.horizontal.toVec3(),
            row1 = weight1.horizontal.toVec3(),
            row2 = weight3.horizontal.toVec3(),
        ).determinant

        val n2 = Matrix3x3.rowMajor(
            row0 = weight0.horizontal.toVec3(),
            row1 = weight2.horizontal.toVec3(),
            row2 = weight3.horizontal.toVec3(),
        ).determinant

        val c1 = n1 / d
        val c2 = -(n2 / d)

        val l10 = this.l10
        val l20 = this.l20
        val l21 = this.l21
        val l30 = this.l30
        val l31 = this.l31

        val la = c1 * l31 + c2 * (l30 + l21) + l20
        val lb = c1 * l30 + c2 * l20 + l10

        return RationalImplicitPolynomial(
            nominatorFunction = lb,
            denominatorFunction = lb - la,
        )
    }

    override fun implicitize(): ImplicitCubicPolynomial {
        val l32 = this.l32
        val l31 = this.l31
        val l30 = this.l30
        val l21 = this.l21
        val l20 = this.l20
        val l10 = this.l10

        return calculateDeterminant(
            a = l32,
            b = l31,
            c = l30,
            d = l31,
            e = l30 + l21,
            f = l20,
            g = l30,
            h = l20,
            i = l10,
        )
    }
}

/**
 * Calculate the determinant of a 3x3 polynomial matrix in the form:
 * | a b c |
 * | d e f |
 * | g h i |
 *
 * @return The determinant of the described matrix.
 */
private fun calculateDeterminant(
    a: ImplicitLinearPolynomial, b: ImplicitLinearPolynomial, c: ImplicitLinearPolynomial,
    d: ImplicitLinearPolynomial, e: ImplicitLinearPolynomial, f: ImplicitLinearPolynomial,
    g: ImplicitLinearPolynomial, h: ImplicitLinearPolynomial, i: ImplicitLinearPolynomial,
): ImplicitCubicPolynomial = a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g)
