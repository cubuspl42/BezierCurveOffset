package app.geometry

import app.algebra.Vector

/**
 * A direction in 2D Euclidean space, i.e. an infinite set of vectors with cross product equal to zero
 */
@Suppress("DataClassPrivateConstructor")
data class Direction private constructor(
    /**
     * One of the infinitely many vectors pointing in this direction, must not
     * effectively be a zero vector
     */
    val dv: Vector,
) {
    companion object {
        /**
         * @return A direction described by [d], or null if d is effectively a
         * zero vector
         */
        fun of(
            d: Vector,
        ): Direction? = when {
            d.lengthSquared < java.lang.Double.MIN_NORMAL -> null
            else -> Direction(dv = d)
        }
    }

    init {
        // For tiny subnormal values, the length of the underlying vector could
        // be 0.0 (even though x > 0 or y > 0), which is not acceptable for a
        // direction. For simplicity, let's just require that the squared length
        // is at least the smallest positive normal value.
        require(dv.lengthSquared >= java.lang.Double.MIN_NORMAL)
    }

    val perpendicular: Direction
        // If d is non-zero, its perpendicular vector will also be non-zero
        get() = Direction(dv = dv.perpendicular)
}

internal fun Vector.toDirection(): Direction? = Direction.of(d = this)
