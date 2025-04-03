package app.algebra.linear

data class Vector1xN(
    override val xs: List<Double>,
) : VectorN() {
    fun dot(
        other: VectorNx1,
    ): Double = dotForced(other)
}