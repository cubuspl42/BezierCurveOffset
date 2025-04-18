package app.algebra.linear.matrices.matrix4

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.vectors.vector4.Vector1x4
import app.algebra.linear.vectors.vectorN.VectorNx1
import app.algebra.linear.VectorOrientation

class MatrixNx4(
    private val data: RectangularMatrix4Data<VectorOrientation.Horizontal>,
) : NumericObject {
    val rows: List<Vector1x4>
        get() = data.vectors

    val column0: VectorNx1
        get() = VectorNx1(
            elements = rows.map { it.a0 },
        )

    val column1: VectorNx1
        get() = VectorNx1(
            elements = rows.map { it.a1 },
        )

    val column2: VectorNx1
        get() = VectorNx1(
            elements = rows.map { it.a2 },
        )

    val column3: VectorNx1
        get() = VectorNx1(
            elements = rows.map { it.a3 },
        )

    val height: Int
        get() = rows.size

    val transposed: Matrix4xN
        get() = Matrix4xN(
            data = data.interpretTransposed,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Double,
    ): Boolean = when (other) {
        !is MatrixNx4 -> false
        else -> rows.equalsWithTolerance(other.rows, tolerance = tolerance)
    }
}
