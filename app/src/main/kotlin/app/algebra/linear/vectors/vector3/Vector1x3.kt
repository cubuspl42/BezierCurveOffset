package app.algebra.linear.vectors.vector3

import app.algebra.linear.VectorOrientation
import app.algebra.linear.matrices.matrix3.Matrix3x3

typealias Vector1x3 = Vector3<VectorOrientation.Horizontal>

fun Vector1x3.dot(
    other: Vector3x1,
): Double = dotForced(other)

operator fun Vector1x3.times(
    matrix: Matrix3x3,
): Vector1x3 = Vector3.horizontal(
    a00 = this.dot(matrix.column0),
    a01 = this.dot(matrix.column1),
    a02 = this.dot(matrix.column2),
)

inline val Vector1x3.transposed: Vector3x1
    get() {
        @Suppress("UNCHECKED_CAST") return this as Vector3x1
    }
