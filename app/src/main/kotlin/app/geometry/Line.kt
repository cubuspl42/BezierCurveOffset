package app.geometry

import app.algebra.linear.Vector2x1

/**
 * A line in 2D Euclidean space, described by the equation p = s + td
 */
data class Line(
    /**
     * One of the infinitely many points lying on the line
     */
    val representativePoint: Point,
    /**
     * One of two directions of this line
     */
    val representativeDirection: Direction,
) {
    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Line = Ray.inDirection(
            point = point,
            direction = direction,
        ).containingLine
    }

    val pv: Vector2x1
        get() = representativePoint.pv

    val dv: Vector2x1
        get() = representativeDirection.dv

    private fun evaluate(
        t: Double,
    ): Vector2x1 = pv + dv.scale(t)

    fun findIntersectionPoint(
        other: Line,
    ): Point? {
        val det = dv.cross(other.dv)
        if (det == 0.0) return null // The lines are parallel

        val ds = other.pv - pv
        val u = ds.cross(other.dv) / det

        return Point.of(
            pv = evaluate(u)
        )
    }
}
