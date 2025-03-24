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
     * @param other - point to find the direction to, must be a different point
     */
    fun directionTo(
        other: Point,
    ): Direction {
        if (this == other) throw IllegalArgumentException("Points must be different")
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
     */
    fun moveInDirection(
        direction: Direction,
        distance: Double,
    ): Point {
        val v = direction.d
        val d = v.length
        return Point(
            pv = pv + v.scale(distance / d),
        )
    }

    /**
     * @param origin - point to move away from, must be a different point
     * @param distance - distance to move away from the origin
     */
    fun moveAway(
        origin: Point,
        distance: Double,
    ): Point {
        if (origin == this) throw IllegalArgumentException("Origin point must be different from this point")

        return moveInDirection(
            direction = directionTo(origin),
            distance = distance,
        )
    }

    fun toVector(): Vector = Vector(x, y)

    fun projectOnto(line: Line): Point {
        val s = line.s
        val d = line.d
        val v = pv - s
        val t = v.dot(d) / d.lengthSquared
        return Point(s + d.scale(t))
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
