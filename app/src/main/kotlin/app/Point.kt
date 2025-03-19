package app

import java.awt.geom.Path2D

data class Point(
    val x: Double,
    val y: Double,
) {
    operator fun minus(
        other: Point,
    ): Vector = Vector(
        x = x - other.x,
        y = y - other.y,
    )

    operator fun plus(
        vector: Vector,
    ): Point = Point(
        x = x + vector.x,
        y = y + vector.y,
    )

    /**
     * @param direction - direction to move in, must not be a zero vector
     * @param distance - distance to move in the direction
     */
    fun moveInDirection(
        direction: Vector,
        distance: Double,
    ): Point {
        val d = direction.length
        if (d == 0.0) throw IllegalArgumentException("Direction vector must not be a zero vector")
        return this + direction.scale(distance / d)
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
            direction = this - origin,
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
