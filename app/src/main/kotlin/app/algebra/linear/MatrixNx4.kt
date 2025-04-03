package app.algebra.linear

import app.fillByRow
import org.ujmp.core.Matrix

data class MatrixNx4(
    val rows: List<Vector1x4>,
) {
    init {
        require(rows.isNotEmpty())
    }

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
            columns = rows.map { it.transposed },
        )

    fun toUjmpMatrix(): Matrix = Matrix.Factory.fillByRow(
        rowElements = rows,
        rowWidth = 4,
    ) { row ->
        row.toArray()
    }
}

