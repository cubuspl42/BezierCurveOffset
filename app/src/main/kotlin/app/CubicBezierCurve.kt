package app

import java.awt.geom.Path2D

data class CubicBezierCurve(
    override val start: Point,
    val control0: Point,
    val control1: Point,
    override val end: Point,
) : BezierCurve {
    override val pathFunction = object : BezierCurve.TimeFunction<Point>() {
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

    val tangentFunction: BezierCurve.TimeFunction<Vector> by lazy {
        derivative.pathFunction.map { it.toVector() }
    }

    val boundTangentFunction = BezierCurve.bind(
        pointFunction = pathFunction,
        vectorFunction = tangentFunction,
    )

    val normalFunction: BezierCurve.TimeFunction<Vector> by lazy {
        tangentFunction.map { it.perpendicular }
    }

    val boundNormalFunction = BezierCurve.bind(
        pointFunction = pathFunction,
        vectorFunction = normalFunction,
    )

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
        direction: Direction,
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
        val startNormal = boundNormalFunction.startValue
        val startNormalLine = startNormal.containingLine

        val endNormal = boundNormalFunction.endValue
        val endNormalLine = endNormal.containingLine

        val normalIntersectionPoint = startNormalLine.intersect(endNormalLine) ?: return moveInDirectionPointWise(
            // If there's no intersection point, the start and end vectors are parallel. We could choose either.
            direction = startNormal.direction,
            distance = offset,
        )

        return moveAwayPointWise(
            origin = normalIntersectionPoint,
            distance = offset,
        )
    }

    override fun toPath2D(): Path2D.Double = Path2D.Double().apply {
        moveTo(start)
        cubicTo(control0, control1, end)
    }
}
