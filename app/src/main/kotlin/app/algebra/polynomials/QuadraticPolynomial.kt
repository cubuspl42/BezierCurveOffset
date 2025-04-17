package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.vectors.vector3.Vector3Irr
import app.algebra.linear.vectors.vector3.conv
import app.algebra.linear.vectors.vector3.lower
import app.algebra.linear.vectors.vector3.plus
import app.algebra.linear.vectors.vector3.plusFirst
import app.algebra.linear.vectors.vector3.unaryMinus
import kotlin.math.sqrt

@Suppress("DataClassPrivateConstructor")
data class QuadraticPolynomial private constructor(
    val coefficients: Vector3Irr,
) : Polynomial() {
    companion object {
        fun of(
            coefficients: Vector3Irr,
        ): Polynomial = when {
            coefficients.a2 == 0.0 -> LinearPolynomial.of(coefficients = coefficients.lower)
            else -> QuadraticPolynomial(coefficients = coefficients)
        }

        fun of(
            a: Double,
            b: Double,
            c: Double,
        ): Polynomial = of(
            coefficients = Vector3Irr(
                a0 = c,
                a1 = b,
                a2 = a,
            ),
        )
    }

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
    ): Polynomial = QuadraticPolynomial(
        coefficients = coefficients.plusFirst(constant),
    )

    override fun plus(
        other: Polynomial,
    ): Polynomial = other.plusQuadratic(this)

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): QuadraticPolynomial = QuadraticPolynomial(
        coefficients = coefficients + linearPolynomial.coefficients,
    )

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial = QuadraticPolynomial.of(
        coefficients = coefficients + quadraticPolynomial.coefficients,
    )

    override fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): CubicPolynomial = cubicPolynomial.plusQuadratic(this)

    override fun plusHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = highPolynomial.plusQuadratic(this)

    override fun times(
        factor: Double,
    ): Polynomial = QuadraticPolynomial.of(
        a = a * factor,
        b = b * factor,
        c = c * factor,
    )

    fun timesLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial = CubicPolynomial.of(
        coefficients = coefficients.conv(linearPolynomial.coefficients),
    )

    override operator fun unaryMinus(): QuadraticPolynomial = QuadraticPolynomial(
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
