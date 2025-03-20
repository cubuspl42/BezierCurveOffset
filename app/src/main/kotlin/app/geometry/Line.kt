package app.geometry

import app.algebra.Vector

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
    init {
        assert(v != Vector.zero)
    }

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
        val d = v.cross(other.v)
        if (d == 0.0) return null

        val s = (other.p0 - p0).cross(other.v) / d
        return Point(
            p = v + v.scale(s),
        )
    }
}
