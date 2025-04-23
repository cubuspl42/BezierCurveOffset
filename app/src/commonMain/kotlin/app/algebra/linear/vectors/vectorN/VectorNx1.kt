package app.algebra.linear.vectors.vectorN

import app.algebra.linear.VectorOrientation

typealias VectorNx1 = VectorN<VectorOrientation.Vertical>

fun VectorNx1.dot(
    other: Vector1xN,
): Double = dotForced(other)
