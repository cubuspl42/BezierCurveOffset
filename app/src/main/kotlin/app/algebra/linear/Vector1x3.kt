package app.algebra.linear

import app.algebra.linear.matrices.matrix3.Matrix3x3

typealias Vector1x3 = Vector3<VectorOrientation.Horizontal>

fun Vector1x3.dot(
    other: Vector3x1,
): Double = dotForced(other)

operator fun Vector1x3.times(
    matrix: Matrix3x3,
): Vector1x3 = Vector3.horizontal(
    x = this.dot(matrix.column0),
    y = this.dot(matrix.column1),
    z = this.dot(matrix.column2),
)

inline val Vector1x3.transposed: Vector3x1
    get() {
        @Suppress("UNCHECKED_CAST") return this as Vector3x1
    }
