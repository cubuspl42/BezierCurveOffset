package app.algebra.linear

import app.fillByColumn
import app.fillByRow
import org.ujmp.core.Matrix

data class Matrix4xN(
    val columns: List<Vector4x1>,
) {
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
        other: MatrixNx4,
    ): Matrix4x4 = Matrix4x4(
        column0 = Vector4x1.of(
            x = row0.dot(other.column0),
            y = row1.dot(other.column0),
            z = row2.dot(other.column0),
            w = row3.dot(other.column0),
        ),
        column1 = Vector4x1.of(
            x = row0.dot(other.column1),
            y = row1.dot(other.column1),
            z = row2.dot(other.column1),
            w = row3.dot(other.column1),
        ),
        column2 = Vector4x1.of(
            x = row0.dot(other.column2),
            y = row1.dot(other.column2),
            z = row2.dot(other.column2),
            w = row3.dot(other.column2),
        ),
        column3 = Vector4x1.of(
            x = row0.dot(other.column3),
            y = row1.dot(other.column3),
            z = row2.dot(other.column3),
            w = row3.dot(other.column3),
        ),
    )

    fun toUjmpMatrix(): Matrix = Matrix.Factory.fillByColumn(
        columnElements = columns,
        columnHeight = 4,
        buildColumn = { v -> v.toArray() },
    )
}
