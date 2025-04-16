package app.geometry.curves.bezier

import app.geometry.Curve
import app.geometry.Point
import app.geometry.curves.LineSegment

internal data class QuadraticBezierCurve(
    val start: Point,
    val control: Point,
    val end: Point,
) : Curve() {
    val lineSegment0: LineSegment
        get() = LineSegment.of(
            start = start,
            end = control,
        )

    val lineSegment1: LineSegment
        get() = LineSegment.of(
            start = control,
            end = end,
        )

    internal fun findSkeleton(
        t: Double,
    ): LineSegment {
        val subPoint0 = lineSegment0.evaluate(t = t)
        val subPoint1 = lineSegment1.evaluate(t = t)

        return LineSegment.of(
            start = subPoint0,
            end = subPoint1,
        )
    }

    override fun evaluate(
        t: Double,
    ): Point = findSkeleton(t = t).evaluate(t = t)
}
