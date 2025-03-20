package app.geometry

import app.algebra.Vector

/**
 * A line in 2D Euclidean space, given by the equation p = p0 + tv
 */
data class Ray(
    /**
     * The initial point of the ray
     */
    val p0: Vector,
    /**
     * One of the infinitely many vectors that give this ray a direction
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
        ): Ray = Ray(
            p0 = point.p,
            v = direction.d,
        )
    }

    val containingLine: Line
        get() = Line(
            p0 = p0,
            v = v,
        )

    val direction: Direction
        get() = Direction(
            d = v,
        )
}
