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
    init {
        require(d != Vector.zero)
    }

    val perpendicular: Direction
        get() = Direction(d = d.perpendicular)
}
