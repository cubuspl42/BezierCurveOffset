package app.algebra.linear.matrices.matrix4

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsWithNoTolerance
import app.algebra.linear.vectors.vector4.Vector1x4
import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.linear.vectors.vector4.Vector4x1
import app.utils.iterable.indexOfMaxBy
import kotlin.jvm.JvmName
import kotlin.math.absoluteValue

sealed class Matrix4x4 : NumericObject {
    companion object {
        val zero = rowMajor(
            row0 = Vector4(0.0, 0.0, 0.0, 0.0),
            row1 = Vector4(0.0, 0.0, 0.0, 0.0),
            row2 = Vector4(0.0, 0.0, 0.0, 0.0),
            row3 = Vector4(0.0, 0.0, 0.0, 0.0),
        )

        val identity = rowMajor(
            row0 = Vector4(1.0, 0.0, 0.0, 0.0),
            row1 = Vector4(0.0, 1.0, 0.0, 0.0),
            row2 = Vector4(0.0, 0.0, 1.0, 0.0),
            row3 = Vector4(0.0, 0.0, 0.0, 1.0),
        )

        fun rowMajor(
            row0: Vector1x4,
            row1: Vector1x4,
            row2: Vector1x4,
            row3: Vector1x4,
        ): RowMajorMatrix4x4 = RowMajorMatrix4x4(
            data = SquareMatrix4Data(
                vector0 = row0,
                vector1 = row1,
                vector2 = row2,
                vector3 = row3,
            ),
        )

        fun columnMajor(
            column0: Vector4x1,
            column1: Vector4x1,
            column2: Vector4x1,
            column3: Vector4x1,
        ): ColumnMajorMatrix4x4 = ColumnMajorMatrix4x4(
            data = SquareMatrix4Data(
                vector0 = column0,
                vector1 = column1,
                vector2 = column2,
                vector3 = column3,
            ),
        )
    }

    internal data class LuDecomposition(
        val l: RowMajorMatrix4x4,
        val u: RowMajorMatrix4x4,
    )

    data class LupDecomposition(
        val l: RowMajorMatrix4x4,
        val u: RowMajorMatrix4x4,
        val p: RowMajorMatrix4x4,
    )

    final override fun equals(other: Any?): Boolean {
        return equalsWithNoTolerance(
            other = other as? NumericObject ?: return false,
        )
    }

