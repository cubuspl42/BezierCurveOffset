package app.geometry

import app.algebra.Vector

/**
 * A ray in 2D Euclidean space, described by the equation p = s + td for t >= 0
 */
data class Ray(
    /**
     * The initial point of the ray
     */
    val s: Vector,
    /**
     * One of the infinitely many vectors that give this ray a direction
     */
    val d: Vector,
) {
    init {
        assert(d != Vector.zero)
    }

    private fun evaluate(
        t: Double,
    ): Vector {
        if (t < 0) {
            throw IllegalArgumentException("t must be non-negative")
        }

        return s + d.scale(t)
    }

    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Ray = Ray(
            s = point.p,
            d = direction.d,
        )
    }

    val startingPoint: Point
        get() = s.toPoint()

    val direction: Direction
        get() = Direction(
            d = d,
        )

    val containingLine: Line
        get() = Line(
            s = s,
            d = d,
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
                p = evaluate(t = u),
            )

            // The intersection point would lye outside the ray
            else -> null
        }
    }
}
