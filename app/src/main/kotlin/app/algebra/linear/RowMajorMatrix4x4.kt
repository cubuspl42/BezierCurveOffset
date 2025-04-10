package app.algebra.linear

import app.algebra.NumericObject

data class RowMajorMatrix4x4(
    override val row0: Vector1x4,
    override val row1: Vector1x4,
    override val row2: Vector1x4,
    override val row3: Vector1x4,
) : Matrix4x4() {
    override val transposed: Matrix4x4
        get() = ColumnMajorMatrix4x4(
            transposedMatrix = this,
        )

    override val column0: Vector4x1
        get() = Vector4x1.of(
            x = row0.x,
            y = row1.x,
            z = row2.x,
            w = row3.x,
        )

    override val column1: Vector4x1
        get() = Vector4x1.of(
            x = row0.y,
            y = row1.y,
            z = row2.y,
            w = row3.y,
        )

    override val column2: Vector4x1
        get() = Vector4x1.of(
            x = row0.z,
            y = row1.z,
            z = row2.z,
            w = row3.z,
        )

    override val column3: Vector4x1
        get() = Vector4x1.of(
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
