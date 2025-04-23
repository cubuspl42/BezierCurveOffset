package app.algebra.linear.matrices.matrix4

import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.linear.vectors.vector4.Vector4x1
import app.algebra.linear.vectors.vector4.dot
import app.algebra.linear.vectors.vector4.times

sealed class EagerMatrix4x4 : Matrix4x4() {
    final override operator fun times(
        vector: Vector4x1,
    ): Vector4x1 = Vector4.of(
        a0 = row0.dot(vector),
        a1 = row1.dot(vector),
        a2 = row2.dot(vector),
        a3 = row3.dot(vector),
    )

    final override operator fun times(
        other: Matrix4x4,
    ): RowMajorMatrix4x4 = rowMajor(
        row0 = row0 * other,
        row1 = row1 * other,
        row2 = row2 * other,
        row3 = row3 * other,
    )
}
