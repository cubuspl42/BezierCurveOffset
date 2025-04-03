package app.algebra.linear

import app.fillFrom
import org.ujmp.core.Matrix

data class MatrixNx4(
    val rows: List<Vector4x1>,
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
            Vector4x1.of(
                x = row.dot(other.rows[0]),
                y = row.dot(other.rows[1]),
                z = row.dot(other.rows[2]),
                w = row.dot(other.rows[3]),
            )
        },
    )

    fun toUjmpMatrix(): Matrix = Matrix.Factory.fillFrom(
        collection = rows,
        rowWidth = 4,
    ) { row ->
        row.toArray()
    }
}
