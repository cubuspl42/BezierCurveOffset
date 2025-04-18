package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.linear.vectors.vectorN.VectorNIrr
import app.algebra.linear.vectors.vectorN.conv
import app.algebra.linear.vectors.vectorN.plus
import app.algebra.linear.vectors.vectorN.times
import app.algebra.linear.vectors.vectorN.unaryMinus
import app.equalsZeroApproximately
import org.apache.commons.math3.analysis.solvers.LaguerreSolver
import kotlin.math.pow

@Suppress("DataClassPrivateConstructor")
data class HighPolynomial private constructor(
    val coefficients: VectorNIrr,
) : Polynomial() {
    companion object {
        fun of(
            vararg coefficients: Double,
        ): Polynomial = of(
            coefficients = VectorNIrr(
                elements = coefficients.toList(),
            ),
        )

        fun of(
            coefficients: VectorNIrr,
        ): Polynomial {
            val a = coefficients.elements
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
    ): HighPolynomial = HighPolynomial(
        coefficients = coefficients.plusFirst(constant),
    )

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

    override fun times(
        other: Polynomial,
    ): Polynomial = other.timesHigh(this)

    override fun unaryMinus(): HighPolynomial = HighPolynomial(
        coefficients = -coefficients,
    )

    override fun times(
        factor: Double,
    ): Polynomial = HighPolynomial.of(
        coefficients = factor * coefficients,
    )

    override fun timesLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(linearPolynomial.coefficients),
    )

    override fun timesQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(quadraticPolynomial.coefficients),
    )

    override fun timesCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(cubicPolynomial.coefficients),
    )

    override fun timesHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = HighPolynomial.of(
        coefficients = coefficients.conv(highPolynomial.coefficients),
    )

    override fun apply(
        x: Double,
    ): Double = coefficients.elements.withIndex().sumOf { (i, ai) ->
        ai * x.pow(i)
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is HighPolynomial -> false
        !coefficients.equalsWithTolerance(other.coefficients, tolerance = tolerance) -> false
        else -> true
    }

    override fun findRoots(): Set<Double> {
        val solver = LaguerreSolver()
        val coefficients = coefficients.elements

        val roots = solver.solveAllReal(
            coefficients = coefficients,
            initial = 0.5,
        )

        return roots.toSet()
    }
}

private fun LaguerreSolver.solveAllReal(
    coefficients: Collection<Double>,
    initial: Double,
): Collection<Double> {
    val complexRoots = solveAllComplex(
        coefficients.toDoubleArray(),
        initial,
    )

    return complexRoots.mapNotNull { complex ->
        complex.takeIf {
            it.imaginary.equalsZeroApproximately(epsilon = 10e-8)
        }?.real
    }
}
