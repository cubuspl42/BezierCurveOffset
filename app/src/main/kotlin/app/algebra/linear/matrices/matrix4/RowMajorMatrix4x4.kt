package app.algebra.linear.matrices.matrix4

import app.algebra.NumericObject
import app.algebra.linear.vectors.vector4.Vector1x4
import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.linear.vectors.vector4.Vector4x1
import app.algebra.linear.VectorOrientation

class RowMajorMatrix4x4(
    private val data: SquareMatrix4Data<VectorOrientation.Horizontal>,
) : EagerMatrix4x4() {
    override fun get(
        i: Int,
        j: Int,
    ): Double = this[i][j]

    override val transposed: Matrix4x4
        get() = ColumnMajorMatrix4x4(
            data = data.interpretTransposed,
        )

    override val row0: Vector1x4
        get() = data.vector0

    override val row1: Vector1x4
        get() = data.vector1

    override val row2: Vector1x4
        get() = data.vector2

    override val row3: Vector1x4
        get() = data.vector3

    override val column0: Vector4x1
        get() = Vector4.vertical(
            x = row0.x,
            y = row1.x,
            z = row2.x,
            w = row3.x,
        )

    override val column1: Vector4x1
        get() = Vector4.vertical(
            x = row0.y,
            y = row1.y,
            z = row2.y,
            w = row3.y,
        )

    override val column2: Vector4x1
        get() = Vector4.vertical(
            x = row0.z,
            y = row1.z,
            z = row2.z,
            w = row3.z,
        )

    override val column3: Vector4x1
        get() = Vector4.vertical(
            x = row0.w,
            y = row1.w,
            z = row2.w,
            w = row3.w,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is Matrix4x4 -> false
        else -> equalsWithToleranceRowWise(
            other = other,
            absoluteTolerance = absoluteTolerance,
        )
    }
}
