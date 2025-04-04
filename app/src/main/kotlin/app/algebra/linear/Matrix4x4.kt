package app.algebra.linear

import app.fillByColumn
import org.ujmp.core.Matrix

data class Matrix4x4(
    val column0: Vector4x1,
    val column1: Vector4x1,
    val column2: Vector4x1,
    val column3: Vector4x1,
) {
    data class LuDecomposition(
        val l: Matrix4x4,
        val u: Matrix4x4,
    )

    data class LupDecomposition(
        val l: Matrix4x4,
        val u: Matrix4x4,
        val p: Matrix4x4,
    )

    companion object {
        val identity: Matrix4x4 = Matrix4x4(
            column0 = Vector4x1.of(1.0, 0.0, 0.0, 0.0),
            column1 = Vector4x1.of(0.0, 1.0, 0.0, 0.0),
            column2 = Vector4x1.of(0.0, 0.0, 1.0, 0.0),
            column3 = Vector4x1.of(0.0, 0.0, 0.0, 1.0),
        )

        fun of(
            row0: Vector1x4,
            row1: Vector1x4,
            row2: Vector1x4,
            row3: Vector1x4,
        ): Matrix4x4 = Matrix4x4(
            column0 = Vector4x1.of(
                x = row0.x,
                y = row1.x,
                z = row2.x,
                w = row3.x,
            ),
            column1 = Vector4x1.of(
                x = row0.y,
                y = row1.y,
                z = row2.y,
                w = row3.y,
            ),
            column2 = Vector4x1.of(
                x = row0.z,
                y = row1.z,
                z = row2.z,
                w = row3.z,
            ),
            column3 = Vector4x1.of(
                x = row0.w,
                y = row1.w,
                z = row2.w,
                w = row3.w,
            ),
        )
    }

    val row0: Vector1x4
        get() = Vector1x4.of(
            x = column0.x,
            y = column1.x,
            z = column2.x,
            w = column3.x,
        )

    val row1: Vector1x4
        get() = Vector1x4.of(
            x = column0.y,
            y = column1.y,
            z = column2.y,
            w = column3.y,
        )

    val row2: Vector1x4
        get() = Vector1x4.of(
            x = column0.z,
            y = column1.z,
            z = column2.z,
            w = column3.z,
        )

    val row3: Vector1x4
        get() = Vector1x4.of(
            x = column0.w,
            y = column1.w,
            z = column2.w,
            w = column3.w,
        )

    val rows: List<Vector1x4>
        get() = listOf(
            row0,
            row1,
            row2,
            row3,
        )

    operator fun get(
        i: Int,
    ): Vector1x4 = when (i) {
        0 -> row0
        1 -> row1
        2 -> row2
        3 -> row3
        else -> throw IllegalArgumentException("Invalid column index: $i")
    }

    operator fun times(
        other: Matrix4x4,
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

    fun swapRows(
        i0: Int,
        i1: Int,
    ): Matrix4x4 {
        require(i0 in 0 until 4) { "i0 must be between 0 and 3" }
        require(i1 in 0 until 4) { "i1 must be between 0 and 3" }

        return Matrix4x4.of(
            row0 = when {
                i0 == 0 -> rows[i1]
                i1 == 0 -> rows[i0]
                else -> row0
            },
            row1 = when {
                i0 == 1 -> rows[i1]
                i1 == 1 -> rows[i0]
                else -> row1
            },
            row2 = when {
                i0 == 2 -> rows[i1]
                i1 == 2 -> rows[i0]
                else -> row2
            },
            row3 = when {
                i0 == 3 -> rows[i1]
                i1 == 3 -> rows[i0]
                else -> row3
            },
        )
    }

    fun maxRowIndexBy(
        i0: Int,
        selector: (row: Vector1x4) -> Double,
    ): Int {
        require(i0 in 0 until 4) { "i0 must be between 0 and 3" }
        return rows.withIndex().drop(i0).maxBy { (_, row) -> selector(row) }.index
    }

    fun invertByBackSubstitution(): Matrix4x4 = Matrix4x4.of(
        row0 = Vector1x4.of(
            x = 1.0 / this[0][0],
            y = -this[0][1] / (this[0][0] * this[1][1]),
            z = (this[0][1] * this[1][2] - this[0][2] * this[1][1]) / (this[0][0] * this[1][1] * this[2][2]),
            w = (this[0][3] * this[2][2] * this[1][1] - this[0][2] * this[2][3] * this[1][1] - this[0][1] * (this[1][2] * this[2][3] - this[1][3] * this[2][2])) / (this[0][0] * this[1][1] * this[2][2] * this[3][3]),
        ),
        row1 = Vector1x4.of(
            x = 0.0,
            y = 1.0 / this[1][1],
            z = -this[1][2] / (this[1][1] * this[2][2]),
            w = (this[1][2] * this[2][3] - this[1][3] * this[2][2]) / (this[1][1] * this[2][2] * this[3][3]),
        ),
        row2 = Vector1x4.of(
            x = 0.0,
            y = 0.0,
            z = 1.0 / this[2][2],
            w = -this[2][3] / (this[2][2] * this[3][3]),
        ),
        row3 = Vector1x4.of(
            x = 0.0,
            y = 0.0,
            z = 0.0,
            w = 1.0 / this[3][3],
        ),
    )

    //     // Assuming a 4x4 matrix, indexing from 0
    //    val l11 = l[0][0]
    //    val l21 = l[1][0]
    //    val l22 = l[1][1]
    //    val l31 = l[2][0]
    //    val l32 = l[2][1]
    //    val l33 = l[2][2]
    //    val l41 = l[3][0]
    //    val l42 = l[3][1]
    //    val l43 = l[3][2]
    //    val l44 = l[3][3]
    //
    //    // Column 1
    //    lInv[0][0] = 1.0 / l11
    //    lInv[1][0] = -l21 / (l11 * l22)
    //    lInv[2][0] = -l31 / (l11 * l33) + (l21 * l32) / (l11 * l22 * l33)
    //    lInv[3][0] = -l41 / (l11 * l44) + (l21 * l42) / (l11 * l22 * l44) + (l31 * l43) / (l11 * l33 * l44) - (l21 * l32 * l43) / (l11 * l22 * l33 * l44)
    //
    //    // Column 2
    //    lInv[1][1] = 1.0 / l22
    //    lInv[2][1] = -l32 / (l22 * l33)
    //    lInv[3][1] = -l42 / (l22 * l44) + (l32 * l43) / (l22 * l33 * l44)
    //
    //    // Column 3
    //    lInv[2][2] = 1.0 / l33
    //    lInv[3][2] = -l43 / (l33 * l44)
    //
    //    // Column 4
    //    lInv[3][3] = 1.0 / l44
    fun invertByForwardSubstitution(): Matrix4x4 = Matrix4x4.of(
        row0 = Vector1x4.of(
            x = 1.0 / this[0][0],
            y = -this[0][1] / (this[0][0] * this[1][1]),
            z = (this[0][1] * this[1][2] - this[0][2] * this[1][1]) / (this[0][0] * this[1][1] * this[2][2]),
            w = (this[0][3] * this[2][2] * this[1][1] - this[0][2] * this[2][3] * this[1][1] - this[0][1] * (this[1][2] * this[2][3] - this[1][3] * this[2][2])) / (this[0][0] * this[1][1] * this[2][2] * this[3][3]),
        ),
        row1 = Vector1x4.of(
            x = 0.0,
            y = 1.0 / this[1][1],
            z = -this[1][2] / (this[1][1] * this[2][2]),
            w = (this[1][2] * this[2][3] - this[1][3] * this[2][2]) / (this[1][1] * this[2][2] * this[3][3]),
        ),
        row2 = Vector1x4.of(
            x = 0.0,
            y = 0.0,
            z = 1.0 / this[2][2],
            w = -this[2][3] / (this[2][2] * this[3][3]),
        ),
        row3 = Vector1x4.of(
            x = 0.0,
            y = 0.0,
            z = 0.0,
            w = 1.0 / this[3][3],
        ),
    )

    fun pivotize(): Matrix4x4 {
        val p0 = Matrix4x4.identity

        // The index of max row for column 0
        val iMax0 = this.maxRowIndexBy(i0 = 0) { it[0] }
        val p1 = p0.swapRows(i0 = 0, i1 = iMax0)

        // The index of max row for column 1
        val iMax1 = this.maxRowIndexBy(i0 = 1) { it[1] }
        val p2 = p1.swapRows(i0 = 1, i1 = iMax1)

        // The index of max row for column 2
        val iMax2 = this.maxRowIndexBy(i0 = 2) { it[2] }
        val p3 = p2.swapRows(i0 = 2, i1 = iMax2)

        // (Nothing to do for the bottom-right corner)

        return p3
    }

    fun lupDecompose(): LupDecomposition? {
        val pMatrix = pivotize()
        val paMatrix = pMatrix * this
        val (lMatrix, uMatrix) = paMatrix.luDecompose() ?: return null

        return LupDecomposition(
            l = lMatrix,
            u = uMatrix,
            p = pMatrix,
        )
    }

    internal fun luDecompose(): LuDecomposition? {
        val u11 = this[0][0]
        val u12 = this[0][1]
        val u13 = this[0][2]
        val u14 = this[0][3]

        if (u11 == 0.0) {
            return null
        }

        val l21 = this[1][0] / u11
        val l31 = this[2][0] / u11
        val l41 = this[3][0] / u11

        val u22 = this[1][1] - u12 * l21
        val u23 = this[1][2] - u13 * l21
        val u24 = this[1][3] - u14 * l21

        if (u22 == 0.0) {
            return null
        }

        val l32 = (this[2][1] - u12 * l31) / u22
        val l42 = (this[3][1] - u12 * l41) / u22

        val u33 = this[2][2] - (u13 * l31 + u23 * l32)
        val u34 = this[2][3] - (u14 * l31 + u24 * l32)

        if (u33 == 0.0) {
            return null
        }

        val l43 = (this[3][2] - u13 * l41 - u23 * l42) / u33

        val u44 = this[3][3] - (u14 * l41 + u24 * l42 + u34 * l43)

        val l = Matrix4x4.of(
            row0 = Vector1x4.of(1.0, 0.0, 0.0, 0.0),
            row1 = Vector1x4.of(l21, 1.0, 0.0, 0.0),
            row2 = Vector1x4.of(l31, l32, 1.0, 0.0),
            row3 = Vector1x4.of(l41, l42, l43, 1.0),
        )

        val u = Matrix4x4.of(
            row0 = Vector1x4.of(u11, u12, u13, u14),
            row1 = Vector1x4.of(0.0, u22, u23, u24),
            row2 = Vector1x4.of(0.0, 0.0, u33, u34),
            row3 = Vector1x4.of(0.0, 0.0, 0.0, u44),
        )

        return LuDecomposition(
            l = l,
            u = u,
        )
    }

    fun toUjmpMatrix(): Matrix = Matrix.Factory.fillByColumn(
        columnElements = listOf(
            column0,
            column1,
            column2,
            column3,
        ),
        columnHeight = 4,
        buildColumn = { column ->
            column.toArray()
        },
    )
}
