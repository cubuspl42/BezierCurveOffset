package app.algebra.linear

typealias Vector1xN = VectorN<VectorOrientation.Horizontal>

fun Vector1xN.dot(
    other: VectorNx1,
): Double = dotForced(other)
