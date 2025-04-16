package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance

@Suppress("DataClassPrivateConstructor")
data class LinearPolynomial private constructor(
    val a: Double,
    val b: Double,
) : Polynomial() {
    companion object {
        fun of(
            a: Double,
            b: Double,
        ): Polynomial = when {
            a == 0.0 -> ConstantPolynomial.of(a = b)
            else -> LinearPolynomial(a = a, b = b)
        }
    }

    init {
        require(a != 0.0)
    }

    override fun apply(x: Double): Double = a * x + b

    override fun equalsWithTolerance(
        other: NumericObject, absoluteTolerance: Double
    ): Boolean = when {
        other !is LinearPolynomial -> false
        !a.equalsWithTolerance(other.a, absoluteTolerance = absoluteTolerance) -> false
        !b.equalsWithTolerance(other.b, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }

    override fun plus(
        constant: Double,
    ): LinearPolynomial = copy(
        b = b + constant,
    )

    override fun plus(
        other: Polynomial,
    ): Polynomial = other.plusLinear(this)

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial = LinearPolynomial.of(
        a = a + linearPolynomial.a,
        b = b + linearPolynomial.b,
    )

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): QuadraticPolynomial = quadraticPolynomial.plusLinear(this)

    override fun plusCubic(cubicPolynomial: CubicPolynomial): Polynomial {
        TODO("Not yet implemented")
    }

    override fun times(
        factor: Double,
    ): Polynomial = LinearPolynomial.of(
        a = a * factor,
        b = b * factor,
    )

    override fun findRoots(): Set<Double> = setOf(findRoot())

    override operator fun unaryMinus(): LinearPolynomial = copy(
        a = -a,
        b = -b,
    )

    fun findRoot(): Double = -b / a
}
