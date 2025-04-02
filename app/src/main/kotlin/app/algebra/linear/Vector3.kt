package app.algebra.linear

/**
 * A three-dimensional vector
 */
data class Vector3(
    val x: Double,
    val y: Double,
    val z: Double,
) {
    companion object {
        val zero = Vector3(0.0, 0.0, 0.0)
    }

    val vectorXy: Vector2
        get() = Vector2(
            x = x,
            y = y,
        )

    init {
        require(x.isFinite())
        require(y.isFinite())
        require(z.isFinite())
    }

    operator fun plus(
        other: Vector3,
    ): Vector3 = Vector3(
        x = x + other.x,
        y = y + other.y,
        z = z + other.z,
    )

    operator fun minus(
        other: Vector3,
    ): Vector3 = Vector3(
        x = x - other.x,
        y = y - other.y,
        z = z - other.z,
    )

    fun dot(
        other: Vector3,
    ): Double = x * other.x + y * other.y + z * other.z
}

operator fun Double.times(
    v: Vector3,
): Vector3 = Vector3(
    x = this * v.x,
    y = this * v.y,
    z = this * v.z,
)

operator fun Vector3.div(
    divisor: Double,
): Vector3 = Vector3(
    x = x / divisor,
    y = y / divisor,
    z = z / divisor,
)
