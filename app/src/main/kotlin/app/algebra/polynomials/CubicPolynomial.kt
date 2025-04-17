package app.algebra.polynomials

import app.algebra.NumericObject
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


@Suppress("DataClassPrivateConstructor")
data class CubicPolynomial private constructor(
    val coefficients: Vector4Irr,
) : Polynomial() {
    companion object {
        fun of(
            coefficients: Vector4Irr,
        ): Polynomial = when {
            coefficients.a3 == 0.0 -> QuadraticPolynomial.of(coefficients.lower)
            else -> CubicPolynomial(coefficients = coefficients)
        }

        fun of(
            d: Double,
            c: Double,
            b: Double,
            a: Double,
        ): Polynomial = of(
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

    val a: Double
        get() = coefficients.a3

    val b: Double
        get() = coefficients.a2

    val c: Double
        get() = coefficients.a1

    val d: Double
        get() = coefficients.a0

    val a3: Double
        get() = coefficients.a3

    val a2: Double
        get() = coefficients.a2

    val a1: Double
        get() = coefficients.a1

    val a0: Double
        get() = coefficients.a0


    init {
        require(coefficients.a3 != 0.0)
    }

    override operator fun plus(
        constant: Double,
    ): CubicPolynomial = CubicPolynomial(
        coefficients = coefficients.plusFirst(constant),
    )

    override fun plus(
        other: Polynomial,
    ): Polynomial = other.plusCubic(this)

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): CubicPolynomial = CubicPolynomial(
        coefficients = coefficients + linearPolynomial.coefficients,
    )

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): CubicPolynomial = CubicPolynomial(
        coefficients = coefficients + quadraticPolynomial.coefficients,
    )

    override fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = CubicPolynomial.of(
        coefficients = coefficients + cubicPolynomial.coefficients,
    )

    override fun plusHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = highPolynomial.plusCubic(this)

    override fun times(
        other: Polynomial,
    ): Polynomial = other.timesCubic(this)

    override fun unaryMinus(): CubicPolynomial = CubicPolynomial(
        coefficients = -coefficients,
    )

    override fun times(
        factor: Double,
    ): Polynomial = CubicPolynomial.of(
        coefficients = factor * coefficients,
    )

    override fun timesLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(linearPolynomial.coefficients),
    )

    override fun timesQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(quadraticPolynomial.coefficients),
    )

    override fun timesCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(cubicPolynomial.coefficients),
    )

    override fun timesHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(highPolynomial.coefficients),
    )

    override fun apply(x: Double): Double = a * x * x * x + b * x * x + c * x + d

    override fun equalsWithTolerance(
        other: NumericObject, absoluteTolerance: Double
    ): Boolean = when {
        other !is CubicPolynomial -> false
        !a.equalsWithTolerance(other.a, absoluteTolerance = absoluteTolerance) -> false
        !b.equalsWithTolerance(other.b, absoluteTolerance = absoluteTolerance) -> false
        !c.equalsWithTolerance(other.c, absoluteTolerance = absoluteTolerance) -> false
        !d.equalsWithTolerance(other.d, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }

    override fun findRoots(): Set<Double> {
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
