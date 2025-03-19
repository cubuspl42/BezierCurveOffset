package app

import java.awt.geom.Path2D

interface BezierCurve {
    /**
     * Time function in range [0, 1].
     */
    abstract class TimeFunction<T> {
        companion object {
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

    companion object {
        fun bind(
            pointFunction: TimeFunction<Point>,
            vectorFunction: TimeFunction<Vector>,
        ): TimeFunction<BoundVector> = TimeFunction.map2(
            functionA = pointFunction,
            functionB = vectorFunction,
        ) { point, vector ->
            vector.bind(point)
        }
    }

    val start: Point
    val end: Point

    val pathFunction: TimeFunction<Point>

    fun toPath2D(): Path2D.Double
}
