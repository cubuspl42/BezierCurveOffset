package app.geometry

abstract class Curve {
    abstract fun containsTValue(t: Double): Boolean

    abstract fun evaluate(t: Double): Point
}
