package app.geometry.bezier_splines

import app.geometry.Point
import app.geometry.bezier_curves.SegmentCurve

sealed class SplineEdge {
    abstract fun bind(
        startKnot: Point,
        endKnot: Point,
    ): SegmentCurve
}
