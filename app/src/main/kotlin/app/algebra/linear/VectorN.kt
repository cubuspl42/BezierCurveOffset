package app.algebra.linear

data class VectorN<out Vo : VectorOrientation> internal constructor(
    val xs: List<Double>
) {
    companion object {
        fun horizontal(
            xs: List<Double>,
        ): Vector1xN = Vector1xN(
            xs = xs,
        )

        fun horizontal(
            vararg xs: Double,
        ): Vector1xN = Vector1xN(
            xs = xs.toList(),
        )

        fun vertical(
            xs: List<Double>,
        ): VectorNx1= VectorNx1(
            xs = xs,
        )

        fun vertical(
            vararg xs: Double,
        ): VectorNx1= VectorNx1(
            xs = xs.toList(),
        )
    }

    fun dotForced(
        other: VectorN<*>,
    ): Double {
        require(xs.size == other.xs.size)
        return xs.zip(other.xs).sumOf { (a, b) -> a * b }
    }

    fun vertical(
        xs: List<Double>,
    ): VectorN<Nothing> = VectorN(
        xs = xs,
    )
}
