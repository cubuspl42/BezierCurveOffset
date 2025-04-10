package app.algebra.linear

typealias VectorNx1 = VectorN<VectorOrientation.Vertical>

fun VectorNx1.dot(
    other: Vector1xN,
): Double = dotForced(other)
