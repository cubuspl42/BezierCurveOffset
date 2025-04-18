package app.algebra.linear.matrices.matrix3

import app.algebra.NumericObject
import app.algebra.linear.vectors.vector3.Vector1x3
import app.algebra.linear.vectors.vector3.Vector3
import app.algebra.linear.vectors.vector3.Vector3x1
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
            a00 = row0.a0,
            a10 = row1.a0,
            a20 = row2.a0,
        )

    override val column1: Vector3x1
        get() = Vector3.vertical(
            a00 = row0.a1,
            a10 = row1.a1,
            a20 = row2.a1,
        )

    override val column2: Vector3x1
        get() = Vector3.vertical(
            a00 = row0.a2,
            a10 = row1.a2,
            a20 = row2.a2,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Double,
    ): Boolean = when {
        other !is Matrix3x3 -> false
        else -> equalsWithToleranceRowWise(
            other = other,
            tolerance = tolerance,
        )
    }
}
