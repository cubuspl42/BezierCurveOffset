package app.algebra.bezier_binomials

import app.algebra.polynomials.Polynomial
import app.geometry.RawVector
import app.geometry.curves.LineSegment
import app.geometry.curves.bezier.TimeFunction

/**
 * @param V - the type of the weights and the result
 */
sealed class BezierBinomial<out V> : RealFunction<V>() {
    data class CriticalPointSet(
        val criticalPointsX: Set<Double>,
        val criticalPointsY: Set<Double>,
    ) {
        companion object {
            private const val eps = 0.00001
            private val range = 0.0..1.0
        }

        val criticalPointsXY: Set<Double> by lazy {
            criticalPointsX + criticalPointsY
        }

        fun filterInteresting(): CriticalPointSet = CriticalPointSet(
            criticalPointsX = criticalPointsX.filter { tX -> isInteresting(t = tX) }.toSet(),
            criticalPointsY = criticalPointsY.filter { tY -> isInteresting(t = tY) }.toSet(),
        )

        private fun isInteresting(t: Double): Boolean = t > (0.0 + eps) && t < (1.0 - eps)
    }

    final override fun apply(x: Double): V = evaluate(t = x)

    /**
     * Evaluates the Bézier binomial at the given parameter t, which can be in the range [0, 1], but doesn't have to.
     */
    abstract fun evaluate(t: Double): V
}

val BezierBinomial<RawVector>.lineSegments: List<LineSegment>
    get() = when (this) {
        is LinearBezierBinomial<RawVector> -> this.segmentsLinear
        is QuadraticBezierBinomial<RawVector> -> this.segmentsQuadratic
        is CubicBezierBinomial<RawVector> -> this.segmentsCubic
    }

fun BezierBinomial<Double>.toPolynomialFormula(): Polynomial? = when (this) {
    is LinearBezierBinomial<Double> -> this.toPolynomialFormulaLinear()
    is QuadraticBezierBinomial<Double> -> this.toPolynomialFormulaQuadratic()
    is CubicBezierBinomial<Double> -> this.toPolynomialFormulaCubic()
}

fun BezierBinomial<RawVector>.findFaster(): TimeFunction<RawVector> {
    return object : TimeFunction<RawVector>() {
        override fun evaluateDirectly(t: Double): RawVector = evaluateFast(t = t)
    }
}

fun BezierBinomial<RawVector>.evaluateFast(
    t: Double,
): RawVector = when (this) {
    is LinearBezierBinomial<RawVector> -> this.evaluateLinear(t = t)
    is QuadraticBezierBinomial<RawVector> -> this.evaluateFastQuadratic(t = t)
    is CubicBezierBinomial<RawVector> -> this.evaluateFastCubic(t = t)
}

val BezierBinomial<RawVector>.componentX: BezierBinomial<Double>
    get() = when (this) {
        is LinearBezierBinomial<RawVector> -> this.componentXLinear
        is QuadraticBezierBinomial<RawVector> -> this.componentXQuadratic
        is CubicBezierBinomial<RawVector> -> this.componentXCubic
    }

val BezierBinomial<RawVector>.componentY: BezierBinomial<Double>
    get() = when (this) {
        is LinearBezierBinomial<RawVector> -> this.componentYLinear
        is QuadraticBezierBinomial<RawVector> -> this.componentYQuadratic
        is CubicBezierBinomial<RawVector> -> this.componentYCubic
    }

fun BezierBinomial<Double>.findRoots(): Set<Double> = toPolynomialFormula()?.findRoots() ?: emptySet()

fun DifferentiableBezierBinomial<RawVector>.findAllCriticalPoints(): BezierBinomial.CriticalPointSet {
    val derivative = findDerivative()
    return BezierBinomial.CriticalPointSet(
        criticalPointsX = derivative.componentX.findRoots(),
        criticalPointsY = derivative.componentY.findRoots(),
    )
}

fun DifferentiableBezierBinomial<RawVector>.findInterestingCriticalPoints(): BezierBinomial.CriticalPointSet =
    findAllCriticalPoints().filterInteresting()
