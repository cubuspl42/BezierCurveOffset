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
class Ray(
    private val rawLine: RawLine,
) {
    private fun evaluate(
        t: Double,
    ): Vector2<*> {
        if (t < 0) {
            throw IllegalArgumentException("t must be non-negative")
        }

        return sv + direction.dv.scale(t)
    }

    /**
     * The initial point of the ray
     */
    val startingPoint: Point = rawLine.p0.asPoint

    /**
     * The direction of this ray
     */
    val direction: Direction = rawLine.dv.asDirection!!

    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Ray = Ray(
            rawLine = RawLine(
                p0 = point.pvRaw,
                dv = direction.dvRaw,
            )
        )
    }

    internal val sv: Vector2<*>
        get() = startingPoint.pv

    internal val dv: Vector2<*>
        get() = direction.dv

    val containingLine: Line
        get() = Line.inDirection(
            point = startingPoint,
            direction = direction,
        )

    val perpendicularLine: Line
        get() = Line.inDirection(
            point = startingPoint,
            direction = direction.perpendicular,
        )

    val opposite: Ray
        get() = startingPoint.castRay(direction.opposite)

    fun intersect(
        other: Ray,
    ): Point? {
        val l0 = rawLine
        val l1 = other.rawLine

        val intersection = RawLine.findUniqueIntersection(
            l0 = l0,
            l1 = l1,
        ) ?: return null

        val t0 = intersection.t0
        val t1 = intersection.t1

        return when {
            t0 > 0.0 && t1 > 0.0 -> {
                val p0 = l0.evaluate(t = t0)

                assert(
                    p0.equalsWithTolerance(
                        l1.evaluate(t = t1),
                        absoluteTolerance = 0.0001,
                    ),
                )

                p0.asPoint
            }

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
