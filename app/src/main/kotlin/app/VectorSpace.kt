package app

abstract class VectorSpace<V> {
    object DoubleVectorSpace : VectorSpace<Double>() {
        override val zero: Double = 0.0

        override fun add(
            v: Double,
            w: Double,
        ): Double = v + w

        override fun scale(
            a: Double,
            v: Double,
        ): Double = a * v

        override fun subtract(
            u: Double,
            v: Double,
        ): Double = u - v
    }

    abstract val zero: V

    abstract fun add(
        u: V,
        v: V,
    ): V

    abstract fun subtract(
        u: V,
        v: V,
    ): V

    abstract fun scale(
        a: Double,
        v: V,
    ): V
}
