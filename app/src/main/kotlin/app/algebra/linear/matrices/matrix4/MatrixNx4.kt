package app.algebra.linear.matrices.matrix4

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.Vector1x4
import app.algebra.linear.VectorNx1
import app.algebra.linear.VectorOrientation

class MatrixNx4(
    private val data: RectangularMatrix4Data<VectorOrientation.Horizontal>,
) : NumericObject {
    val rows: List<Vector1x4>
        get() = data.vectors

    val column0: VectorNx1
        get() = VectorNx1(
            xs = rows.map { it.x },
        )

    val column1: VectorNx1
        get() = VectorNx1(
            xs = rows.map { it.y },
        )

    val column2: VectorNx1
        get() = VectorNx1(
            xs = rows.map { it.z },
        )

    val column3: VectorNx1
        get() = VectorNx1(
            xs = rows.map { it.w },
        )

    val height: Int
        get() = rows.size

    val transposed: Matrix4xN
        get() = Matrix4xN(
            data = data.interpretTransposed,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when (other) {
        !is MatrixNx4 -> false
        else -> rows.equalsWithTolerance(other.rows, absoluteTolerance = absoluteTolerance)
    }
}
