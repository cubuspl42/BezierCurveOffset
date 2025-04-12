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
            a00 = row0.a0,
            a10 = row1.a0,
            a20 = row2.a0,
            a30 = row3.a0,
        )

    override val column1: Vector4x1
        get() = Vector4.vertical(
            a00 = row0.a1,
            a10 = row1.a1,
            a20 = row2.a1,
            a30 = row3.a1,
        )

    override val column2: Vector4x1
        get() = Vector4.vertical(
            a00 = row0.a2,
            a10 = row1.a2,
            a20 = row2.a2,
            a30 = row3.a2,
        )

    override val column3: Vector4x1
        get() = Vector4.vertical(
            a00 = row0.a3,
            a10 = row1.a3,
            a20 = row2.a3,
            a30 = row3.a3,
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
