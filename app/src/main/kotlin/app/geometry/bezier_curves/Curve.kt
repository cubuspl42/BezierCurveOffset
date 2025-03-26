package app.geometry.bezier_curves

import app.geometry.Point

abstract class Curve {
    abstract val start: Point

    abstract val end: Point
}
