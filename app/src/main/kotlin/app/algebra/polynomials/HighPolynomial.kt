package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.linear.vectors.vectorN.VectorNIrr
import app.algebra.linear.vectors.vectorN.plus
import app.algebra.linear.vectors.vectorN.times
import app.algebra.linear.vectors.vectorN.unaryMinus
import kotlin.math.pow

@Suppress("DataClassPrivateConstructor")
data class HighPolynomial private constructor(
    val coefficients: VectorNIrr,
) : Polynomial() {
    companion object {
        fun of(
            coefficients: VectorNIrr,
        ): Polynomial {
            val a = coefficients.xs
            val n = coefficients.size - 1
            require(n > 3)

            val an = a.last()

            return when {
                an == 0.0 -> when {
                    n == 4 -> CubicPolynomial.of(
                        a = a[3],
                        b = a[2],
                        c = a[1],
                        d = a[0],
                    )

                    else -> HighPolynomial.of(
                        coefficients = coefficients.lower,
                    )
                }

                else -> HighPolynomial(
                    coefficients = coefficients,
                )
            }
        }
    }

    init {
        require(coefficients.an != 0.0)
    }

    override operator fun plus(
        constant: Double,
    ): HighPolynomial = TODO()

    override fun plus(
        other: Polynomial,
    ): Polynomial = other.plusHigh(this)

    override fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): HighPolynomial = HighPolynomial(
        coefficients + linearPolynomial.coefficients,
    )

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): HighPolynomial = HighPolynomial(
        coefficients + quadraticPolynomial.coefficients,
    )

    override fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = HighPolynomial(
        coefficients + cubicPolynomial.coefficients,
    )

    override fun plusHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients + highPolynomial.coefficients,
    )

    override fun unaryMinus(): HighPolynomial = HighPolynomial(
        coefficients = -coefficients,
    )

    override fun times(
        factor: Double,
    ): Polynomial = HighPolynomial.of(
        coefficients = factor * coefficients,
    )

    override fun apply(
        x: Double,
    ): Double = coefficients.xs.withIndex().sumOf { (i, ai) ->
        ai * x.pow(i)
    }

    override fun equalsWithTolerance(
        other: NumericObject, absoluteTolerance: Double
    ): Boolean = when {
        other !is HighPolynomial -> false
        !coefficients.equalsWithTolerance(other.coefficients, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }

    override fun findRoots(): Set<Double> {
        TODO()
    }
}
