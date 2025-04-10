package app.geometry

import app.algebra.NumericObject
import app.algebra.linear.vectors.vector2.Vector2

/**
 * A bi-direction in the 2D Euclidean space, i.e. a pair of opposite directions, or an infinite set of non-zero vectors
 * in which each pair (a, b) is pointing in roughly the same direction (a × b = 0) or an infinite set of lines with the
 * same defining angle.
 */
class BiDirection internal constructor(
    /**
     * One of two directions of this bi-direction
     */
    val representativeDirection: Direction,
): NumericObject {
    companion object {
        /**
         * @return A bi-direction described by [dv], or null if [dv] is effectively
         * a zero vector
         */
        fun of(
            dv: Vector2<*>,
        ): BiDirection? = Direction.of(dv = dv)?.let {
            BiDirection(
                representativeDirection = it,
            )
        }
    }

    val perpendicular: BiDirection
        get() = BiDirection(
            representativeDirection = representativeDirection.perpendicular,
        )

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is BiDirection -> false
        else -> representativeDirection.equalsWithTolerance(
            other = other.representativeDirection,
            absoluteTolerance = absoluteTolerance,
        )
    }
}
