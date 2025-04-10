package app.algebra.linear

import app.algebra.NumericObject

data class ColumnMajorMatrix4x4(
    val transposedMatrix: RowMajorMatrix4x4,
) : Matrix4x4() {
    override fun get(
        i: Int,
        j: Int,
    ): Double = when (i) {
        0 -> when (j) {
            0 -> column0.x
            1 -> column1.x
            2 -> column2.x
            3 -> column3.x
            else -> throw IndexOutOfBoundsException("Index out of bounds: $i, $j")
        }

        1 -> when (j) {
            0 -> column0.y
            1 -> column1.y
            2 -> column2.y
            3 -> column3.y
            else -> throw IndexOutOfBoundsException("Index out of bounds: $i, $j")
        }

        2 -> when (j) {
            0 -> column0.z
            1 -> column1.z
            2 -> column2.z
            3 -> column3.z
            else -> throw IndexOutOfBoundsException("Index out of bounds: $i, $j")
        }

        3 -> when (j) {
            0 -> column0.w
            1 -> column1.w
            2 -> column2.w
            3 -> column3.w
            else -> throw IndexOutOfBoundsException("Index out of bounds: $i, $j")
        }

        else -> throw IndexOutOfBoundsException("Index out of bounds: $i, $j")
    }

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
            other is ColumnMajorMatrix4x4 -> when {
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
