package app.geometry.bezier_curves

import app.geometry.Point

abstract class SegmentCurve {
    abstract class Edge {
        abstract fun bind(
            startKnot: Point,
            endKnot: Point,
        ): SegmentCurve
    }

    abstract val start: Point

    abstract val end: Point
}
