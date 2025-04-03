package app.algebra.linear

data class VectorNx1(
    override val xs: List<Double>,
) : VectorN() {
    fun dot(
        other: Vector1xN,
    ): Double = dotForced(other)
}
