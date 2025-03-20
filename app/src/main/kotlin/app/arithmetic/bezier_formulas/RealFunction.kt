package app.arithmetic.bezier_formulas

import app.arithmetic.bezier_formulas.RealFunction.SamplingStrategy
import app.step
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection

abstract class RealFunction<V> {
    data class SamplingStrategy(
        val x0: Double,
        val x1: Double,
        val xInterval: Double,
    ) {
        init {
            assert(x1 >= x0)
        }

        fun <V> sample(
            formula: RealFunction<V>,
        ): List<Sample<V>> = (x0..x1 step xInterval).map { x ->
            Sample(
                x = x,
                value = formula.apply(x = x),
            )
        }
    }

    data class Sample<V>(
        val x: Double,
        val value: V,
    )

    fun sample(
        strategy: SamplingStrategy,
    ): List<Sample<V>> = strategy.sample(this)

    fun sampleValues(
        strategy: SamplingStrategy,
    ): List<V> = sample(strategy).map { it.value }

    abstract fun apply(x: Double): V
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
