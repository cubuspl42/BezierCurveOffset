package app.algebra.linear

abstract class VectorN {
    abstract val xs: List<Double>

    protected fun dotForced(
        other: VectorN,
    ): Double {
        require(xs.size == other.xs.size)
        return xs.zip(other.xs).sumOf { (a, b) -> a * b }
    }
}
