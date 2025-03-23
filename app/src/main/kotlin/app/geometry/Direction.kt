package app.geometry

import app.algebra.Vector

/**
 * A direction in 2D Euclidean space, i.e. an infinite set of vectors with cross product equal to zero
 */
data class Direction(
    /**
     * One of the infinitely many vectors pointing in this direction, must not be a zero vector
     */
    val d: Vector,
) {
    companion object {
        fun of(
            d: Vector,
        ): Direction? = when {
            d == Vector.zero -> null
            else -> Direction(d = d)
        }
    }

    init {
        require(d != Vector.zero)
    }

    val perpendicular: Direction
        // If d is non-zero, its perpendicular vector will also be non-zero
        get() = Direction(d = d.perpendicular)
}

internal fun Vector.toDirection(): Direction = Direction(d = this)
