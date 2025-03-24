package app.algebra.bezier_binomials

import app.algebra.Vector
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.geometry.TimedPointSeries.Companion.sample
import app.geometry.lineTo
import app.geometry.moveTo
import app.step
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.geom.Path2D

abstract class RealFunction<out V> {
    data class SamplingStrategy(
        val x0: Double,
        val x1: Double,
        val xInterval: Double,
    ) {
        companion object {
            fun withSampleCount(
                sampleCount: Int,
            ): SamplingStrategy = SamplingStrategy(
                x0 = 0.0,
                x1 = 1.0,
                xInterval = 1.0 / sampleCount,
            )
        }

        init {
            require(x1 >= x0)
        }

    }

    data class Sample<V>(
        val x: Double,
        val value: V,
    )

    abstract fun apply(x: Double): V
}

fun <V: Any> RealFunction<V?>.sampleValues(
    strategy: SamplingStrategy,
): List<V> = sample(strategy).map { it.value }

fun <V : Any> RealFunction<V?>.sample(
    strategy: SamplingStrategy,
): List<RealFunction.Sample<V>> = strategy.sample(this)

fun <V : Any> SamplingStrategy.sample(
    formula: RealFunction<V?>,
): List<RealFunction.Sample<V>> = (x0..x1 step xInterval).mapNotNull { x ->
    formula.apply(x = x)?.let {
        RealFunction.Sample(
            x = x,
            value = it,
        )
    }
}

fun RealFunction<Double>.toDataset(
    samplingStrategy: SamplingStrategy,
    name: String,
): XYSeriesCollection {
    val samples = sample(strategy = samplingStrategy)

    val series = XYSeries(name)

    samples.forEach { sample ->
        series.add(sample.x, sample.value)
    }

    return XYSeriesCollection().apply {
        addSeries(series)
    }
}

fun RealFunction<Vector>.toPath2D(
    samplingStrategy: SamplingStrategy,
): Path2D {
    val points = sampleValues(strategy = samplingStrategy).map {
        it.toPoint()
    }

    return Path2D.Double().apply {
        moveTo(points.first())
        points.drop(1).forEach { point ->
            lineTo(point)
        }
    }
}
