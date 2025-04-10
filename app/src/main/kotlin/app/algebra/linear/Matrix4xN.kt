package app.algebra.linear

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.fillByColumn
import org.ujmp.core.Matrix

data class Matrix4xN(
    val columns: List<Vector4x1>,
) : NumericObject {
    init {
        require(columns.isNotEmpty())
    }

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

    operator fun times(
        vector: VectorNx1,
    ): Vector4x1 = Vector4.vertical(
        x = row0.dot(vector),
        y = row1.dot(vector),
        z = row2.dot(vector),
        w = row3.dot(vector),
    )

    fun toUjmpMatrix(): Matrix = Matrix.Factory.fillByColumn(
        columnElements = columns,
        columnHeight = 4,
        buildColumn = { v -> v.toArray() },
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
): CmMatrix4x4 = Matrix4x4.columnMajor(
    column0 = this * other.column0,
    column1 = this * other.column1,
    column2 = this * other.column2,
    column3 = this * other.column3,
)
