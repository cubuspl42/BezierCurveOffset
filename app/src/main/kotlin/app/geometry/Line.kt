package app.geometry

import app.Vector

/**
 * A line in 2D Euclidean space, given by the equation p = p0 + tv
 */
data class Line(
    /**
     * One of the infinitely many points lying on the line
     */
    val p0: Vector,
    /**
     * One of the infinitely many vectors this line is parallel to
     */
    val v: Vector,
) {
    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Line = Line(
            p0 = point.p,
            v = direction.d,
        )
    }

    fun intersect(
        other: Line,
    ): Point? {
        val d = v.cross(other.v)
        if (d == 0.0) return null

        val s = (other.p0 - p0).cross(other.v) / d
        return Point(
            p = v + v.scale(s),
        )
    }
}
