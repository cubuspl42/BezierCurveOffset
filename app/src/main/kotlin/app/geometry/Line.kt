package app.geometry

import app.algebra.linear.Vector2x1

/**
 * A line in 2D Euclidean space, described by the equation p = s + td
 */
data class Line(
    /**
     * One of the infinitely many points lying on the line
     */
    val s: Vector2x1,
    /**
     * One of the infinitely many vectors this line is parallel to, cannot be a zero vector
     */
    val d: Vector2x1,
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

    init {
        require(d != Vector2x1.zero)
    }

    private fun evaluate(
        t: Double,
    ): Vector2x1 = s + d.scale(t)

    fun findIntersectionPoint(
        other: Line,
    ): Point? {
        val det = d.cross(other.d)
        if (det == 0.0) return null // The lines are parallel

        val ds = other.s - s
        val u = ds.cross(other.d) / det

        return Point.of(
            pv = evaluate(u)
        )
    }
}
