package app.geometry.curves.bezier

import app.algebra.euclidean.bezier_binomials.ParametricCurveFunction
import app.algebra.euclidean.bezier_binomials.RealFunction
import app.geometry.RawVector

/**
 * Time function in range [0, 1].
 *
 * @param R - type of the result
 */
abstract class TimeFunction<R> : RealFunction<R> {
    companion object {
        fun wrap(
            curveFunction: ParametricCurveFunction,
        ): TimeFunction<RawVector> = object : TimeFunction<RawVector>() {
            override fun evaluateDirectly(t: Double): RawVector = curveFunction.apply(t)
        }

        fun <A, B, R> map2(
            functionA: TimeFunction<A>,
            functionB: TimeFunction<B>,
            transform: (A, B) -> R,
        ): TimeFunction<R> = object : TimeFunction<R>() {
            override fun evaluateDirectly(
                t: Double,
            ): R = transform(functionA.evaluate(t), functionB.evaluate(t))
        }
    }

    final override fun apply(x: Double): R = evaluate(t = x)

    fun evaluate(t: Double): R {
        if (t < 0.0 || t > 1.0) {
            throw IllegalArgumentException("t must be in range [0, 1], but was $t")
        }

        return evaluateDirectly(t)
    }

    val startValue: R
        get() = evaluateDirectly(0.0)

    val endValue: R
        get() = evaluateDirectly(1.0)

    abstract fun evaluateDirectly(t: Double): R

    fun <R2> map(
        transform: (R) -> R2,
    ): TimeFunction<R2> = object : TimeFunction<R2>() {
        override fun evaluateDirectly(t: Double): R2 = transform(this@TimeFunction.evaluate(t))
    }
}
