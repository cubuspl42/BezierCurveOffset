package app.algebra.linear

import app.geometry.Direction
import app.geometry.Point
import app.geometry.toDirection
import kotlin.math.sqrt

fun Vector2_of(
    x: Double,
    y: Double,
): Vector2 = Vector2.of(x, y,)

/**
 * A two-dimensional vector, which might (but doesn't have to) have a spatial interpretation
 */
@Suppress("DataClassPrivateConstructor")
data class Vector2 private constructor(
    val x: Double,
    val y: Double,
) {
    /**
     * A vectors space of two-dimensional vectors
     */
    object Vector2VectorSpace : VectorSpace<Vector2>() {
        override val zero: Vector2 = Vector2.zero

        override fun add(
            u: Vector2,
            v: Vector2,
        ): Vector2 = u + v

        override fun subtract(
            u: Vector2,
            v: Vector2,
        ): Vector2 = u - v

        override fun scale(
            a: Double,
            v: Vector2,
        ): Vector2 = v.scale(a)
    }

    companion object {
        val zero = Vector2_of(0.0, 0.0)

        fun of(
            x: Double,
            y: Double,
        ): Vector2 = Vector2(
            x = x,
            y = y,
        )

        fun bisector(
            a: Vector2,
            b: Vector2,
        ): Vector2 = b.length * a + a.length * b
    }

    init {
        require(x.isFinite())
        require(y.isFinite())
    }

    fun toVec3(
        z: Double = 1.0,
    ): Vector3x1 = Vector3x1.of(
        x = x,
        y = y,
        z = z,
    )

    operator fun minus(
        other: Vector2,
    ): Vector2 = Vector2_of(
        x = x - other.x,
        y = y - other.y,
    )

    operator fun plus(
        other: Vector2,
    ): Vector2 = Vector2_of(
        x = x + other.x,
        y = y + other.y,
    )

    fun dot(
        other: Vector2,
    ): Double = x * other.x + y * other.y

    fun cross(
        other: Vector2,
    ): Double = x * other.y - y * other.x

    fun scale(
        factor: Double,
    ): Vector2 {
        require(factor.isFinite())
        return Vector2_of(
            x = x * factor,
            y = y * factor,
        )
    }

    /**
     * Resizes this vector to the given length, requiring that the length of
     * this vector is not zero
     *
     * @param newLength - the new length of this vector
     * @return the resized vector
     */
    fun resize(
        newLength: Double,
    ): Vector2 {
        require(newLength.isFinite())
        require(lengthSquared > 0.0)
        return scale(newLength / length)
    }

    /**
     * The direction of this vector
     *
     * @throws IllegalStateException if this vector doesn't have a direction (is a zero vector)
     */
    val direction: Direction
        get() = toDirection() ?: throw IllegalStateException("Zero vectors don't have a direction")

    /**
     * The counterclockwise perpendicular vector
     */
    val perpendicular: Vector2
        get() = Vector2_of(-y, x)

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

    fun projectOnto(other: Vector2): Vector2 {
        require(other != zero)
        return (this.dot(other) / other.lengthSquared) * other
    }

    fun toPoint(): Point = Point.of(pv = this)
}

operator fun Vector2.unaryMinus(): Vector2 = Vector2_of(
    x = -x,
    y = -y,
)

operator fun Double.times(
    v: Vector2,
): Vector2 = Vector2_of(
    x = this * v.x,
    y = this * v.y,
)

operator fun Vector2.div(
    divisor: Double,
): Vector2 = Vector2_of(
    x = x / divisor,
    y = y / divisor,
)
