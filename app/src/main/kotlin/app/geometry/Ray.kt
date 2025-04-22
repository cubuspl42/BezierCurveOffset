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
    internal val startingPoint: Point,
    internal val direction: Direction,
) {
    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Ray = Ray(
            startingPoint = point,
            direction = direction,
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
        val l0 = toParametricLineFunction()
        val l1 = other.toParametricLineFunction()

        val t0 = l0.solveIntersection(l1) ?: return null
        if (t0 < 0.0) return null

        val point = l0.apply(t0)

        val t1 = l1.solvePoint(point) ?: return null
        if (t1 < 0.0) return null

        return point.asPoint
    }

    fun toParametricLineFunction(): ParametricLineFunction = ParametricLineFunction(
        s = startingPoint.pv,
        d = dv,
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
