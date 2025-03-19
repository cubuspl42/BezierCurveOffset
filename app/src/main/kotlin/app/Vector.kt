package app

import kotlin.math.sqrt

data class Vector(
    val x: Double,
    val y: Double,
) {
    operator fun minus(
        other: Point,
    ): Vector = Vector(
        x = x - other.x,
        y = y - other.y,
    )

    operator fun plus(
        other: Vector,
    ): Vector = Vector(
        x = x + other.x,
        y = y + other.y,
    )

    fun dot(
        other: Vector,
    ): Double = x * other.x + y * other.y

    fun cross(
        other: Vector,
    ): Double = x * other.y - y * other.x

    fun scale(
        factor: Double,
    ): Vector = Vector(
        x = x * factor,
        y = y * factor,
    )

    /**
     * The counterclockwise perpendicular vector
     */
    val perpendicular: Vector
        get() = Vector(-y, x)

    val length: Double
        get() = sqrt(x * x + y * y)

    fun toPoint(): Point = Point(x, y)
}
