package app.geometry

import app.algebra.equalsWithTolerance
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
            rawLine = RawLine.of(
                p0 = point.pv,
                p1 = point.pv + direction.dv,
            )!!,
        )
    }


    internal val dv: RawVector
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

    fun findIntersection(
        other: Ray,
    ): Point? {
        val l0 = rawLine
        val l1 = other.rawLine

        val solution = RawLine.findIntersection(
            rawLine0 = l0,
            rawLine1 = l1,
        ) ?: return null

        val t0 = solution.t0
        val t1 = solution.t1

        return when {
            t0 > 0.0 && t1 > 0.0 -> {
                val pi0 = l0.evaluate(t = t0)

                assert(
                    pi0.equalsWithTolerance(
                        l1.evaluate(t = t1),
                        absoluteTolerance = Constants.epsilon,
                    ),
                )

                pi0
            }

            // The intersection point would lye outside the ray
            else -> null
        }
    }

    fun isParallelTo(
        other: Ray,
    ): Boolean = dv.cross(other.dv).equalsWithTolerance(
        0.0,
        absoluteTolerance = Constants.epsilon,
    )
}

fun Ray.toDebugPath(
    document: SVGDocument,
): SVGPathElement = LineSegment.of(
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
