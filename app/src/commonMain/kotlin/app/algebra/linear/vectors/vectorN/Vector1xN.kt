package app.algebra.linear.vectors.vectorN

import app.algebra.linear.VectorOrientation

typealias Vector1xN = VectorN<VectorOrientation.Horizontal>

fun Vector1xN.dot(
    other: VectorNx1,
): Double = dotForced(other)
