package app.algebra.linear.matrices.matrix4

import app.algebra.NumericObject
import app.algebra.linear.vectors.vector4.Vector1x4
import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.linear.vectors.vector4.Vector4x1
import app.algebra.linear.VectorOrientation

class ColumnMajorMatrix4x4(
    private val data: SquareMatrix4Data<VectorOrientation.Vertical>,
) : EagerMatrix4x4() {
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
        get() = RowMajorMatrix4x4(
            data = data.interpretTransposed,
        )

    override val row0: Vector1x4
        get() = Vector4.horizontal(
            x = column0.x,
            y = column1.x,
            z = column2.x,
            w = column3.x,
        )

    override val row1: Vector1x4
        get() = Vector4.horizontal(
            x = column0.y,
            y = column1.y,
            z = column2.y,
            w = column3.y,
        )

    override val row2: Vector1x4
        get() = Vector4.horizontal(
            x = column0.z,
            y = column1.z,
            z = column2.z,
            w = column3.z,
        )

    override val row3: Vector1x4
        get() = Vector4.horizontal(
            x = column0.w,
            y = column1.w,
            z = column2.w,
            w = column3.w,
        )

    override val column0: Vector4x1
        get() = data.vector0

    override val column1: Vector4x1
        get() = data.vector1

    override val column2: Vector4x1
        get() = data.vector2

    override val column3: Vector4x1
        get() = data.vector3

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
