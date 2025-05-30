package app.algebra.linear.vectors.vector4

import app.algebra.linear.VectorOrientation
import app.algebra.linear.matrices.matrix4.Matrix4x4

typealias Vector1x4 = Vector4<VectorOrientation.Horizontal>

fun Vector1x4.dot(
    other: Vector4x1,
): Double = dotForced(other)

operator fun Vector1x4.times(
    other: Matrix4x4,
): Vector1x4 = Vector4.horizontal(
    a00 = this.dot(other.column0),
    a01 = this.dot(other.column1),
    a02 = this.dot(other.column2),
    a03 = this.dot(other.column3),
)

inline val Vector1x4.transposed: Vector4x1
    get() {
        @Suppress("UNCHECKED_CAST") return this as Vector4x1
    }
