package app.algebra.linear.matrices.matrix3

import app.algebra.NumericObject
import app.algebra.linear.vectors.vector3.Vector1x3
import app.algebra.linear.vectors.vector3.Vector3
import app.algebra.linear.vectors.vector3.Vector3x1
import app.algebra.linear.VectorOrientation

class ColumnMajorMatrix3x3(
    private val data: SquareMatrix3Data<VectorOrientation.Vertical>,
) : Matrix3x3() {
    override val transposed: Matrix3x3
        get() = RowMajorMatrix3x3(
            data = data.interpretTransposed,
        )

    override val row0: Vector1x3
        get() = Vector3.horizontal(
            x = column0.x,
            y = column1.x,
            z = column2.x,
        )

    override val row1: Vector1x3
        get() = Vector3.horizontal(
            x = column0.y,
            y = column1.y,
            z = column2.y,
        )

    override val row2: Vector1x3
        get() = Vector3.horizontal(
            x = column0.z,
            y = column1.z,
            z = column2.z,
        )

    override val column0: Vector3x1
        get() = data.vector0

    override val column1: Vector3x1
        get() = data.vector1

    override val column2: Vector3x1
        get() = data.vector2

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other is Matrix3x3 -> when {
            other is ColumnMajorMatrix3x3 -> when {
                !column0.equalsWithTolerance(other.column0, absoluteTolerance = absoluteTolerance) -> false
                !column1.equalsWithTolerance(other.column1, absoluteTolerance = absoluteTolerance) -> false
                !column2.equalsWithTolerance(other.column2, absoluteTolerance = absoluteTolerance) -> false
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
