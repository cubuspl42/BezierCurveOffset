package app.algebra.linear

/**
 * A three-dimensional vector
 */
@Suppress("DataClassPrivateConstructor")
data class Vector3 private constructor(
    val x: Double,
    val y: Double,
    val z: Double,
) {
    companion object {
        fun of(
            x: Double,
            y: Double,
            z: Double,
        ): Vector3 = Vector3(
            x = x,
            y = y,
            z = z,
        )
        
        val zero = Vector3.of(0.0, 0.0, 0.0)
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
        other: Vector3,
    ): Vector3 = Vector3.of(
        x = x + other.x,
        y = y + other.y,
        z = z + other.z,
    )

    operator fun minus(
        other: Vector3,
    ): Vector3 = Vector3.of(
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
): Vector3 = Vector3.of(
    x = this * v.x,
    y = this * v.y,
    z = this * v.z,
)

operator fun Vector3.div(
    divisor: Double,
): Vector3 = Vector3.of(
    x = x / divisor,
    y = y / divisor,
    z = z / divisor,
)
