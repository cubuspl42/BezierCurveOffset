package app.geometry

import app.algebra.Vector
import app.algebra.div
import app.equalsZeroApproximately
import java.awt.geom.Path2D

@Suppress("DataClassPrivateConstructor")
data class Point private constructor(
    val pv: Vector,
) {
    companion object {
        val zero = Point(
            pv = Vector.zero,
        )

        fun of(
            pv: Vector,
        ): Point = Point(
            pv = pv,
        )

        fun of(
            px: Double,
            py: Double,
        ): Point = of(
            pv = Vector(
                x = px,
                y = py,
            ),
        )

        fun midPoint(
            a: Point,
            b: Point,
        ): Point = Point.of(
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
     * @return direction, or null if this point is effectively the same as the
     * other point
     */
    fun directionTo(
        other: Point,
    ): Direction? = Direction.of(
        dv = other.pv - this.pv,
    )

    fun translate(
        translation: Translation,
    ): Point = Point.of(
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
    ): Point {
        require(distance.isFinite())

        return Point.of(
            pv = pv + direction.dv.resize(newLength = distance)
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
        require(distance.isFinite())

        val direction = directionTo(origin) ?: return null

        return moveInDirection(
            direction = direction,
            distance = distance,
        )
    }

    // TODO: Nuke?
    fun toVector(): Vector = Vector(x, y)

    fun projectOnto(line: Line): Point {
        val s = line.s
        return Point.of(s + (pv - s).projectOnto(line.d))
    }

    fun dump(): String = "Point.of(${pv.x}, ${pv.y})"
}

fun Path2D.moveTo(p: Point) {
    moveTo(p.x, p.y)
}

fun Path2D.lineTo(p: Point) {
    lineTo(p.x, p.y)
}

fun Path2D.quadTo(p1: Point, p2: Point) {
    quadTo(p1.x, p1.y, p2.x, p2.y)
}

fun Path2D.cubicTo(p1: Point, p2: Point, p3: Point) {
    curveTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
}
