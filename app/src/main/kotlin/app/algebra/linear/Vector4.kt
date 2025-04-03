package app.algebra.linear

data class Vector4(
    val x: Double,
    val y: Double,
    val z: Double,
    val w: Double,
) {
    companion object {
        val zero = Vector4(0.0, 0.0, 0.0, 0.0)
    }

    init {
        require(x.isFinite())
        require(y.isFinite())
        require(z.isFinite())
        require(w.isFinite())
    }

    fun dot(
        other: Vector4,
    ): Double = x * other.x + y * other.y + z * other.z + w * other.w

    fun toArray(): DoubleArray = doubleArrayOf(
        x,
        y,
        z,
        w,
    )
}
