package app.geometry

import app.algebra.Vector
import java.awt.geom.Path2D

data class Point(
    val p: Vector,
) {
    constructor(
        px: Double,
        py: Double,
    ) : this(
        p = Vector(
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
        get() = p.x

    val y: Double
        get() = p.y

    fun distanceTo(
        other: Point,
    ): Double = (other.p - this.p).length

    fun distanceSquaredTo(
        other: Point,
    ): Double = (other.p - this.p).lengthSquared

    /**
     * @param other - point to find the direction to, must be a different point
     */
    fun directionTo(
        other: Point
    ): Direction {
        if (this == other) throw IllegalArgumentException("Points must be different")
        return Direction(
            d = other.p - this.p,
        )
    }

    fun translate(
        translation: Translation,
    ): Point = Point(
        p = p + translation.t,
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
            p = p + v.scale(distance / d),
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
