package app.algebra.bezier_binomials

import app.algebra.linear.Vector2x1
import app.algebra.polynomials.Polynomial
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

    abstract fun evaluate(t: Double): V
}

val BezierBinomial<Vector2x1>.lineSegments: List<LineSegment>
    get() = when (this) {
        is LinearBezierBinomial<Vector2x1> -> this.segmentsLinear
        is QuadraticBezierBinomial<Vector2x1> -> this.segmentsQuadratic
        is CubicBezierBinomial<Vector2x1> -> this.segmentsCubic
    }

fun BezierBinomial<Double>.toPolynomialFormula(): Polynomial? = when (this) {
    is LinearBezierBinomial<Double> -> this.toPolynomialFormulaLinear()
    is QuadraticBezierBinomial<Double> -> this.toPolynomialFormulaQuadratic()
    // We shouldn't need cubic polynomials
    is CubicBezierBinomial<Double> -> throw NotImplementedError()
}

fun BezierBinomial<Vector2x1>.findFaster(): TimeFunction<Vector2x1> {
    return object : TimeFunction<Vector2x1>() {
        override fun evaluateDirectly(t: Double): Vector2x1 = evaluateFast(t = t)
    }
}

fun BezierBinomial<Vector2x1>.evaluateFast(
    t: Double,
): Vector2x1 = when (this) {
    is LinearBezierBinomial<Vector2x1> -> this.evaluateLinear(t = t)
    is QuadraticBezierBinomial<Vector2x1> -> this.evaluateFastQuadratic(t = t)
    is CubicBezierBinomial<Vector2x1> -> this.evaluateFastCubic(t = t)
}

val BezierBinomial<Vector2x1>.componentX: BezierBinomial<Double>
    get() = when (this) {
        is LinearBezierBinomial<Vector2x1> -> this.componentXLinear
        is QuadraticBezierBinomial<Vector2x1> -> this.componentXQuadratic
        is CubicBezierBinomial<Vector2x1> -> this.componentXCubic
    }

val BezierBinomial<Vector2x1>.componentY: BezierBinomial<Double>
    get() = when (this) {
        is LinearBezierBinomial<Vector2x1> -> this.componentYLinear
        is QuadraticBezierBinomial<Vector2x1> -> this.componentYQuadratic
        is CubicBezierBinomial<Vector2x1> -> this.componentYCubic
    }

fun BezierBinomial<Double>.findRoots(): Set<Double> = toPolynomialFormula()?.findRoots() ?: emptySet()

fun DifferentiableBezierBinomial<Vector2x1>.findAllCriticalPoints(): BezierBinomial.CriticalPointSet {
    val derivative = findDerivative()
    return BezierBinomial.CriticalPointSet(
        criticalPointsX = derivative.componentX.findRoots(),
        criticalPointsY = derivative.componentY.findRoots(),
    )
}

fun DifferentiableBezierBinomial<Vector2x1>.findInterestingCriticalPoints(): BezierBinomial.CriticalPointSet =
    findAllCriticalPoints().filterInteresting()
