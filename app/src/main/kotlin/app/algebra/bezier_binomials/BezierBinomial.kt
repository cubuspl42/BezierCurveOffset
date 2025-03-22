package app.algebra.bezier_binomials

import app.algebra.Vector
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.algebra.polynomials.Polynomial
import app.geometry.Segment
import app.geometry.bezier_curves.TimeFunction
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection

/**
 * @param V - the type of the weights and the result
 */
sealed class BezierBinomial<V> : RealFunction<V>() {
    data class CriticalPointSet(
        val criticalPointsX: Set<Double>,
        val criticalPointsY: Set<Double>,
    ) {
        companion object {
            private const val eps = 0.00001
            private val range = 0.0..1.0
        }

        val criticalPoints: Set<Double> by lazy {
            criticalPointsX + criticalPointsY
        }

        fun filterInteresting(): CriticalPointSet = CriticalPointSet(
            criticalPointsX = criticalPointsX.filter { tX -> isInteresting(t = tX) }.toSet(),
            criticalPointsY = criticalPointsY.filter { tY -> isInteresting(t = tY) }.toSet(),
        )

        private fun isInteresting(t: Double): Boolean = t > (0.0 + eps) && t < (1.0 - eps)
    }

    abstract fun findDerivative(): BezierBinomial<V>

    final override fun apply(x: Double): V = evaluate(t = x)

    abstract fun evaluate(t: Double): V
}

val BezierBinomial<Vector>.segments: List<Segment>
    get() = when (this) {
        is ConstantBezierBinomial<Vector> -> this.segmentsConstant
        is LinearBezierBinomial<Vector> -> this.segmentsLinear
        is QuadraticBezierBinomial<Vector> -> this.segmentsQuadratic
        is CubicBezierBinomial<Vector> -> this.segmentsCubic
    }

fun BezierBinomial<Vector>.findSkeleton(
    t: Double,
): BezierBinomial<Vector> = when (this) {
    is ConstantBezierBinomial<*> -> TODO("Move this logic to curves")
    is LinearBezierBinomial<Vector> ->  this.findSkeletonLinear(t = t)
    is QuadraticBezierBinomial<Vector> -> this.findSkeletonQuadratic(t = t)
    is CubicBezierBinomial<Vector> -> this.findSkeletonCubic(t = t)
}

fun BezierBinomial<Vector>.toDataset(
    samplingStrategy: SamplingStrategy,
): XYSeriesCollection {
    val samples = sample(strategy = samplingStrategy)

    val xSeries = XYSeries("X")
    val ySeries = XYSeries("Y")

    samples.forEach { sample ->
        val t = sample.x
        xSeries.add(t, sample.value.x)
        ySeries.add(t, sample.value.y)
    }

    return XYSeriesCollection().apply {
        addSeries(xSeries)
        addSeries(ySeries)
    }
}

fun BezierBinomial<Double>.toPolynomialFormula(): Polynomial = when (this) {
    is ConstantBezierBinomial<Double> -> this.toPolynomialFormulaConstant()
    is LinearBezierBinomial<Double> -> this.toPolynomialFormulaLinear()
    is QuadraticBezierBinomial<Double> -> this.toPolynomialFormulaQuadratic()
    // We shouldn't need cubic polynomials
    is CubicBezierBinomial<Double> -> throw NotImplementedError()
}

fun BezierBinomial<Vector>.findFaster(): TimeFunction<Vector> {
    return object : TimeFunction<Vector>() {
        override fun evaluateDirectly(t: Double): Vector = evaluateFast(t = t)
    }
}

fun BezierBinomial<Vector>.evaluateFast(
    t: Double,
): Vector = when (this) {
    is ConstantBezierBinomial<Vector> -> TODO("Move this to curves")
    is LinearBezierBinomial<Vector> -> this.evaluateLinear(t = t)
    is QuadraticBezierBinomial<Vector> -> this.evaluateFastQuadratic(t = t)
    is CubicBezierBinomial<Vector> -> this.evaluateFastCubic(t = t)
}

val BezierBinomial<Vector>.componentX: BezierBinomial<Double>
    get() = when (this) {
        is ConstantBezierBinomial<Vector> -> TODO("Move this to curves")
        is LinearBezierBinomial<Vector> -> this.componentXLinear
        is QuadraticBezierBinomial<Vector> -> this.componentXQuadratic
        is CubicBezierBinomial<Vector> -> this.componentXCubic
    }

val BezierBinomial<Vector>.componentY: BezierBinomial<Double>
    get() = when (this) {
        is ConstantBezierBinomial<Vector> -> TODO("Move this to curves")
        is LinearBezierBinomial<Vector> -> this.componentYLinear
        is QuadraticBezierBinomial<Vector> -> this.componentYQuadratic
        is CubicBezierBinomial<Vector> -> this.componentYCubic
    }

fun BezierBinomial<Double>.findRoots(): Set<Double> = toPolynomialFormula().findRoots()

fun BezierBinomial<Vector>.findAllCriticalPoints(): BezierBinomial.CriticalPointSet {
    val derivative = findDerivative()
    return BezierBinomial.CriticalPointSet(
        criticalPointsX = derivative.componentX.findRoots(),
        criticalPointsY = derivative.componentY.findRoots(),
    )
}

fun BezierBinomial<Vector>.findInterestingCriticalPoints(): BezierBinomial.CriticalPointSet =
    findAllCriticalPoints().filterInteresting()
