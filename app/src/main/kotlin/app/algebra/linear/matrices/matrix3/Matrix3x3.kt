package app.algebra.linear.matrices.matrix3

import app.algebra.NumericObject
import app.algebra.linear.vectors.vector3.Vector1x3
import app.algebra.linear.vectors.vector3.Vector3
import app.algebra.linear.vectors.vector3.Vector3x1
import app.algebra.linear.vectors.vector3.dot
import app.algebra.linear.vectors.vector3.times

sealed class Matrix3x3 : NumericObject {
    companion object {
        val zero = rowMajor(
            row0 = Vector3.horizontal(0.0, 0.0, 0.0),
            row1 = Vector3.horizontal(0.0, 0.0, 0.0),
            row2 = Vector3.horizontal(0.0, 0.0, 0.0),
        )

        val identity = rowMajor(
            row0 = Vector3.horizontal(1.0, 0.0, 0.0),
            row1 = Vector3.horizontal(0.0, 1.0, 0.0),
            row2 = Vector3.horizontal(0.0, 0.0, 1.0),
        )

        fun rowMajor(
            row0: Vector1x3,
            row1: Vector1x3,
            row2: Vector1x3,
        ): RowMajorMatrix3x3 = RowMajorMatrix3x3(
            data = SquareMatrix3Data(
                vector0 = row0,
                vector1 = row1,
                vector2 = row2,
            ),
        )

        fun columnMajor(
            column0: Vector3x1,
            column1: Vector3x1,
            column2: Vector3x1,
        ): ColumnMajorMatrix3x3 = ColumnMajorMatrix3x3(
            data = SquareMatrix3Data(
                vector0 = column0,
                vector1 = column1,
                vector2 = column2,
            ),
        )
    }

    final override fun equals(other: Any?): Boolean {
        return equalsWithTolerance(
            other = other as? NumericObject ?: return false,
            absoluteTolerance = 0.0,
        )
    }

    final override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }

    override fun toString(): String = """
        
        |${row0.a0} ${row0.a1} ${row0.a2}|
        |${row1.a0} ${row1.a1} ${row1.a2}|
        |${row2.a0} ${row2.a1} ${row2.a2}|
        
    """.trimIndent()

    protected fun equalsWithToleranceRowWise(
        other: Matrix3x3, absoluteTolerance: Double
    ): Boolean = when {
        !row0.equalsWithTolerance(other.row0, absoluteTolerance = absoluteTolerance) -> false
        !row1.equalsWithTolerance(other.row1, absoluteTolerance = absoluteTolerance) -> false
        !row2.equalsWithTolerance(other.row2, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }

    abstract val transposed: Matrix3x3

    operator fun times(
        vector: Vector3x1,
    ): Vector3x1 = Vector3(
        a0 = row0.dot(vector),
        a1 = row1.dot(vector),
        a2 = row2.dot(vector),
    )

    @JvmName("timesRm")
    operator fun times(
        other: Matrix3x3,
    ): RowMajorMatrix3x3 = rowMajor(
        row0 = row0 * other,
        row1 = row1 * other,
        row2 = row2 * other,
    )

    val determinant: Double
        get() {
            val a = row0.a0
            val b = row0.a1
            val c = row0.a2
            val d = row1.a0
            val e = row1.a1
            val f = row1.a2
            val g = row2.a0
            val h = row2.a1
            val i = row2.a2

            return a * (e * i - f * h) - b * (d * i - f * g) + c * (d * h - e * g)
        }

    abstract val row0: Vector1x3
    abstract val row1: Vector1x3
    abstract val row2: Vector1x3

    abstract val column0: Vector3x1
    abstract val column1: Vector3x1
    abstract val column2: Vector3x1
}
