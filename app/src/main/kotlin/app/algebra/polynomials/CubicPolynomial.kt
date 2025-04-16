package app.algebra.polynomials

import kotlin.math.acos
import kotlin.math.cbrt
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Suppress("DataClassPrivateConstructor")
data class CubicPolynomial private constructor(
    val a: Double,
    val b: Double,
    val c: Double,
    val d: Double,
) : Polynomial() {
    companion object {
        fun of(
            a: Double,
            b: Double,
            c: Double,
            d: Double,
        ): Polynomial = when {
            a == 0.0 -> QuadraticPolynomial.of(a = b, b = c, c = d)
            else -> CubicPolynomial(a = a, b = b, c = c, d = d)
        }
    }

    init {
        require(a != 0.0)
    }

    override operator fun plus(
        constant: Double,
    ): CubicPolynomial = copy(
        d = d + constant,
    )

    override fun plus(
        other: Polynomial,
    ): Polynomial = other.plusCubic(this)

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): CubicPolynomial = copy(
        c = c + linearPolynomial.a,
        d = d + linearPolynomial.b,
    )

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): CubicPolynomial = copy(
        b = b + quadraticPolynomial.a,
        c = c + quadraticPolynomial.b,
        d = d + quadraticPolynomial.c,
    )

    override fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = CubicPolynomial.of(
        a = a + cubicPolynomial.a,
        b = b + cubicPolynomial.b,
        c = c + cubicPolynomial.c,
        d = d + cubicPolynomial.d,
    )

    override fun unaryMinus(): CubicPolynomial = CubicPolynomial(
        a = -a,
        b = -b,
        c = -c,
        d = -d,
    )

    override fun times(
        factor: Double,
    ): Polynomial = CubicPolynomial.of(
        a = a * factor,
        b = b * factor,
        c = c * factor,
        d = d * factor,
    )

    override fun apply(x: Double): Double = a * x * x * x + b * x * x + c * x + d

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
