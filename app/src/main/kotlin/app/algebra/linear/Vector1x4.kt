package app.algebra.linear

@Suppress("DataClassPrivateConstructor")
data class Vector1x4 private constructor(
    override val x: Double,
    override val y: Double,
    override val z: Double,
    override val w: Double,
) : Vector4() {
    companion object {
        fun of(
            x: Double,
            y: Double,
            z: Double,
            w: Double,
        ): Vector1x4 = Vector1x4(
            x = x,
            y = y,
            z = z,
            w = w,
        )

        val zero = Vector1x4.of(0.0, 0.0, 0.0, 0.0)
    }

    init {
        require(x.isFinite())
        require(y.isFinite())
        require(z.isFinite())
        require(w.isFinite())
    }

    fun dot(
        other: Vector4x1,
    ): Double = dotRaw(other)
}
