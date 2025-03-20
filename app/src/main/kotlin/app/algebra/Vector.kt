package app.algebra

import app.BoundVector
import app.VectorSpace
import app.geometry.Direction
import app.geometry.Point
import kotlin.math.sqrt

/**
 * A two-dimensional free vector
 */
data class Vector(
    val x: Double,
    val y: Double,
) {
    /**
     * A vectors space of two-dimensional vectors
     */
    object VectorVectorSpace : VectorSpace<Vector>() {
        override val zero: Vector = Companion.zero

        override fun add(
            u: Vector,
            v: Vector,
        ): Vector = u + v

        override fun subtract(
            u: Vector,
            v: Vector,
        ): Vector = u - v

        override fun scale(
            a: Double,
            v: Vector,
        ): Vector = v.scale(a)
    }

    companion object {
        val zero = Vector(0.0, 0.0)
    }

    operator fun minus(
        other: Vector,
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

    fun bind(
        point: Point
    ): BoundVector = BoundVector(
        vector = this,
        point = point,
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
     * The direction of this vector or null if this vector doesn't have a direction (is a zero vector)
     */
    val directionOrNull: Direction?
        get() = when {
            this == zero -> null
            else -> Direction(d = this)
        }

    /**
     * The direction of this vector
     *
     * @throws IllegalStateException if this vector doesn't have a direction (is a zero vector)
     */
    val direction: Direction
        get() = directionOrNull ?: throw IllegalStateException("Zero vectors don't have a direction")

    /**
     * The counterclockwise perpendicular vector
     */
    val perpendicular: Vector
        get() = Vector(-y, x)

    /**
     * The length of this vector
     */
    val length: Double
        get() = sqrt(x * x + y * y)

    fun toPoint(): Point = Point(p = this)
}
