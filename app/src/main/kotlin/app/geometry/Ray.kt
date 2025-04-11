package app.geometry

import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector2.minus
import app.algebra.linear.vectors.vector2.plus
import app.fill
import app.geometry.curves.LineSegment
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
    ): Vector2<*> {
        if (t < 0) {
            throw IllegalArgumentException("t must be non-negative")
        }

        return sv + direction.dv.scale(t)
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

    internal val sv: Vector2<*>
        get() = startingPoint.pv

    internal val dv: Vector2<*>
        get() = direction.dv

    val containingLine: BoundLine
        get() = BoundLine(
            originPoint = startingPoint,
            biDirection = direction.biDirection,
        )

    val perpendicularLine: BoundLine
        get() = BoundLine(
            originPoint = startingPoint,
            // If d is non-zero, its perpendicular vector will also be non-zero
            biDirection = direction.perpendicular.biDirection,
        )

    val opposite: Ray
        get() = Ray(
            startingPoint = startingPoint,
            direction = direction.opposite,
        )

    fun intersect(
        other: Ray,
    ): Point? {
        val det = dv.cross(other.dv)
        if (det == 0.0) return null // The rays are parallel

        val sd = other.sv - sv
        val u = sd.cross(other.dv) / det
        val v = sd.cross(this.dv) / det

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
    ): Boolean = dv.cross(other.dv) == 0.0
}

fun Ray.toDebugPath(
    document: SVGDocument,
): SVGPathElement = LineSegment(
    start = startingPoint,
    end = startingPoint.translateInDirection(
        direction = direction,
        distance = 100.0,
    ),
).toSvgPath(
    document = document,
).apply {
    fill = "none"
    stroke = "orange"
}
