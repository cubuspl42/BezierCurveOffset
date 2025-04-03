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

    val vectorXy: Vector1x2
        get() = Vector1x2.of(
            x = this.x,
            y = this.y,
        )

    val vectorXyz: Vector1x3
        get() = Vector1x3.of(
            x = this.x,
            y = this.y,
            z = this.z,
        )

    val vectorYzw: Vector1x3
        get() = Vector1x3.of(
            x = this.y,
            y = this.z,
            z = this.w,
        )

    val vectorZw: Vector1x2
        get() = Vector1x2.of(
            x = this.z,
            y = this.w,
        )

    val transposed: Vector4x1
        get() = Vector4x1.of(
            x = this.x,
            y = this.y,
            z = this.z,
            w = this.w,
        )

    fun dot(
        other: Vector4x1,
    ): Double = dotForced(other)

    operator fun get(
        j: Int,
    ): Double = when (j) {
        0 -> x
        1 -> y
        2 -> z
        3 -> w
        else -> throw IndexOutOfBoundsException("Index $j out of bounds for length 4")
    }
}
