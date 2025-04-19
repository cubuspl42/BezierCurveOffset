package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsWithTolerance
import app.algebra.linear.matrices.matrix3.Matrix3x3
import app.algebra.linear.matrices.matrix3.RowMajorMatrix3x3
import app.algebra.linear.vectors.vector3.Vector1x3
import app.algebra.linear.vectors.vector4.Vector4Irr
import app.algebra.linear.vectors.vector4.conv
import app.algebra.linear.vectors.vector4.lower
import app.algebra.linear.vectors.vector4.plus
import app.algebra.linear.vectors.vector4.plusFirst
import app.algebra.linear.vectors.vector4.times
import app.algebra.linear.vectors.vector4.unaryMinus
import kotlin.math.acos
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

interface CubicPolynomial : Polynomial {
    companion object {
        fun of(
            coefficients: Vector4Irr,
        ): CubicPolynomial = when {
            coefficients.a3 == 0.0 -> QuadraticPolynomial.of(coefficients.lower)
            else -> ProperCubicPolynomial(coefficients = coefficients)
        }

        fun of(
            d: Double,
            c: Double,
            b: Double,
            a: Double,
        ): CubicPolynomial = of(
            coefficients = Vector4Irr(
                a0 = d,
                a1 = c,
                a2 = b,
                a3 = a,
            ),
        )

        internal fun resultantMatrix(
            pa: CubicPolynomial,
            pb: CubicPolynomial,
        ): RowMajorMatrix3x3 {
            val a0 = pa.a0
            val a1 = pa.a1
            val a2 = pa.a2
            val a3 = pa.a3

            val b0 = pb.a0
            val b1 = pb.a1
            val b2 = pb.a2
            val b3 = pb.a3

            val a1b0 = a1 * b0 - a0 * b1
            val a2b0 = a2 * b0 - a0 * b2
            val a2b1 = a2 * b1 - a1 * b2
            val a3b0 = a3 * b0 - a0 * b3
            val a3b1 = a3 * b1 - a1 * b3
            val a3b2 = a3 * b2 - a2 * b3

            val matrix = Matrix3x3.rowMajor(
                row0 = Vector1x3(a3b2, a3b1, a3b0),
                row1 = Vector1x3(a3b1, a3b0 + a2b1, a2b0),
                row2 = Vector1x3(a3b0, a2b0, a1b0),
            )

            return matrix
        }

        fun resultant(
            pa: CubicPolynomial,
            pb: CubicPolynomial,
        ): Double {
            val matrix = resultantMatrix(
                pa = pa,
                pb = pb,
            )

            val determinant = matrix.determinant

            return determinant
        }
    }

    val coefficientsCubic: Vector4Irr

    val a3: Double
        get() = coefficientsCubic.a3

    val a2: Double
        get() = coefficientsCubic.a2

    val a1: Double
        get() = coefficientsCubic.a1

    val a0: Double
        get() = coefficientsCubic.a0

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): CubicPolynomial
}

data class ProperCubicPolynomial internal constructor(
    val coefficients: Vector4Irr,
) : CubicPolynomial {

    override val coefficientsCubic: Vector4Irr
        get() = coefficients

    init {
        require(coefficients.a3 != 0.0)
    }

    override operator fun plus(
        constant: Double,
    ): ProperCubicPolynomial = ProperCubicPolynomial(
        coefficients = coefficients.plusFirst(constant),
    )

    override fun plus(
        other: Polynomial,
    ): Polynomial = other.plusCubic(this)

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): ProperCubicPolynomial = ProperCubicPolynomial(
        coefficients = coefficients + linearPolynomial.coefficientsLinear,
    )

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): ProperCubicPolynomial = ProperCubicPolynomial(
        coefficients = coefficients + quadraticPolynomial.coefficientsQuadratic,
    )

    override fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = CubicPolynomial.of(
        coefficients = coefficients + cubicPolynomial.coefficientsCubic,
    )

    override fun plusHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = highPolynomial.plusCubic(this)

    override fun times(
        other: Polynomial,
    ): Polynomial = other.timesCubic(this)

    override fun unaryMinus(): ProperCubicPolynomial = ProperCubicPolynomial(
        coefficients = -coefficients,
    )

    override fun times(
        factor: Double,
    ): CubicPolynomial = CubicPolynomial.of(
        coefficients = factor * coefficients,
    )

    override fun timesLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(linearPolynomial.coefficientsLinear),
    )

    override fun timesQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(quadraticPolynomial.coefficientsQuadratic),
    )

    override fun timesCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(cubicPolynomial.coefficientsCubic),
    )

    override fun timesHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(highPolynomial.coefficients),
    )

    override fun apply(x: Double): Double = a3 * x * x * x + a2 * x * x + a1 * x + a0

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is ProperCubicPolynomial -> false
        !a3.equalsWithTolerance(other.a3, tolerance = tolerance) -> false
        !a2.equalsWithTolerance(other.a2, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        else -> true
    }

    override fun findRoots(): Set<Double> {
        val a = a3
        val b = a2
        val c = a1
        val d = a0

        val f = (3.0 * a * c - b * b) / (3.0 * a * a)
        val g = (2.0 * b * b * b - 9.0 * a * b * c + 27.0 * a * a * d) / (27.0 * a * a * a)
        val h = g * g / 4.0 + f * f * f / 27.0

        return when {
            h > 0 -> {
                // One real root

                val r = -g / 2.0
                val s = sqrt(h)
                val u = cbrt(r + s)
                val v = cbrt(r - s)

                val x0 = u + v - (b / (3.0 * a))

                setOf(x0)
            }

            h == 0.0 -> {
                // All roots real, at least two equal

                val u = cbrt(-g / 2.0)

                val x0 = 2.0 * u - (b / (3.0 * a))
                val x1 = -u - (b / (3.0 * a))

                setOf(x0, x1)
            }

            else -> {
                // Three distinct real roots

                val i = sqrt(g * g / 4.0 - h)
                val j = cbrt(i)
                val k = acos(-g / (2.0 * i))
                val m = cos(k / 3.0)
                val n = sqrt(3.0) * sin(k / 3.0)
                val p = -b / (3.0 * a)

                val x0 = 2.0 * j * m + p
                val x1 = j * (-m + n) + p
                val x2 = j * (-m - n) + p

                setOf(x0, x1, x2)
            }
        }
    }
}
