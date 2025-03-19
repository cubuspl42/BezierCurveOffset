package app

import java.awt.geom.Path2D

data class QuadraticBezierCurve(
    override val start: Point,
    val control: Point,
    override val end: Point,
) : BezierCurve {
    override val path: BezierCurve.TimeFunction<Point> = object : BezierCurve.TimeFunction<Point>() {
        override fun evaluateDirectly(t: Double): Point {
            val u = 1.0 - t
            val a = start.toVector().scale(u * u)
            val b = control.toVector().scale(2.0 * u * t)
            val c = end.toVector().scale(t * t)
            return (a + b + c).toPoint()
        }
    }

    override fun toPath2D(): Path2D.Double = Path2D.Double().apply {
        moveTo(start)
        quadTo(control, end)
    }
}
