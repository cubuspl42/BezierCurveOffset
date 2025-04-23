package app.algebra.linear.matrices.matrix3

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
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
            a00 = column0.a0,
            a01 = column1.a0,
            a02 = column2.a0,
        )

    override val row1: Vector1x3
        get() = Vector3.horizontal(
            a00 = column0.a1,
            a01 = column1.a1,
            a02 = column2.a1,
        )

    override val row2: Vector1x3
        get() = Vector3.horizontal(
            a00 = column0.a2,
            a01 = column1.a2,
            a02 = column2.a2,
        )

    override val column0: Vector3x1
        get() = data.vector0

    override val column1: Vector3x1
        get() = data.vector1

    override val column2: Vector3x1
        get() = data.vector2

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other is Matrix3x3 -> when {
            other is ColumnMajorMatrix3x3 -> when {
                !column0.equalsWithTolerance(other.column0, tolerance = tolerance) -> false
                !column1.equalsWithTolerance(other.column1, tolerance = tolerance) -> false
                !column2.equalsWithTolerance(other.column2, tolerance = tolerance) -> false
                else -> true
            }

            else -> equalsWithToleranceRowWise(
                other = other,
                tolerance = tolerance,
            )
        }

        else -> false
    }
}
