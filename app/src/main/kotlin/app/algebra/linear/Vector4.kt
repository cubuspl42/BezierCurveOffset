package app.algebra.linear

@Suppress("DataClassPrivateConstructor")
data class Vector4 private constructor(
    val x: Double,
    val y: Double,
    val z: Double,
    val w: Double,
) {
    companion object {
        fun of(
            x: Double,
            y: Double,
            z: Double,
            w: Double,
        ): Vector4 = Vector4(
            x = x,
            y = y,
            z = z,
            w = w,
        )

        val zero = Vector4.of(0.0, 0.0, 0.0, 0.0)
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
