package app.geometry

import app.algebra.linear.Vector2
import app.algebra.linear.unaryMinus

/**
 * A direction in 2D Euclidean space, i.e. an infinite set of vectors with cross
 * product equal to zero
 */
@Suppress("DataClassPrivateConstructor")
data class Direction private constructor(
    /**
     * One of the infinitely many vectors pointing in this direction, must not
     * effectively be a zero vector
     */
    val dv: Vector2,
) {
    companion object {
        /**
         * @return A direction described by [dv], or null if [dv] is effectively
         * a zero vector
         */
        fun of(
            dv: Vector2,
        ): Direction? = when {
                dv.lengthSquared == 0.0 -> null
                else -> {
//                    require(dv.lengthSquared > 0.0001)

                    Direction(dv = dv)
                }
            }
    }

    init {
        require(dv.lengthSquared != 0.0)
    }

    val perpendicular: Direction
        // If d is non-zero, its perpendicular vector will also be non-zero
        get() = Direction(dv = dv.perpendicular)

    val opposite: Direction
        get() = Direction(dv = -dv)
}

internal fun Vector2.toDirection(): Direction? = Direction.of(dv = this)