    final override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }

    protected fun equalsWithToleranceRowWise(
        other: Matrix4x4,
        tolerance: Tolerance,
    ): Boolean = when {
        !row0.equalsWithTolerance(other.row0, tolerance = tolerance) -> false
        !row1.equalsWithTolerance(other.row1, tolerance = tolerance) -> false
        !row2.equalsWithTolerance(other.row2, tolerance = tolerance) -> false
        !row3.equalsWithTolerance(other.row3, tolerance = tolerance) -> false
        else -> true
    }

    abstract operator fun get(
        i: Int,
        j: Int,
    ): Double

    operator fun get(
        i: Int,
    ): Vector1x4 = when (i) {
        0 -> row0
        1 -> row1
        2 -> row2
        3 -> row3
        else -> throw IllegalArgumentException("Invalid column index: $i")
    }

    /** Checks if the matrix is upper triangular. */
    fun isUpperTriangular(): Boolean {
        val a21 = this[1, 0]
        val a31 = this[2, 0]
        val a32 = this[2, 1]
        val a41 = this[3, 0]
        val a42 = this[3, 1]
        val a43 = this[3, 2]
        return a21 == 0.0 && a31 == 0.0 && a32 == 0.0 && a41 == 0.0 && a42 == 0.0 && a43 == 0.0
    }

    /** Checks if the matrix is lower triangular. */
    fun isLowerTriangular(): Boolean {
        val a12 = this[0, 1]
        val a13 = this[0, 2]
        val a14 = this[0, 3]
        val a23 = this[1, 2]
        val a24 = this[1, 3]
        val a34 = this[2, 3]
        return a12 == 0.0 && a13 == 0.0 && a14 == 0.0 && a23 == 0.0 && a24 == 0.0 && a34 == 0.0
    }

    fun swapRows(
        i0: Int,
        i1: Int,
    ): RowMajorMatrix4x4 {
        require(i0 in 0 until 4) { "i0 must be between 0 and 3" }
        require(i1 in 0 until 4) { "i1 must be between 0 and 3" }

        return rowMajor(
            row0 = when {
                i0 == 0 -> this[i1]
                i1 == 0 -> this[i0]
                else -> row0
            },
            row1 = when {
                i0 == 1 -> this[i1]
                i1 == 1 -> this[i0]
                else -> row1
            },
            row2 = when {
                i0 == 2 -> this[i1]
                i1 == 2 -> this[i0]
                else -> row2
            },
            row3 = when {
                i0 == 3 -> this[i1]
                i1 == 3 -> this[i0]
                else -> row3
            },
        )
    }

    fun invert(): InvertedMatrix4x4? {
        val lupDecomposition = lupDecompose() ?: return null
        return InvertedMatrix4x4(
            lupDecomposition = lupDecomposition,
        )
    }

    /** Solves the system of equations Ax = y using backward substitution, where A is an upper triangular matrix. */
    fun solveByBackSubstitution(yVector: Vector4x1): Vector4x1 {
        require(isUpperTriangular()) { "Matrix is not upper triangular" }

        val y4 = yVector[3]
        val y3 = yVector[2]
        val y2 = yVector[1]
        val y1 = yVector[0]

        val a11 = this[0, 0]
        val a12 = this[0, 1]
        val a13 = this[0, 2]
        val a14 = this[0, 3]
        val a22 = this[1, 1]
        val a23 = this[1, 2]
        val a24 = this[1, 3]
        val a33 = this[2, 2]
        val a34 = this[2, 3]
        val a44 = this[3, 3]

        val x4 = y4 / a44
        val x3 = (y3 - a34 * x4) / a33
        val x2 = (y2 - a24 * x4 - a23 * x3) / a22
        val x1 = (y1 - a14 * x4 - a13 * x3 - a12 * x2) / a11

        return Vector4.vertical(
            a00 = x1,
            a10 = x2,
            a20 = x3,
            a30 = x4,
        )
    }

    /** Solves the equation AX = Y using backward substitution column-wise. */
    fun solveByBackSubstitution(yMatrix: Matrix4x4): ColumnMajorMatrix4x4 {
        require(isUpperTriangular()) { "Matrix is not upper triangular" }

        val xColumn0 = solveByBackSubstitution(yVector = yMatrix.column0)
        val xColumn1 = solveByBackSubstitution(yVector = yMatrix.column1)
        val xColumn2 = solveByBackSubstitution(yVector = yMatrix.column2)
        val xColumn3 = solveByBackSubstitution(yVector = yMatrix.column3)

        return columnMajor(
            column0 = xColumn0,
            column1 = xColumn1,
            column2 = xColumn2,
            column3 = xColumn3,
        )
    }

    /** Solves the system of equations Ax = y using forward substitution, where A is a lower triangular matrix. */
    fun solveByForwardSubstitution(yVector: Vector4x1): Vector4x1 {
        require(isLowerTriangular()) { "Matrix is not lower triangular" }

        val y4 = yVector[3]
        val y3 = yVector[2]
        val y2 = yVector[1]
        val y1 = yVector[0]

        val a11 = this[0, 0]
        val a21 = this[1, 0]
        val a22 = this[1, 1]
        val a31 = this[2, 0]
        val a32 = this[2, 1]
        val a33 = this[2, 2]
        val a41 = this[3, 0]
        val a42 = this[3, 1]
        val a43 = this[3, 2]
        val a44 = this[3, 3]

        val x1 = y1 / a11
        val x2 = (y2 - a21 * x1) / a22
        val x3 = (y3 - a31 * x1 - a32 * x2) / a33
        val x4 = (y4 - a41 * x1 - a42 * x2 - a43 * x3) / a44

        return Vector4.vertical(
            a00 = x1,
            a10 = x2,
            a20 = x3,
            a30 = x4,
        )
    }

    /** Solves the equation AX = Y using forward substitution column-wise. */
    fun solveByForwardSubstitution(yMatrix: Matrix4x4): ColumnMajorMatrix4x4 {
        require(isLowerTriangular()) { "Matrix is not lower triangular" }

        val xColumn0 = solveByForwardSubstitution(yVector = yMatrix.column0)
        val xColumn1 = solveByForwardSubstitution(yVector = yMatrix.column1)
        val xColumn2 = solveByForwardSubstitution(yVector = yMatrix.column2)
        val xColumn3 = solveByForwardSubstitution(yVector = yMatrix.column3)

        return columnMajor(
            column0 = xColumn0,
            column1 = xColumn1,
            column2 = xColumn2,
            column3 = xColumn3,
        )
    }

    /** Creates a pivot matrix for the current matrix. */
    fun pivotize(): RowMajorMatrix4x4 {
        val p0 = identity

        // The index of max row for column 0
        val iMax0 = column0.toList().indexOfMaxBy { it.absoluteValue }
        val p1 = p0.swapRows(i0 = 0, i1 = iMax0)

        // The index of max row for column 1
        val iMax1 = column1.toList().indexOfMaxBy(fromIndex = 1) { it.absoluteValue }
        val p2 = p1.swapRows(i0 = 1, i1 = iMax1)

        // The index of max row for column 2
        val iMax2 = column2.toList().indexOfMaxBy(fromIndex = 2) { it.absoluteValue }
        val p3 = p2.swapRows(i0 = 2, i1 = iMax2)

        // (Nothing to do for the bottom-right corner)

        return p3
    }

    /** Performs LUP decomposition on the current matrix. */
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

    /** Performs LU decomposition on the current matrix. */
    internal fun luDecompose(): LuDecomposition? {
        val u11 = this[0, 0]
        val u12 = this[0, 1]
        val u13 = this[0, 2]
        val u14 = this[0, 3]

        if (u11 == 0.0) {
            return null
        }

        val l21 = this[1, 0] / u11
        val l31 = this[2, 0] / u11
        val l41 = this[3, 0] / u11

        val u22 = this[1, 1] - u12 * l21
        val u23 = this[1, 2] - u13 * l21
        val u24 = this[1, 3] - u14 * l21

        if (u22 == 0.0) {
            return null
        }

        val l32 = (this[2, 1] - u12 * l31) / u22
        val l42 = (this[3, 1] - u12 * l41) / u22

        val u33 = this[2, 2] - (u13 * l31 + u23 * l32)
        val u34 = this[2, 3] - (u14 * l31 + u24 * l32)

        if (u33 == 0.0) {
            return null
        }

        val l43 = (this[3, 2] - u13 * l41 - u23 * l42) / u33

        val u44 = this[3, 3] - (u14 * l41 + u24 * l42 + u34 * l43)

        val l = rowMajor(
            row0 = Vector4.horizontal(1.0, 0.0, 0.0, 0.0),
            row1 = Vector4.horizontal(l21, 1.0, 0.0, 0.0),
            row2 = Vector4.horizontal(l31, l32, 1.0, 0.0),
            row3 = Vector4.horizontal(l41, l42, l43, 1.0),
        )

        val u = rowMajor(
            row0 = Vector4.horizontal(u11, u12, u13, u14),
            row1 = Vector4.horizontal(0.0, u22, u23, u24),
            row2 = Vector4.horizontal(0.0, 0.0, u33, u34),
            row3 = Vector4.horizontal(0.0, 0.0, 0.0, u44),
        )

        return LuDecomposition(
            l = l,
            u = u,
        )
    }

    abstract val transposed: Matrix4x4

    abstract operator fun times(
        vector: Vector4x1,
    ): Vector4x1

    abstract operator fun times(
        other: Matrix4x4,
    ): Matrix4x4

    @JvmName("timesRect")
    operator fun times(
        other: Matrix4xN,
    ): Matrix4xN = RectangularMatrix4.horizontal(
        columns = other.columns.map { column -> this * column },
    )

    abstract val row0: Vector1x4
    abstract val row1: Vector1x4
    abstract val row2: Vector1x4
    abstract val row3: Vector1x4

    abstract val column0: Vector4x1
    abstract val column1: Vector4x1
    abstract val column2: Vector4x1
    abstract val column3: Vector4x1
}
