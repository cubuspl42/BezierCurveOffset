package app.algebra.linear

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance

/**
 * A two-dimensional vector, which might (but doesn't have to) have a spatial interpretation
 */
@Suppress("DataClassPrivateConstructor")
data class Vector1x2 private constructor(
    override val x: Double,
    override val y: Double,
) : Vector2() {
    /**
     * A vectors space of two-dimensional vectors
     */
    object Vector2VectorSpace : VectorSpace<Vector1x2>() {
        override val zero: Vector1x2 = Vector1x2.zero

        override fun add(
            u: Vector1x2,
            v: Vector1x2,
        ): Vector1x2 = u + v

        override fun subtract(
            u: Vector1x2,
            v: Vector1x2,
        ): Vector1x2 = u - v

        override fun scale(
            a: Double,
            v: Vector1x2,
        ): Vector1x2 = v.scale(a)
    }

    companion object {
        val zero = Vector1x2.of(0.0, 0.0)

        fun of(
            x: Double,
            y: Double,
        ): Vector1x2 = Vector1x2(
            x = x,
            y = y,
        )
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
        other: Vector1x2,
    ): Vector1x2 = Vector1x2.of(
        x = x - other.x,
        y = y - other.y,
    )

    operator fun plus(
        other: Vector1x2,
    ): Vector1x2 = Vector1x2.of(
        x = x + other.x,
        y = y + other.y,
    )

    fun dot(
        other: Vector2x1,
    ): Double = dotForced(other)

    fun scale(
        factor: Double,
    ): Vector1x2 {
        require(factor.isFinite())
        return Vector1x2.of(
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
    ): Vector1x2 {
        require(newLength.isFinite())
        require(lengthSquared > 0.0)
        return scale(newLength / length)
    }

    /**
     * The counterclockwise perpendicular vector
     */
    val perpendicular: Vector1x2
        get() = Vector1x2.of(-y, x)

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is Vector1x2 -> false
        !x.equalsWithTolerance(other.x, absoluteTolerance = absoluteTolerance) -> false
        !y.equalsWithTolerance(other.y, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }
}

operator fun Vector1x2.unaryMinus(): Vector1x2 = Vector1x2.of(
    x = -x,
    y = -y,
)

operator fun Double.times(
    v: Vector1x2,
): Vector1x2 = Vector1x2.of(
    x = this * v.x,
    y = this * v.y,
)

operator fun Vector1x2.div(
    divisor: Double,
): Vector1x2 = Vector1x2.of(
    x = x / divisor,
    y = y / divisor,
)
