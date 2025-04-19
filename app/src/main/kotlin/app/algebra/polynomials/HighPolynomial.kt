package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsZeroWithTolerance
import app.algebra.linear.vectors.vectorN.VectorNIrr
import app.algebra.linear.vectors.vectorN.conv
import app.algebra.linear.vectors.vectorN.plus
import app.algebra.linear.vectors.vectorN.times
import app.algebra.linear.vectors.vectorN.unaryMinus
import app.utils.div
import app.utils.equalsWithTolerance
import app.utils.iterable.uncons
import app.utils.iterable.untrail
import app.utils.plus
import app.utils.solveAllReal
import org.apache.commons.math3.analysis.solvers.LaguerreSolver
import org.apache.commons.math3.complex.Complex
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

data class HighPolynomial internal constructor(
    val coefficients: VectorNIrr,
) : Polynomial {
    private object FindRootsConstants {
        const val maxDepth = 1000
    }

    /**
     * Significance of the improvement of the root.
     */
    private enum class ImprovementSignificance {
        Acceptable, Insignificant;

        companion object {
            fun of(
                delta: Complex,
                tolerance: Tolerance,
            ): ImprovementSignificance = when {
                delta.equalsWithTolerance(Complex.ZERO, tolerance = tolerance) -> Insignificant
                else -> Acceptable
            }
        }

        fun combineWith(
            other: ImprovementSignificance
        ): ImprovementSignificance = when {
            this == Insignificant || other == Insignificant -> Insignificant
            else -> Acceptable
        }
    }

    init {
        require(coefficients.an != 0.0)
        require(degree >= 4)
    }

    val degree: Int
        get() = coefficients.size - 1

    override val derivative: Polynomial
        get() = Polynomial.of(
            coefficients = coefficients.elements.drop(1).mapIndexed { index, ai ->
                (index + 1) * ai
            },
        )

    override val coefficientsN: VectorNIrr
        get() = coefficients

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
        coefficients + linearPolynomial.coefficientsLinear,
    )

    override fun plusQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): HighPolynomial = HighPolynomial(
        coefficients + quadraticPolynomial.coefficientsQuadratic,
    )

    override fun plusCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = HighPolynomial(
        coefficients + cubicPolynomial.coefficientsCubic,
    )

    override fun plusHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = Polynomial.of(
        coefficients = coefficients + highPolynomial.coefficientsN,
    )

    override fun times(
        other: Polynomial,
    ): Polynomial = other.timesHigh(this)

    override fun unaryMinus(): HighPolynomial = HighPolynomial(
        coefficients = -coefficients,
    )

    override fun times(
        factor: Double,
    ): Polynomial = Polynomial.of(
        coefficients = factor * coefficients,
    )

    override fun timesLinear(
        linearPolynomial: LinearPolynomial,
    ): Polynomial = Polynomial.of(
        coefficients = coefficients.conv(linearPolynomial.coefficientsLinear),
    )

    override fun timesQuadratic(
        quadraticPolynomial: QuadraticPolynomial,
    ): Polynomial = Polynomial.of(
        coefficients = coefficients.conv(quadraticPolynomial.coefficientsQuadratic),
    )

    override fun timesCubic(
        cubicPolynomial: CubicPolynomial,
    ): Polynomial = Polynomial.of(
        coefficients = coefficients.conv(cubicPolynomial.coefficientsCubic),
    )

    override fun timesHigh(
        highPolynomial: HighPolynomial,
    ): Polynomial = Polynomial.of(
        coefficients = coefficients.conv(highPolynomial.coefficientsN),
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



//    override fun findRoots(): Set<Double> {
//        val solver = LaguerreSolver()
//        val coefficients = coefficients.elements
//
//        val roots = solver.solveAllReal(
//            coefficients = coefficients,
//            initial = 0.5,
//        )
//
//        return roots.toSet()
//    }


    override fun findRoots(
        maxDepth: Int,
        tolerance: Tolerance,
    ): List<Double> {
        val primaryRoot = findPrimaryRoot(
            maxDepth = maxDepth,
            tolerance = tolerance,
        ) ?: return emptyList()

        val (deflatedPolynomial, _) = this.divide(x0 = primaryRoot) ?: return listOf(primaryRoot)

        val lowerDegreeRoots = deflatedPolynomial.findRoots(
            maxDepth = maxDepth,
            tolerance = tolerance,
        )

        return listOf(primaryRoot) + lowerDegreeRoots
    }

    fun findPrimaryRoot(
        maxDepth: Int = 1000,
        tolerance: Tolerance,
    ): Double? {
        val n = degree.toDouble()

        val firstDerivative = derivative
        val secondDerivative = firstDerivative.derivative

        tailrec fun improveRoot(
            approximatedRoot: Double,
            depth: Int,
        ): Double? {
            if (depth > maxDepth) {
                return approximatedRoot
            }

            val p0 = apply(approximatedRoot)

            if (p0.equalsZeroWithTolerance(tolerance = tolerance)) {
                return approximatedRoot
            }

            val p1 = firstDerivative.apply(approximatedRoot)
            val p2 = secondDerivative.apply(approximatedRoot)

            val g = p1 / p0
            val g2 = g * g
            val h = g2 - p2 / p0

            val i = (n - 1) * (n * h - g2)

            if (i < 0.0) {
                return null
            }

            val d = sqrt(i)

            val gd = listOf(
                g + d,
                g - d,
            ).maxBy(::abs)

            val a = n / gd

            return improveRoot(
                approximatedRoot = approximatedRoot - a,
                depth = depth + 1,
            )
        }

        return improveRoot(
            approximatedRoot = 0.5,
            depth = 0,
        )
    }
}

operator fun Double.div(
    other: Complex,
): Complex = Complex.valueOf(this).div(other)

inline fun <T> Iterable<T>.sumOfComplex(
    selector: (T) -> Complex,
): Complex = this.fold(initial = Complex.ZERO) { acc, complex ->
    acc + selector(complex)
}
