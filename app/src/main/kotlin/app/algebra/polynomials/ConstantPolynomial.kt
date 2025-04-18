package app.algebra.polynomials

import app.algebra.NumericObject
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

    override fun apply(x: Double): Double = a

    override fun equalsWithTolerance(
        other: NumericObject, absoluteTolerance: Double
    ): Boolean = when {
        other !is ConstantPolynomial -> false
        !a.equalsWithTolerance(other.a, absoluteTolerance = absoluteTolerance) -> false
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

    override fun times(
        factor: Double,
    ): Polynomial = ConstantPolynomial.of(a = a * factor)

    override fun findRoots(): Set<Double> = emptySet()

    override operator fun unaryMinus(): ConstantPolynomial = copy(
        a = -a,
    )
}
