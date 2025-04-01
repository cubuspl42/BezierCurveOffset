package app.geometry.bezier_splines

import app.geometry.Point
import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.CubicBezierCurve
import app.geometry.bezier_curves.SegmentCurve

data class BezierSplineEdge(
    val startControl: Point,
    val endControl: Point,
) : SegmentCurve.Edge() {
    override fun bind(
        startKnot: Point,
        endKnot: Point,
    ): BezierCurve<*> = CubicBezierCurve.of(
        start = startKnot,
        control0 = startControl,
        control1 = endControl,
        end = endKnot,
    )
}
