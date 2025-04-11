package app.geometry

import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector2.minus
import app.algebra.linear.vectors.vector2.plus

/**
 * A line in 2D Euclidean space, described by the equation p = s + td
 */
data class Line(
    /**
     * One of the infinitely many points lying on the line
     */
    val representativePoint: Point,
    /**
     * The bi-direction of this line
     */
    val biDirection: BiDirection,
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

    val pv: Vector2<*>
        get() = representativePoint.pv

    val dv: Vector2<*>
        get() = biDirection.dv

    private fun evaluate(
        t: Double,
    ): Vector2<*> = pv + dv.scale(t)

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
