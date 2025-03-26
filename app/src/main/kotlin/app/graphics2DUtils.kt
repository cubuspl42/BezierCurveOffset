package app

import app.geometry.Point
import java.awt.Graphics2D
import kotlin.math.roundToInt

fun Graphics2D.drawCircle(
    center: Point,
    radius: Double,
) {
    val diameter = (2 * radius).roundToInt()
    val x = center.x - radius
    val y = center.y - radius
    drawOval(x.roundToInt(), y.roundToInt(), diameter, diameter)
}

fun Graphics2D.fillCircle(
    center: Point,
    radius: Double,
) {
    val diameter = (2 * radius).roundToInt()
    val x = center.x - radius
    val y = center.y - radius
    fillOval(x.roundToInt(), y.roundToInt(), diameter, diameter)
}
