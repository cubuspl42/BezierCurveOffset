package app.algebra.linear

/**
 * A three-dimensional vector
 */
@Suppress("DataClassPrivateConstructor")
data class Vector1x3 private constructor(
    override val x: Double,
    override val y: Double,
    override val z: Double,
) : Vector3() {
    companion object {
        fun of(
            x: Double,
            y: Double,
            z: Double,
        ): Vector1x3 = Vector1x3(
            x = x,
            y = y,
            z = z,
        )

        val zero = Vector1x3.of(0.0, 0.0, 0.0)
    }

    val vectorXy: Vector2
        get() = Vector2_of(
            x = x,
            y = y,
        )

    init {
        require(x.isFinite())
        require(y.isFinite())
        require(z.isFinite())
    }

    operator fun plus(
        other: Vector1x3,
    ): Vector1x3 = Vector1x3.of(
        x = x + other.x,
        y = y + other.y,
        z = z + other.z,
    )

    operator fun minus(
        other: Vector1x3,
    ): Vector1x3 = Vector1x3.of(
        x = x - other.x,
        y = y - other.y,
        z = z - other.z,
    )

    fun dot(
        other: Vector3x1,
    ): Double = dotRaw(other)
}

operator fun Double.times(
    v: Vector1x3,
): Vector1x3 = Vector1x3.of(
    x = this * v.x,
    y = this * v.y,
    z = this * v.z,
)

operator fun Vector1x3.div(
    divisor: Double,
): Vector1x3 = Vector1x3.of(
    x = x / divisor,
    y = y / divisor,
    z = z / divisor,
)
