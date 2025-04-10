package app.algebra.linear.matrices.matrix3

import app.algebra.NumericObject
import app.algebra.linear.vectors.vector3.Vector1x3
import app.algebra.linear.vectors.vector3.Vector3
import app.algebra.linear.vectors.vector3.Vector3x1
import app.algebra.linear.vectors.vector3.dot
import app.algebra.linear.vectors.vector3.times

sealed class Matrix3x3 : NumericObject {
    companion object {
        val zero = rowMajor(
            row0 = Vector3.horizontal(0.0, 0.0, 0.0),
            row1 = Vector3.horizontal(0.0, 0.0, 0.0),
            row2 = Vector3.horizontal(0.0, 0.0, 0.0),
        )

        val identity = rowMajor(
            row0 = Vector3.horizontal(1.0, 0.0, 0.0),
            row1 = Vector3.horizontal(0.0, 1.0, 0.0),
            row2 = Vector3.horizontal(0.0, 0.0, 1.0),
        )

        fun rowMajor(
            row0: Vector1x3,
            row1: Vector1x3,
            row2: Vector1x3,
        ): RowMajorMatrix3x3 = RowMajorMatrix3x3(
            data = SquareMatrix3Data(
                vector0 = row0,
                vector1 = row1,
                vector2 = row2,
            ),
        )

        fun columnMajor(
            column0: Vector3x1,
            column1: Vector3x1,
            column2: Vector3x1,
        ): ColumnMajorMatrix3x3 = ColumnMajorMatrix3x3(
            data = SquareMatrix3Data(
                vector0 = column0,
                vector1 = column1,
                vector2 = column2,
            ),
        )
    }

    final override fun equals(other: Any?): Boolean {
        return equalsWithTolerance(
            other = other as? NumericObject ?: return false,
            absoluteTolerance = 0.0,
        )
    }

    final override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }

    protected fun equalsWithToleranceRowWise(
        other: Matrix3x3, absoluteTolerance: Double
    ): Boolean = when {
        !row0.equalsWithTolerance(other.row0, absoluteTolerance = absoluteTolerance) -> false
        !row1.equalsWithTolerance(other.row1, absoluteTolerance = absoluteTolerance) -> false
        !row2.equalsWithTolerance(other.row2, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }

    fun toColumnMajor(): ColumnMajorMatrix3x3 = columnMajor(
        column0 = column0,
        column1 = column1,
        column2 = column2,
    )

    abstract val transposed: Matrix3x3

    operator fun times(
        vector: Vector3x1,
    ): Vector3x1 = Vector3.of(
        x = row0.dot(vector),
        y = row1.dot(vector),
        z = row2.dot(vector),
    )

    @JvmName("timesRm")
    operator fun times(
        other: Matrix3x3,
    ): RowMajorMatrix3x3 = rowMajor(
        row0 = row0 * other,
        row1 = row1 * other,
        row2 = row2 * other,
    )

    abstract val row0: Vector1x3
    abstract val row1: Vector1x3
    abstract val row2: Vector1x3

    abstract val column0: Vector3x1
    abstract val column1: Vector3x1
    abstract val column2: Vector3x1
}
