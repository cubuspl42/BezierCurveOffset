package app.geometry.bezier_curves

import app.geometry.Point

abstract class SegmentCurve {
    abstract class Edge<out CurveT: SegmentCurve> {
        abstract fun bind(
            startKnot: Point,
            endKnot: Point,
        ): CurveT
    }

    abstract val start: Point

    abstract val end: Point
}
