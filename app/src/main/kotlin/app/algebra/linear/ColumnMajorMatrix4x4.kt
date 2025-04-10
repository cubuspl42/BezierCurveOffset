package app.algebra.linear

import app.algebra.NumericObject

data class ColumnMajorMatrix4x4(
    override val column0: Vector4x1,
    override val column1: Vector4x1,
    override val column2: Vector4x1,
    override val column3: Vector4x1,
) : Matrix4x4() {
    override val row0: Vector1x4
        get() = Vector1x4.of(
            x = column0.x,
            y = column1.x,
            z = column2.x,
            w = column3.x,
        )

    override val row1: Vector1x4
        get() = Vector1x4.of(
            x = column0.y,
            y = column1.y,
            z = column2.y,
            w = column3.y,
        )

    override val row2: Vector1x4
        get() = Vector1x4.of(
            x = column0.z,
            y = column1.z,
            z = column2.z,
            w = column3.z,
        )

    override val row3: Vector1x4
        get() = Vector1x4.of(
            x = column0.w,
            y = column1.w,
            z = column2.w,
            w = column3.w,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other is Matrix4x4 -> when {
            other is ColumnMajorMatrix4x4 ->  when {
                !column0.equalsWithTolerance(other.column0, absoluteTolerance = absoluteTolerance) -> false
                !column1.equalsWithTolerance(other.column1, absoluteTolerance = absoluteTolerance) -> false
                !column2.equalsWithTolerance(other.column2, absoluteTolerance = absoluteTolerance) -> false
                !column3.equalsWithTolerance(other.column3, absoluteTolerance = absoluteTolerance) -> false
                else -> true
            }

            else -> equalsWithToleranceRowWise(
                other = other,
                absoluteTolerance = absoluteTolerance,
            )
        }

        else -> false
    }
}
