package app

import java.awt.geom.Path2D

interface BezierCurve {
    /**
     * Time function in range [0, 1].
     */
    abstract class TimeFunction<T> {
        fun evaluate(t: Double): T {
            if (t < 0.0 || t > 1.0) {
                throw IllegalArgumentException("t must be in range [0, 1], but was $t")
            }

            return evaluateDirectly(t)
        }

        val startValue: T
            get() = evaluateDirectly(0.0)

        val endValue: T
            get() = evaluateDirectly(1.0)

        abstract fun evaluateDirectly(t: Double): T

        fun <R> map(
            transform: (T) -> R,
        ): TimeFunction<R> = object : TimeFunction<R>() {
            override fun evaluateDirectly(t: Double): R = transform(this@TimeFunction.evaluate(t))
        }
    }

    val start: Point
    val end: Point

    val path: TimeFunction<Point>

    fun toPath2D(): Path2D.Double
}
