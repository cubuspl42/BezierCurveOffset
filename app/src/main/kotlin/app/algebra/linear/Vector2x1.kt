package app.algebra.linear

import app.geometry.Direction
import app.geometry.Point
import app.geometry.toDirection
import kotlin.math.sqrt

/**
 * A two-dimensional vector, which might (but doesn't have to) have a spatial interpretation
 */
@Suppress("DataClassPrivateConstructor")
data class Vector2x1 private constructor(
    override val x: Double,
    override val y: Double,
) : Vector2() {
    /**
     * A vectors space of two-dimensional vectors
     */
    object Vector2VectorSpace : VectorSpace<Vector2x1>() {
        override val zero: Vector2x1 = Vector2x1.zero

        override fun add(
            u: Vector2x1,
            v: Vector2x1,
        ): Vector2x1 = u + v

        override fun subtract(
            u: Vector2x1,
            v: Vector2x1,
        ): Vector2x1 = u - v

        override fun scale(
            a: Double,
            v: Vector2x1,
        ): Vector2x1 = v.scale(a)
    }

    companion object {
        val zero = Vector2x1.of(0.0, 0.0)

        fun of(
            x: Double,
            y: Double,
        ): Vector2x1 = Vector2x1(
            x = x,
            y = y,
        )

        fun bisector(
            a: Vector2x1,
            b: Vector2x1,
        ): Vector2x1 = b.length * a + a.length * b
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
        other: Vector2x1,
    ): Vector2x1 = Vector2x1.of(
        x = x - other.x,
        y = y - other.y,
    )

    operator fun plus(
        other: Vector2x1,
    ): Vector2x1 = Vector2x1.of(
        x = x + other.x,
        y = y + other.y,
    )

    fun dot(
        other: Vector2x1,
    ): Double = dotRaw(other)

    fun scale(
        factor: Double,
    ): Vector2x1 {
        require(factor.isFinite())
        return Vector2x1.of(
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
    ): Vector2x1 {
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
    val perpendicular: Vector2x1
        get() = Vector2x1.of(-y, x)

    fun projectOnto(other: Vector2x1): Vector2x1 {
        require(other != zero)
        return (this.dot(other) / other.lengthSquared) * other
    }

    fun toPoint(): Point = Point.of(pv = this)
}

operator fun Vector2x1.unaryMinus(): Vector2x1 = Vector2x1.of(
    x = -x,
    y = -y,
)

operator fun Double.times(
    v: Vector2x1,
): Vector2x1 = Vector2x1.of(
    x = this * v.x,
    y = this * v.y,
)

operator fun Vector2x1.div(
    divisor: Double,
): Vector2x1 = Vector2x1.of(
    x = x / divisor,
    y = y / divisor,
)
