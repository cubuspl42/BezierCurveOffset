package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import kotlin.math.sqrt

@Suppress("DataClassPrivateConstructor")
data class QuadraticPolynomial private constructor(
    val a: Double,
    val b: Double,
    val c: Double,
) : Polynomial() {
    companion object {
        fun of(
            a: Double,
            b: Double,
            c: Double,
        ): Polynomial = when {
            a == 0.0 -> LinearPolynomial.of(a = b, b = c)
            else -> QuadraticPolynomial(a = a, b = b, c = c)
        }
    }

    init {
        require(a != 0.0)
    }

    override fun apply(x: Double): Double = a * x * x + b * x + c

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is QuadraticPolynomial -> false
        !a.equalsWithTolerance(other.a, absoluteTolerance = absoluteTolerance) -> false
        !b.equalsWithTolerance(other.b, absoluteTolerance = absoluteTolerance) -> false
        !c.equalsWithTolerance(other.c, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }

    override fun plus(
        constant: Double,
    ): Polynomial = copy(
        c = c + constant,
    )

    override fun plus(
        other: Polynomial,
    ): Polynomial = other.plusQuadratic(this)

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): QuadraticPolynomial = copy(
        b = b + linearPolynomial.a,
        c = c + linearPolynomial.b,
    )

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial = QuadraticPolynomial.of(
        a = a + quadraticPolynomial.a,
        b = b + quadraticPolynomial.b,
        c = c + quadraticPolynomial.c,
    )

    override fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): CubicPolynomial = cubicPolynomial.plusQuadratic(this)

    override fun times(
        factor: Double,
    ): Polynomial = QuadraticPolynomial.of(
        a = a * factor,
        b = b * factor,
        c = c * factor,
    )

    override operator fun unaryMinus(): QuadraticPolynomial = copy(
        a = -a,
        b = -b,
        c = -c,
    )

    override fun findRoots(): Set<Double> {
        val discriminant: Double = b * b - 4 * a * c

        fun buildRoot(
            sign: Double,
        ): Double = (-b + sign * sqrt(discriminant)) / (2 * a)

        return when {
            discriminant >= 0 -> setOf(
                buildRoot(sign = -1.0),
                buildRoot(sign = 1.0),
            )

            else -> emptySet()
        }
    }
}
