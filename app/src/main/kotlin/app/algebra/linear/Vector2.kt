package app.algebra.linear

import kotlin.math.sqrt

abstract class Vector2 {
    companion object {
        fun of(
            x: Double,
            y: Double,
        ): Vector2x1 = Vector2x1.of(x, y)
    }

    /**
     * The length^2 of this vector
     */
    val lengthSquared: Double
        get() = x * x + y * y

    /**
     * The length of this vector
     */
    val length: Double
        get() = sqrt(lengthSquared)

    /**
     * Calculates the dot product of this vector with another vector,
     * assuming that the other has a matching orientation
     */
    fun dotForced(
        other: Vector2,
    ): Double = x * other.x + y * other.y

    fun cross(
        other: Vector2x1,
    ): Double = x * other.y - y * other.x

    abstract val x: Double

    abstract val y: Double
}
