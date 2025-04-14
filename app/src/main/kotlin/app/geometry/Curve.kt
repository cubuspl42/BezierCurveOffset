package app.geometry

abstract class Curve {
    data class IntersectionDetails(
        val t0: Double,
        val t1: Double,
    )

    abstract fun evaluate(t: Double): Point
}
