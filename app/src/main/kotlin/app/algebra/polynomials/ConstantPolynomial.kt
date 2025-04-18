package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsWithTolerance

@Suppress("DataClassPrivateConstructor")
data class ConstantPolynomial private constructor(
    val a: Double,
) : Polynomial() {
    companion object {
        fun of(
            a: Double,
        ): ConstantPolynomial = ConstantPolynomial(a = a)
    }

    val a0: Double
        get() = a

    override fun apply(x: Double): Double = a

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is ConstantPolynomial -> false
        !a.equalsWithTolerance(other.a, tolerance = tolerance) -> false
        else -> true
    }

    override fun plus(
        constant: Double,
    ): Polynomial = ConstantPolynomial(a + constant)

    override fun plus(
        other: Polynomial,
    ): Polynomial = other + a

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial = linearPolynomial + a

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial = quadraticPolynomial + a

    override fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = cubicPolynomial + a

    override fun plusHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = highPolynomial + a

    override fun times(other: Polynomial): Polynomial = other * a

    override fun times(
        factor: Double,
    ): Polynomial = ConstantPolynomial.of(a = a * factor)

    override fun timesLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial = linearPolynomial * a

    override fun timesQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial = quadraticPolynomial * a

    override fun timesCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = cubicPolynomial * a

    override fun timesHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = highPolynomial * a

    override fun findRoots(): Set<Double> = emptySet()

    override operator fun unaryMinus(): ConstantPolynomial = copy(
        a = -a,
    )
}
