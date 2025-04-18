package app.algebra.linear.matrices.matrix4

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
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
            0 -> column0.a0
            1 -> column1.a0
            2 -> column2.a0
            3 -> column3.a0
            else -> throw IndexOutOfBoundsException("Index out of bounds: $i, $j")
        }

        1 -> when (j) {
            0 -> column0.a1
            1 -> column1.a1
            2 -> column2.a1
            3 -> column3.a1
            else -> throw IndexOutOfBoundsException("Index out of bounds: $i, $j")
        }

        2 -> when (j) {
            0 -> column0.a2
            1 -> column1.a2
            2 -> column2.a2
            3 -> column3.a2
            else -> throw IndexOutOfBoundsException("Index out of bounds: $i, $j")
        }

        3 -> when (j) {
            0 -> column0.a3
            1 -> column1.a3
            2 -> column2.a3
            3 -> column3.a3
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
            a00 = column0.a0,
            a01 = column1.a0,
            a02 = column2.a0,
            a03 = column3.a0,
        )

    override val row1: Vector1x4
        get() = Vector4.horizontal(
            a00 = column0.a1,
            a01 = column1.a1,
            a02 = column2.a1,
            a03 = column3.a1,
        )

    override val row2: Vector1x4
        get() = Vector4.horizontal(
            a00 = column0.a2,
            a01 = column1.a2,
            a02 = column2.a2,
            a03 = column3.a2,
        )

    override val row3: Vector1x4
        get() = Vector4.horizontal(
            a00 = column0.a3,
            a01 = column1.a3,
            a02 = column2.a3,
            a03 = column3.a3,
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
        tolerance: Tolerance,
    ): Boolean = when {
        other is Matrix4x4 -> when {
            other is ColumnMajorMatrix4x4 -> when {
                !column0.equalsWithTolerance(other.column0, tolerance = tolerance) -> false
                !column1.equalsWithTolerance(other.column1, tolerance = tolerance) -> false
                !column2.equalsWithTolerance(other.column2, tolerance = tolerance) -> false
                !column3.equalsWithTolerance(other.column3, tolerance = tolerance) -> false
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
