package app.algebra.linear

import app.algebra.NumericObject

data class ColumnMajorMatrix4x4(
    val transposedMatrix: RowMajorMatrix4x4,
) : Matrix4x4() {
    override val transposed: Matrix4x4
        get() = transposedMatrix

    override val row0: Vector1x4
        get() = transposedMatrix.column0.transposed

    override val row1: Vector1x4
        get() = transposedMatrix.column1.transposed

    override val row2: Vector1x4
        get() = transposedMatrix.column2.transposed

    override val row3: Vector1x4
        get() = transposedMatrix.column3.transposed

    override val column0: Vector4x1
        get() = transposedMatrix.row0.transposed

    override val column1: Vector4x1
        get() = transposedMatrix.row1.transposed

    override val column2: Vector4x1
        get() = transposedMatrix.row2.transposed

    override val column3: Vector4x1
        get() = transposedMatrix.row3.transposed

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
