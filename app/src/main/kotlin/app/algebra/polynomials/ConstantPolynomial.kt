package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsWithTolerance
import app.algebra.linear.VectorOrientation
import app.algebra.linear.vectors.vector2.Vector2

@Suppress("DataClassPrivateConstructor")
data class ConstantPolynomial private constructor(
    val a: Double,
) : LinearPolynomial {
    companion object {
        val zero = ConstantPolynomial(a = 0.0)

        fun of(
            a: Double,
        ): ConstantPolynomial = ConstantPolynomial(a = a)
    }

    override val coefficientsLinear: Vector2<VectorOrientation.Irrelevant>
        get() = Vector2(a0 = a, a1 = 0.0)

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
    ): ConstantPolynomial = ConstantPolynomial(a + constant)

    override fun plus(
        other: Polynomial,
    ): Polynomial = other + a

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): LinearPolynomial = linearPolynomial + a

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): QuadraticPolynomial = quadraticPolynomial + a

    override fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = cubicPolynomial + a

    override fun plusHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = highPolynomial + a

    override fun times(other: Polynomial): Polynomial = other * a

    override fun times(
        factor: Double,
    ): ConstantPolynomial = ConstantPolynomial.of(a = a * factor)

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

    override fun findRoots(
        maxDepth: Int,
        tolerance: Tolerance,
    ): List<Double> = emptyList()

    override val derivative: ConstantPolynomial
        get() = ConstantPolynomial.zero

    override fun divide(
        x0: Double,
    ): Pair<Polynomial, Double> = Pair(
        ConstantPolynomial.zero,
        a,
    )

    override operator fun unaryMinus(): ConstantPolynomial = copy(
        a = -a,
    )
}
