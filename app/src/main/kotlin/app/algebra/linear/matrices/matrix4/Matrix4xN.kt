package app.algebra.linear.matrices.matrix4

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.vectors.vectorN.Vector1xN
import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.linear.vectors.vector4.Vector4x1
import app.algebra.linear.vectors.vectorN.VectorNx1
import app.algebra.linear.VectorOrientation
import app.algebra.linear.vectors.vector3.dot
import app.algebra.linear.vectors.vectorN.dot

class Matrix4xN(
    private val data: RectangularMatrix4Data<VectorOrientation.Vertical>,
) : NumericObject {
    val columns: List<Vector4x1>
        get() = data.vectors

    val row0: Vector1xN
        get() = Vector1xN(
            xs = columns.map { it.x },
        )

    val row1: Vector1xN
        get() = Vector1xN(
            xs = columns.map { it.y },
        )

    val row2: Vector1xN
        get() = Vector1xN(
            xs = columns.map { it.z },
        )

    val row3: Vector1xN
        get() = Vector1xN(
            xs = columns.map { it.w },
        )

    val width: Int
        get() = columns.size

    val transposed: MatrixNx4
        get() = MatrixNx4(
            data = data.interpretTransposed,
        )

    operator fun times(
        vector: VectorNx1,
    ): Vector4x1 = Vector4.vertical(
        x = row0.dot(vector),
        y = row1.dot(vector),
        z = row2.dot(vector),
        w = row3.dot(vector),
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when (other) {
        !is Matrix4xN -> false
        else -> columns.equalsWithTolerance(other.columns, absoluteTolerance = absoluteTolerance)
    }
}

operator fun Matrix4xN.times(
    other: MatrixNx4,
): ColumnMajorMatrix4x4 = Matrix4x4.columnMajor(
    column0 = this * other.column0,
    column1 = this * other.column1,
    column2 = this * other.column2,
    column3 = this * other.column3,
)
