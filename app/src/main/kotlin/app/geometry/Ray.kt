package app.geometry

import app.algebra.Vector

/**
 * A ray in 2D Euclidean space, described by the equation p = s + td for t >= 0
 */
data class Ray(
    /**
     * The initial point of the ray
     */
    val startingPoint: Point,
    /**
     * The direction of this ray
     */
    val direction: Direction,
) {
    private fun evaluate(
        t: Double,
    ): Vector {
        if (t < 0) {
            throw IllegalArgumentException("t must be non-negative")
        }

        return s + direction.dv.scale(t)
    }

    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Ray = Ray(
            startingPoint = point,
            direction = direction,
        )
    }

    internal val s: Vector
        get() = startingPoint.pv

    internal val d: Vector
        get() = direction.dv

    val containingLine: Line
        get() = Line(
            s = s,
            d = d,
        )

    val perpendicularLine: Line
        get() = Line(
            s = s,
            // If d is non-zero, its perpendicular vector will also be non-zero
            d = d.perpendicular,
        )

    fun intersect(
        other: Ray,
    ): Point? {
        val det = d.cross(other.d)
        if (det == 0.0) return null // The rays are parallel

        val d = other.s - s
        val u = d.cross(other.d) / det
        val v = d.cross(this.d) / det

        return when {
            u > 0.0 && v > 0.0 -> Point(
                pv = evaluate(t = u),
            )

            // The intersection point would lye outside the ray
            else -> null
        }
    }

    fun isParallelTo(
        other: Ray,
    ): Boolean = d.cross(other.d) == 0.0
}
