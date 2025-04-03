package app.geometry

import app.algebra.linear.Vector2
import app.fill
import app.geometry.curves.toSvgPath
import app.stroke
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGPathElement

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
    ): Vector2 {
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

    internal val s: Vector2
        get() = startingPoint.pv

    internal val d: Vector2
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

    val opposite: Ray
        get() = Ray(
            startingPoint = startingPoint,
            direction = direction.opposite,
        )

    fun intersect(
        other: Ray,
    ): Point? {
        val det = d.cross(other.d)
        if (det == 0.0) return null // The rays are parallel

        val sd = other.s - s
        val u = sd.cross(other.d) / det
        val v = sd.cross(this.d) / det

        return when {
            u > 0.0 && v > 0.0 -> Point.of(
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

fun Ray.toDebugPath(
    document: SVGDocument,
): SVGPathElement = Subline(
    start = startingPoint,
    end = startingPoint.moveInDirection(
        direction = direction,
        distance = 100.0,
    ),
).toSvgPath(
    document = document,
).apply {
    fill = "none"
    stroke = "orange"
}
