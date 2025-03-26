package app.geometry

import app.algebra.Vector
import app.algebra.div
import app.equalsApproximately
import app.equalsZeroApproximately
import java.awt.geom.Path2D

data class Point(
    val pv: Vector,
) {
    companion object {
        fun midPoint(
            a: Point,
            b: Point,
        ): Point = Point(
            pv = a.pv + (b.pv - a.pv) / 2.0,
        )

        fun areCollinear(
            a: Point,
            b: Point,
            c: Point,
            epsilon: Double = Constants.epsilon,
        ): Boolean {
            val ab = b.pv - a.pv
            val ac = c.pv - a.pv
            return ab.cross(ac).equalsZeroApproximately(epsilon = epsilon)
        }

        fun makeCollinear(
            a: Point,
            b: Point,
            base: Point,
        ): Pair<Point, Point> {
            val biRay = BiRay.fromPoints(
                basePoint = base,
                directionPoint1 = a,
                directionPoint2 = b,
            ) ?: run {
                // If one of the points is the same as the base point, these
                // are essentially two points. Two points are always collinear.
                return Pair(a, b)
            }

            val bisectingRay = biRay.bisectingRay ?: run {
                // If both rays (BA and AB) point in the opposite directions,
                // A and B are definitely collinear
                return Pair(a, b)
            }

            val projectionLine = bisectingRay.perpendicularLine

            return Pair(
                a.projectOnto(projectionLine),
                b.projectOnto(projectionLine),
            )
        }
    }

    constructor(
        px: Double,
        py: Double,
    ) : this(
        pv = Vector(
            x = px,
            y = py,
        ),
    )

    constructor(
        px: Int,
        py: Int,
    ) : this(
        px = px.toDouble(),
        py = py.toDouble(),
    )

    val x: Double
        get() = pv.x

    val y: Double
        get() = pv.y

    fun distanceTo(
        other: Point,
    ): Double = (other.pv - this.pv).length

    fun distanceSquaredTo(
        other: Point,
    ): Double = (other.pv - this.pv).lengthSquared

    /**
     * @param other - point to find the direction to
     * @return direction, or null if this point is the same as the other point
     */
    fun directionTo(
        other: Point,
    ): Direction? {
        if (this == other) {
            return null
        }
        return Direction(
            d = other.pv - this.pv,
        )
    }

    fun translate(
        translation: Translation,
    ): Point = Point(
        pv = pv + translation.tv,
    )

    /**
     * @param direction - direction to move in, must not be a zero vector
     * @param distance - distance to move in the direction
     * @return point moved in the given direction, or null if the direction was
     * numerically
     */
    fun moveInDirection(
        direction: Direction,
        distance: Double,
    ): Point? {
        val d = direction.d
        val dl = d.length

        if (dl == 0.0) {
            return null
        }

        return Point(
            pv = pv + d.scale(distance / dl),
        )
    }

    /**
     * @param origin - point to move away from, must be a different point
     * @param distance - distance to move away from the origin
     * @return the moved point, or null if [origin] was the same as this point
     */
    fun moveAway(
        origin: Point,
        distance: Double,
    ): Point? {
        val direction = directionTo(origin) ?: return null

        return moveInDirection(
            direction = direction,
            distance = distance,
        )
    }

    fun toVector(): Vector = Vector(x, y)

    fun projectOnto(line: Line): Point {
        val s = line.s
        return Point(s + (pv - s).projectOnto(line.d))
    }
}

fun Path2D.moveTo(point: Point) {
    moveTo(point.x, point.y)
}

fun Path2D.lineTo(point: Point) {
    lineTo(point.x, point.y)
}

fun Path2D.quadTo(control: Point, end: Point) {
    quadTo(control.x, control.y, end.x, end.y)
}

fun Path2D.cubicTo(control1: Point, control2: Point, end: Point) {
    curveTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)
}
