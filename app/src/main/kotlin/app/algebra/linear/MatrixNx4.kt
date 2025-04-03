package app.algebra.linear

import app.fillFrom
import org.ujmp.core.Matrix

data class MatrixNx4(
    val rows: List<Vector4>,
) {
    init {
        require(rows.isNotEmpty())
    }

    val column0: VectorN
        get() = VectorN(
            elements = rows.map { it.x },
        )

    val column1: VectorN
        get() = VectorN(
            elements = rows.map { it.y },
        )

    val column2: VectorN
        get() = VectorN(
            elements = rows.map { it.z },
        )

    val column3: VectorN
        get() = VectorN(
            elements = rows.map { it.w },
        )

    val height: Int
        get() = rows.size

    operator fun times(
        other: MatrixNx4,
    ): MatrixNx4 = MatrixNx4(
        rows = rows.map { row ->
            Vector4(
                x = row.dot(other.rows[0]),
                y = row.dot(other.rows[1]),
                z = row.dot(other.rows[2]),
                w = row.dot(other.rows[3]),
            )
        },
    )

    /**
     * Multiplies this matrix by another matrix
     *
     * @param other - the other matrix to multiply (interpreted as a transposed matrix)
     */
    fun timesTransposed(
        other: MatrixNx4,
    ): Matrix4x4 {

        require(rows.size == other.rows.size) {
            "MatrixNx4: Cannot multiply matrices of different heights"
        }

        return Matrix4x4(
            row0 = Vector4(
                x = column0.dot(other.column0),
                y = column0.dot(other.column1),
                z = column0.dot(other.column2),
                w = column0.dot(other.column3),
            ),
            row1 = Vector4(
                x = column1.dot(other.column0),
                y = column1.dot(other.column1),
                z = column1.dot(other.column2),
                w = column1.dot(other.column3),
            ),
            row2 = Vector4(
                x = column2.dot(other.column0),
                y = column2.dot(other.column1),
                z = column2.dot(other.column2),
                w = column2.dot(other.column3),
            ),
            row3 = Vector4(
                x = column3.dot(other.column0),
                y = column3.dot(other.column1),
                z = column3.dot(other.column2),
                w = column3.dot(other.column3),
            ),
        )
    }

    fun toUjmpMatrix(): Matrix = Matrix.Factory.fillFrom(
        collection = rows,
        rowWidth = 4,
    ) { row ->
        row.toArray()
    }
}
