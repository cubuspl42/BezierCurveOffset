package app.geometry.transformations

import app.geometry.Point

abstract class Transformation {
    abstract fun transform(
        point: Point,
    ): Point
}
