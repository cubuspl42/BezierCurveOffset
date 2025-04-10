package app.algebra.linear.matrices.matrix3

import app.algebra.NumericObject
import app.algebra.linear.Vector1x3
import app.algebra.linear.Vector3
import app.algebra.linear.Vector3x1
import app.algebra.linear.VectorOrientation

class RowMajorMatrix3x3(
    private val data: SquareMatrix3Data<VectorOrientation.Horizontal>,
) : Matrix3x3() {
    override val transposed: Matrix3x3
        get() = ColumnMajorMatrix3x3(
            data = data.interpretTransposed,
        )

    override val row0: Vector1x3
        get() = data.vector0

    override val row1: Vector1x3
        get() = data.vector1

    override val row2: Vector1x3
        get() = data.vector2


    override val column0: Vector3x1
        get() = Vector3.vertical(
            x = row0.x,
            y = row1.x,
            z = row2.x,
        )

    override val column1: Vector3x1
        get() = Vector3.vertical(
            x = row0.y,
            y = row1.y,
            z = row2.y,
        )

    override val column2: Vector3x1
        get() = Vector3.vertical(
            x = row0.z,
            y = row1.z,
            z = row2.z,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is Matrix3x3 -> false
        else -> equalsWithToleranceRowWise(
            other = other,
            absoluteTolerance = absoluteTolerance,
        )
    }
}
