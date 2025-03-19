package app.geometry

import app.Vector

/**
 * A line in 2D Euclidean space
 */
data class Line(
    /**
     * One of the infinitely many points lying on the line
     */
    val representativePoint: Point,
    /**
     * One of two directions of this line,
     */
    val representativeDirection: Direction,
) {
    /**
     * One of the infinitely many vectors that gives the line a direction
     */
    val representativeVector: Vector
        get() = representativeDirection.representativeVector

    fun intersect(
        other: Line,
    ): Point? {
        val d = representativeVector.cross(other.representativeVector)
        if (d == 0.0) return null

        val v = other.representativePoint - representativePoint
        val t = v.cross(other.representativeVector) / d
        return representativePoint + representativeVector.scale(t)
    }
}
