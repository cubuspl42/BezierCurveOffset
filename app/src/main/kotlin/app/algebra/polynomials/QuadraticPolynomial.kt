package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsWithTolerance
import app.algebra.linear.vectors.vector3.Vector3Irr
import app.algebra.linear.vectors.vector3.conv
import app.algebra.linear.vectors.vector3.lower
import app.algebra.linear.vectors.vector3.plus
import app.algebra.linear.vectors.vector3.plusFirst
import app.algebra.linear.vectors.vector3.unaryMinus
import app.algebra.linear.vectors.vector4.Vector4Irr
import kotlin.math.sqrt

interface QuadraticPolynomial : CubicPolynomial {
    companion object {
        fun of(
            coefficients: Vector3Irr,
        ): QuadraticPolynomial = when {
            coefficients.a2 == 0.0 -> LinearPolynomial.of(coefficients = coefficients.lower)
            else -> ProperQuadraticPolynomial(coefficients = coefficients)
        }

        fun of(
            c: Double,
            b: Double,
            a: Double,
        ): QuadraticPolynomial = of(
            coefficients = Vector3Irr(
                a0 = c,
                a1 = b,
                a2 = a,
            ),
        )
    }

    override val coefficientsCubic: Vector4Irr
        get() = Vector4Irr(
            a0 = coefficientsQuadratic.a0,
            a1 = coefficientsQuadratic.a1,
            a2 = coefficientsQuadratic.a2,
            a3 = 0.0,
        )

    val coefficientsQuadratic: Vector3Irr

    override operator fun plus(
        constant: Double,
    ): QuadraticPolynomial

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): QuadraticPolynomial
}

data class ProperQuadraticPolynomial internal constructor(
    val coefficients: Vector3Irr,
) : QuadraticPolynomial {


    override val coefficientsQuadratic: Vector3Irr
        get() = coefficients

    val a: Double
        get() = coefficients.a2

    val b: Double
        get() = coefficients.a1

    val c: Double
        get() = coefficients.a0

    init {
        require(a != 0.0)
    }

    override fun apply(x: Double): Double = a * x * x + b * x + c

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is ProperQuadraticPolynomial -> false
        !a.equalsWithTolerance(other.a, tolerance = tolerance) -> false
        !b.equalsWithTolerance(other.b, tolerance = tolerance) -> false
        !c.equalsWithTolerance(other.c, tolerance = tolerance) -> false
        else -> true
    }

    override fun plus(
        constant: Double,
    ): ProperQuadraticPolynomial = ProperQuadraticPolynomial(
        coefficients = coefficients.plusFirst(constant),
    )

    override fun plus(
        other: Polynomial,
    ): Polynomial = other.plusQuadratic(this)

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): QuadraticPolynomial = ProperQuadraticPolynomial(
        coefficients = coefficients + linearPolynomial.coefficientsLinear,
    )

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): QuadraticPolynomial = QuadraticPolynomial.of(
        coefficients = coefficients + quadraticPolynomial.coefficientsQuadratic,
    )

    override fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): CubicPolynomial = cubicPolynomial.plusQuadratic(this)

    override fun plusHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = highPolynomial.plusQuadratic(this)

    override fun times(
        other: Polynomial,
    ): Polynomial = other.timesQuadratic(this)

    override fun times(
        factor: Double,
    ): QuadraticPolynomial = QuadraticPolynomial.of(
        a = a * factor,
        b = b * factor,
        c = c * factor,
    )

    override fun timesLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial = CubicPolynomial.of(
        coefficients = coefficients.conv(linearPolynomial.coefficientsLinear),
    )

    override fun timesQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(quadraticPolynomial.coefficientsQuadratic),
    )

    override fun timesCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = cubicPolynomial.timesQuadratic(this)

    override fun timesHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = highPolynomial.timesQuadratic(this)

    override operator fun unaryMinus(): ProperQuadraticPolynomial = ProperQuadraticPolynomial(
        coefficients = -coefficients,
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
