package app.algebra.bezier_binomials

import app.algebra.linear.Vector2
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.geometry.lineTo
import app.geometry.moveTo
import app.linspace
import app.step
import java.awt.geom.Path2D

abstract class RealFunction<out V> {
    data class SamplingStrategy(
        val x0: Double = 0.0,
        val x1: Double = 1.0,
        val sampleCount: Int,
    ) {
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

fun <V : Any> RealFunction<V?>.sampleValues(
    strategy: SamplingStrategy,
): List<V> = sample(strategy).map { it.value }

fun <V : Any> RealFunction<V?>.sample(
    strategy: SamplingStrategy,
): List<RealFunction.Sample<V>> = strategy.sample(this)

fun <V : Any> SamplingStrategy.sample(
    formula: RealFunction<V?>,
): List<RealFunction.Sample<V>> = linspace(
    x0 = x0,
    x1 = x1,
    n = sampleCount,
).mapNotNull { x ->
    formula.apply(x = x)?.let {
        RealFunction.Sample(
            x = x,
            value = it,
        )
    }
}.toList()

fun RealFunction<Vector2>.toPath2D(
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
