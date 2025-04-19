package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsWithTolerance
import app.algebra.linear.VectorOrientation
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector2.Vector2Irr
import app.algebra.linear.vectors.vector2.conv
import app.algebra.linear.vectors.vector2.plus
import app.algebra.linear.vectors.vector2.times
import app.algebra.linear.vectors.vector2.unaryMinus
import app.algebra.linear.vectors.vector3.Vector3
import app.algebra.linear.vectors.vector3.Vector3Irr

interface LinearPolynomial : QuadraticPolynomial {
    companion object {
        fun of(
            coefficients: Vector2Irr,
        ): LinearPolynomial = when {
            coefficients.a1 == 0.0 -> ConstantPolynomial.of(
                a = coefficients.a0,
            )

            else -> ProperLinearPolynomial(
                coefficients = coefficients,
            )
        }

        fun of(
            a0: Double,
            a1: Double,
        ): LinearPolynomial = of(
            coefficients = Vector2(
                a0 = a0,
                a1 = a1,
            ),
        )
    }

    override val coefficientsQuadratic: Vector3Irr
        get() = Vector3(
            a0 = coefficientsLinear.a0,
            a1 = coefficientsLinear.a1,
            a2 = 0.0,
        )

    override fun timesCubic(
        cubicPolynomial: ProperCubicPolynomial,
    ): Polynomial = cubicPolynomial.timesLinear(this)

    override operator fun plus(
        constant: Double,
    ): LinearPolynomial

    override fun times(
        factor: Double,
    ): LinearPolynomial


    val coefficientsLinear: Vector2<VectorOrientation.Irrelevant>
}

/**
 * A polynomial in the form a1 * x + a0
 */
data class ProperLinearPolynomial internal constructor(
    val coefficients: Vector2Irr,
) : LinearPolynomial {
    init {
        require(a1 != 0.0)
    }

    override val coefficientsLinear: Vector2Irr
        get() = coefficients

    override fun apply(x: Double): Double = a1 * x + a0

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is ProperLinearPolynomial -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        else -> true
    }

    override fun plus(
        constant: Double,
    ): ProperLinearPolynomial = ProperLinearPolynomial(
        coefficients + Vector2Irr(a0 = constant, a1 = 0.0),
    )

    override fun plus(
        other: Polynomial,
    ): Polynomial = other.plusLinear(this)

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): LinearPolynomial = LinearPolynomial.of(
        coefficients = coefficients + linearPolynomial.coefficientsLinear,
    )

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): QuadraticPolynomial = quadraticPolynomial.plusLinear(this)

    override fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = cubicPolynomial.plusLinear(this)

    override fun plusHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = highPolynomial.plusLinear(this)

    override fun times(
        other: Polynomial,
    ): Polynomial = other.timesLinear(this)

    override fun times(
        factor: Double,
    ): LinearPolynomial = LinearPolynomial.of(
        factor * coefficients,
    )

    override fun timesLinear(
        linearPolynomial: LinearPolynomial,
    ): QuadraticPolynomial = QuadraticPolynomial.of(
        coefficients = coefficients.conv(linearPolynomial.coefficientsLinear),
    )

    override fun timesQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial = quadraticPolynomial.timesLinear(this)

    override fun findRoots(
        maxDepth: Int,
        tolerance: Tolerance,
    ): List<Double> = listOf(findRoot())

    override val derivative: ConstantPolynomial
        get() = ConstantPolynomial.of(a1)

    override operator fun unaryMinus(): ProperLinearPolynomial = ProperLinearPolynomial(
        coefficients = -coefficients,
    )

    fun findRoot(): Double = -a0 / a1
}

operator fun Double.times(
    polynomial: LinearPolynomial,
): LinearPolynomial = this * polynomial
