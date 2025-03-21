package app.algebra.bezier_formulas

import app.algebra.Vector
import app.algebra.bezier_formulas.RealFunction.SamplingStrategy
import app.algebra.polynomial_formulas.PolynomialFormula
import app.geometry.Segment
import app.geometry.bezier_curves.TimeFunction
import app.geometry.lineTo
import app.geometry.moveTo
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.geom.Path2D

/**
 * @param V - the type of the weights and the result
 */
sealed class BezierFormula<V> : RealFunction<V>() {
    data class CriticalPointSet(
        val criticalPointsX: Set<Double>,
        val criticalPointsY: Set<Double>,
    ) {
        companion object {
            val range = 0.0..1.0
        }

        fun filterInteresting(): CriticalPointSet = CriticalPointSet(
            criticalPointsX = criticalPointsX.filter { it in range }.toSet(),
            criticalPointsY = criticalPointsY.filter { it in range }.toSet(),
        )
    }

    abstract fun findDerivative(): BezierFormula<V>

    final override fun apply(x: Double): V = evaluate(t = x)

    abstract fun evaluate(t: Double): V
}

val BezierFormula<Vector>.segments: List<Segment>
    get() = when (this) {
        is LinearBezierFormula<Vector> -> this.segmentsLinear
        is QuadraticBezierFormula<Vector> -> this.segmentsQuadratic
        is CubicBezierFormula<Vector> -> this.segmentsCubic
    }

fun BezierFormula<Vector>.findSkeleton(
    t: Double,
): BezierFormula<Vector> = when (this) {
    is LinearBezierFormula<Vector> -> throw NotImplementedError()
    is QuadraticBezierFormula<Vector> -> this.findSkeletonQuadratic(t = t)
    is CubicBezierFormula<Vector> -> this.findSkeletonCubic(t = t)
}

fun BezierFormula<Vector>.toDataset(
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

fun BezierFormula<Double>.toPolynomialFormula(): PolynomialFormula = when (this) {
    is LinearBezierFormula<Double> -> this.toPolynomialFormulaLinear()
    is QuadraticBezierFormula<Double> -> this.toPolynomialFormulaQuadratic()
    // We shouldn't need cubic polynomials
    is CubicBezierFormula<Double> -> throw NotImplementedError()
}

fun BezierFormula<Vector>.findFaster(): TimeFunction<Vector> {
    return object : TimeFunction<Vector>() {
        override fun evaluateDirectly(t: Double): Vector = evaluateFast(t = t)
    }
}

fun BezierFormula<Vector>.evaluateFast(
    t: Double,
): Vector = when (this) {
    is LinearBezierFormula<Vector> -> this.evaluateLinear(t = t)
    is QuadraticBezierFormula<Vector> -> this.evaluateFastQuadratic(t = t)
    is CubicBezierFormula<Vector> -> this.evaluateFastCubic(t = t)
}

val BezierFormula<Vector>.componentX: BezierFormula<Double>
    get() = when (this) {
        is LinearBezierFormula<Vector> -> this.componentXLinear
        is QuadraticBezierFormula<Vector> -> this.componentXQuadratic
        is CubicBezierFormula<Vector> -> this.componentXCubic
    }

val BezierFormula<Vector>.componentY: BezierFormula<Double>
    get() = when (this) {
        is LinearBezierFormula<Vector> -> this.componentYLinear
        is QuadraticBezierFormula<Vector> -> this.componentYQuadratic
        is CubicBezierFormula<Vector> -> this.componentYCubic
    }

fun BezierFormula<Double>.findRoots(): Set<Double> = toPolynomialFormula().findRoots()

fun BezierFormula<Vector>.findAllCriticalPoints(): BezierFormula.CriticalPointSet {
    val derivative = findDerivative()
    return BezierFormula.CriticalPointSet(
        criticalPointsX = derivative.componentX.findRoots(),
        criticalPointsY = derivative.componentY.findRoots(),
    )
}

fun BezierFormula<Vector>.findInterestingCriticalPoints(): BezierFormula.CriticalPointSet =
    findAllCriticalPoints().filterInteresting()
