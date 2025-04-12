package app.geometry

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
    private val lineEquation: LineEquation,
) {
    /**
     * The initial point of the ray
     */
    val startingPoint: Point = lineEquation.p0.asPoint

    /**
     * The direction of this ray
     */
    val direction: Direction = lineEquation.dv.asDirection!!

    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Ray = Ray(
            lineEquation = LineEquation(
                p0 = point.pvRaw,
                dv = direction.dvRaw,
            )
        )
    }

    internal val sv: RawVector
        get() = startingPoint.pvRaw

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
        val l0 = lineEquation
        val l1 = other.lineEquation

        val solution = LineEquation.solveIntersection(
            l0 = l0,
            l1 = l1,
        ) ?: return null

        val t0 = solution.t0
        val t1 = solution.t1

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
