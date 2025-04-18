package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector2.Vector2Irr
import app.algebra.linear.vectors.vector2.conv
import app.algebra.linear.vectors.vector2.plus
import app.algebra.linear.vectors.vector2.unaryMinus

/**
 * A polynomial in the form a1 * x + a0
 */
@Suppress("DataClassPrivateConstructor")
data class LinearPolynomial private constructor(
    val coefficients: Vector2Irr,
) : Polynomial() {
    companion object {
        fun of(
            coefficients: Vector2Irr,
        ): Polynomial = when {
            coefficients.a1 == 0.0 -> ConstantPolynomial.of(
                a = coefficients.a0,
            )

            else -> LinearPolynomial(
                coefficients = coefficients,
            )
        }

        fun of(
            b: Double,
            a: Double,
        ): Polynomial = of(
            coefficients = Vector2(
                a0 = b,
                a1 = a,
            ),
        )

        fun of2(
            a0: Double,
            a1: Double,
        ): Polynomial = of(
            coefficients = Vector2(
                a0 = a0,
                a1 = a1,
            ),
        )
    }

    init {
        require(a != 0.0)
    }

    val a: Double
        get() = coefficients.a1

    val b: Double
        get() = coefficients.a0


    val a1: Double
        get() = coefficients.a1

    val a0: Double
        get() = coefficients.a0

    override fun apply(x: Double): Double = a * x + b

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: Double
    ): Boolean = when {
        other !is LinearPolynomial -> false
        !a.equalsWithTolerance(other.a, tolerance = tolerance) -> false
        !b.equalsWithTolerance(other.b, tolerance = tolerance) -> false
        else -> true
    }

    override fun plus(
        constant: Double,
    ): LinearPolynomial = LinearPolynomial(
        coefficients + Vector2Irr(a0 = constant, a1 = 0.0),
    )

    override fun plus(
        other: Polynomial,
    ): Polynomial = other.plusLinear(this)

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial = LinearPolynomial.of(
        coefficients = coefficients + linearPolynomial.coefficients,
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
        a = a * factor,
        b = b * factor,
    ) as LinearPolynomial // FIXME

    override fun timesLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial = QuadraticPolynomial.of(
        coefficients = coefficients.conv(linearPolynomial.coefficients),
    )

    override fun timesQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial = quadraticPolynomial.timesLinear(this)

    override fun timesCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = cubicPolynomial.timesLinear(this)

    override fun timesHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = highPolynomial.timesLinear(this)

    override fun findRoots(): Set<Double> = setOf(findRoot())

    override operator fun unaryMinus(): LinearPolynomial = LinearPolynomial(
        coefficients = -coefficients,
    )

    fun findRoot(): Double = -b / a
}

operator fun Double.times(
    polynomial: LinearPolynomial,
): LinearPolynomial = polynomial * this
