package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.bezier_binomials.RealFunction
import app.algebra.linear.vectors.vectorN.VectorN
import app.algebra.linear.vectors.vectorN.VectorNIrr
import app.geometry.Constants
import app.geometry.splines.ClosedSpline
import app.utils.iterable.uncons
import app.utils.iterable.untrail

sealed interface Polynomial : RealFunction<Double>, NumericObject {
    companion object {
        fun of(
            vararg coefficients: Double,
        ): Polynomial = of(
            coefficients = VectorNIrr(
                elements = coefficients.toList(),
            ),
        )

        fun of(
            coefficients: List<Double>,
        ): Polynomial = of(
            coefficients = VectorN.ofIrr(
                elements = coefficients,
            ),
        )

        fun of(
            coefficients: VectorNIrr,
        ): Polynomial {
            val a = coefficients.elements
            val n = coefficients.size - 1

            if (n <= 3) {
                return CubicPolynomial.of(
                    a = a.getOrNull(3) ?: 0.0,
                    b = a.getOrNull(2) ?: 0.0,
                    c = a.getOrNull(1) ?: 0.0,
                    d = a.getOrNull(0) ?: 0.0,
                )
            }

            val an = a.last()

            return when {
                an == 0.0 -> of(
                    coefficients = coefficients.lower,
                )

                else -> HighPolynomial(
                    coefficients = coefficients,
                )
            }
        }
    }

    val coefficientsN: VectorNIrr

    fun solveFor(
        y: Double,
    ): List<Double> = (this - y).findRoots()

    /**
     * Divides the polynomial by a linear polynomial of the form (x - x0).
     *
     * @return Pair of quotient and remainder.
     */
    fun divide(
        x0: Double,
    ): Pair<Polynomial, Double> {
        val (highestDegreeCoefficient, lowerDegreeCoefficients) = coefficientsN.elements.reversed().uncons()!!

        val intermediateCoefficients = lowerDegreeCoefficients.scan(
            initial = highestDegreeCoefficient,
        ) { higherDegreeCoefficient, coefficient ->
            higherDegreeCoefficient * x0 + coefficient
        }

        val (quotientCoefficients, remainder) = intermediateCoefficients.untrail()!!

        val quotient = Polynomial.of(
            coefficients = quotientCoefficients.reversed(),
        )

        return Pair(
            quotient,
            remainder,
        )
    }

    abstract operator fun plus(
        constant: Double,
    ): Polynomial

    operator fun minus(
        constant: Double,
    ): Polynomial = this + (-constant)

    abstract fun plusLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial

    abstract fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial

    abstract fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial

    abstract fun plusHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial

    abstract operator fun plus(
        other: Polynomial,
    ): Polynomial

    operator fun minus(
        other: Polynomial,
    ): Polynomial = other + (-other)

    abstract operator fun times(
        other: Polynomial,
    ): Polynomial

    abstract fun timesLinear(
        linearPolynomial: LinearPolynomial
    ): Polynomial

    abstract fun timesQuadratic(
        quadraticPolynomial: QuadraticPolynomial
    ): Polynomial

    abstract fun timesCubic(
        cubicPolynomial: CubicPolynomial
    ): Polynomial

    abstract fun timesHigh(
        highPolynomial: HighPolynomial
    ): Polynomial

    abstract operator fun unaryMinus(): Polynomial

    abstract operator fun times(factor: Double): Polynomial

    abstract fun findRoots(
        maxDepth: Int = 1000,
        tolerance: Tolerance = Tolerance.Absolute(absoluteTolerance = Constants.epsilon),
    ): List<Double>

    val derivative: Polynomial
}

operator fun Double.times(
    polynomial: Polynomial,
): Polynomial = polynomial * this
