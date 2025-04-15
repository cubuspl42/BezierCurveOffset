package app.geometry.curves.bezier

import app.algebra.bezier_binomials.RealFunction

/**
 * Time function in range [-inf, +inf].
 *
 * @param R - type of the result
 */
abstract class RawTimeFunction<out R> : RealFunction<R>() {
    final override fun apply(x: Double): R = evaluate(t = x)

    abstract fun evaluate(t: Double): R

    fun <R2> map(
        transform: (R) -> R2,
    ): RawTimeFunction<R2> = object : RawTimeFunction<R2>() {
        override fun evaluate(t: Double): R2 = transform(this@RawTimeFunction.evaluate(t))
    }
}
