package app.geometry.bezier_splines

import app.geometry.Point
import app.geometry.bezier_curves.Curve

sealed class SplineEdge {
    abstract fun bind(
        startKnot: Point,
        endKnot: Point,
    ): Curve
}
