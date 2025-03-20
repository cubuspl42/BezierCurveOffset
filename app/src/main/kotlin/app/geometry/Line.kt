package app.geometry

import app.algebra.Vector

/**
 * A line in 2D Euclidean space, described by the equation p = s + td
 */
data class Line(
    /**
     * One of the infinitely many points lying on the line
     */
    val s: Vector,
    /**
     * One of the infinitely many vectors this line is parallel to
     */
    val d: Vector,
) {
    init {
        assert(d != Vector.zero)
    }

    private fun evaluate(
        t: Double,
    ): Vector = s + d.scale(t)

    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Line = Ray.inDirection(
            point = point,
            direction = direction,
        ).containingLine
    }

    fun intersect(
        other: Line,
    ): Point? {
        val det = d.cross(other.d)
        if (det == 0.0) return null // The lines are parallel

        val d = other.s - s
        val u = d.cross(other.d) / det

        return Point(
            p = evaluate(u)
        )
    }
}
