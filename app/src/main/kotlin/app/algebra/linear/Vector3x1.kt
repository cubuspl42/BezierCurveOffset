package app.algebra.linear

/**
 * A three-dimensional vector
 */
@Suppress("DataClassPrivateConstructor")
data class Vector3x1 private constructor(
    override val x: Double,
    override val y: Double,
    override val z: Double,
) : Vector3() {
    companion object {
        fun of(
            x: Double,
            y: Double,
            z: Double,
        ): Vector3x1 = Vector3x1(
            x = x,
            y = y,
            z = z,
        )

        val zero = Vector3x1.of(0.0, 0.0, 0.0)
    }

    val vectorXy: Vector2
        get() = Vector2.of(
            x = x,
            y = y,
        )

    init {
        require(x.isFinite())
        require(y.isFinite())
        require(z.isFinite())
    }

    operator fun plus(
        other: Vector3x1,
    ): Vector3x1 = Vector3x1.of(
        x = x + other.x,
        y = y + other.y,
        z = z + other.z,
    )

    operator fun minus(
        other: Vector3x1,
    ): Vector3x1 = Vector3x1.of(
        x = x - other.x,
        y = y - other.y,
        z = z - other.z,
    )

    fun dot(
        other: Vector1x3,
    ): Double = dotRaw(other)
}

operator fun Double.times(
    v: Vector3x1,
): Vector3x1 = Vector3x1.of(
    x = this * v.x,
    y = this * v.y,
    z = this * v.z,
)

operator fun Vector3x1.div(
    divisor: Double,
): Vector3x1 = Vector3x1.of(
    x = x / divisor,
    y = y / divisor,
    z = z / divisor,
)
