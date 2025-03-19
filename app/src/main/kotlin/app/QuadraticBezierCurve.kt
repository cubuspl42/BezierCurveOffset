package app

import java.awt.geom.Path2D

data class QuadraticBezierCurve(
    override val start: Point,
    val control: Point,
    override val end: Point,
) : BezierCurve {
    override val pathFunction: BezierCurve.TimeFunction<Point> = object : BezierCurve.TimeFunction<Point>() {
        override fun evaluateDirectly(t: Double): Point {
            val u = 1.0 - t
            val a = start.toVector().scale(u * u)
            val b = control.toVector().scale(2.0 * u * t)
            val c = end.toVector().scale(t * t)
            return (a + b + c).toPoint()
        }
    }

    private val vector0: Vector
        get() = start.toVector()

    private val vector1: Vector
        get() = control.toVector()

    private val vector2: Vector
        get() = end.toVector()

    private val vectorA: Vector
        get() = vector0 - vector1.scale(2.0) + vector2

    private val vectorB: Vector
        get() = (vector1 - vector0).scale(2.0)

    private val vectorC: Vector
        get() = vector0

    private val quadraticFormulaX: QuadraticFormula
        get() = QuadraticFormula(
            a = vectorA.x,
            b = vectorB.x,
            c = vectorC.x,
        )

    private val quadraticFormulaY: QuadraticFormula
        get() = QuadraticFormula(
            a = vectorA.y,
            b = vectorB.y,
            c = vectorC.y,
        )

    override fun toPath2D(): Path2D.Double = Path2D.Double().apply {
        moveTo(start)
        quadTo(control, end)
    }
}

