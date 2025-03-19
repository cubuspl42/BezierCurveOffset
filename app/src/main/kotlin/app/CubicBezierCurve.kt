package app

import java.awt.geom.Path2D

data class CubicBezierCurve(
    override val start: Point,
    val control0: Point,
    val control1: Point,
    override val end: Point,
) : BezierCurve {
    override val path = object : BezierCurve.TimeFunction<Point>() {
        override fun evaluateDirectly(t: Double): Point {
            val u = 1.0 - t
            val a = start.toVector().scale(u * u * u)
            val b = control0.toVector().scale(3.0 * u * u * t)
            val c = control1.toVector().scale(3.0 * u * t * t)
            val d = end.toVector().scale(t * t * t)
            return (a + b + c + d).toPoint()
        }
    }

    val derivative: QuadraticBezierCurve by lazy {
        fun s(v: Vector) = v.scale(3.0).toPoint()

        QuadraticBezierCurve(
            start = s(control0 - start),
            control = s(control1 - control0),
            end = s(end - control1),
        )
    }

    fun mapPointWise(
        transform: (Point) -> Point,
    ): CubicBezierCurve = CubicBezierCurve(
        start = transform(start),
        control0 = transform(control0),
        control1 = transform(control1),
        end = transform(end),
    )

    val tangent: BezierCurve.TimeFunction<Vector> by lazy {
        derivative.path.map { it.toVector() }
    }

    val normal: BezierCurve.TimeFunction<Vector> by lazy {
        tangent.map { it.perpendicular }
    }

    fun moveAwayPointWise(
        origin: Point,
        distance: Double,
    ): CubicBezierCurve = mapPointWise {
        it.moveAway(
            origin = origin,
            distance = distance,
        )
    }

    fun moveInDirectionPointWise(
        direction: Vector,
        distance: Double,
    ): CubicBezierCurve = mapPointWise {
        it.moveInDirection(
            direction = direction,
            distance = distance,
        )
    }

    fun moveByOffset(
        offset: Double,
    ): CubicBezierCurve {
        val startNormal = normal.startValue
        val startNormalLine = Line(point = start, direction = startNormal)

        val endNormal = normal.endValue
        val endNormalLine = Line(point = end, direction = endNormal)

        val origin = startNormalLine.intersection(endNormalLine) ?: return moveInDirectionPointWise(
            direction = startNormal,
            distance = offset,
        )

        return moveAwayPointWise(
            origin = origin,
            distance = offset,
        )
    }

    override fun toPath2D(): Path2D.Double = Path2D.Double().apply {
        moveTo(start)
        cubicTo(control0, control1, end)
    }
}
