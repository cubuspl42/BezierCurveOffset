package app.geometry

abstract class Transformation {
    abstract fun transform(
        point: Point,
    ): Point
}
