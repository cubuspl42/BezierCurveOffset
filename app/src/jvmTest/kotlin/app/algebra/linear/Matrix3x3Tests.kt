package app.algebra.linear

import app.algebra.linear.matrices.matrix3.Matrix3x3
import app.algebra.linear.vectors.vector3.Vector1x3
import app.algebra.linear.vectors.vector3.Vector3
import kotlin.test.Test
import kotlin.test.assertEquals

class Matrix3x3Tests {
    @Test
    fun testDeterminant1() {
        val matrix = Matrix3x3.rowMajor(
            row0 = Vector3.horizontal(1.0, 2.0, 3.0),
            row1 = Vector3.horizontal(4.0, 5.0, 6.0),
            row2 = Vector3.horizontal(7.0, 8.0, 9.0),
        )

        val determinant = matrix.determinant

        assertEquals(
            expected = 0.0,
            actual = determinant,
        )
    }

    @Test
    fun testDeterminant2() {
        val matrix = Matrix3x3.rowMajor(
            row0 = Vector1x3(7.0, -7.0, 2.0),
            row1 = Vector1x3(-7.0, -5.0, -11.0),
            row2 = Vector1x3(2.0, -11.0, 13.0),
        )

        val determinant = matrix.determinant

        assertEquals(
            expected = -1611.0,
            actual = determinant,
        )
    }

    @Test
    fun testTimesSquareRm() {
        val aMatrix = Matrix3x3.rowMajor(
            row0 = Vector3.horizontal(1.0, 2.0, 3.0),
            row1 = Vector3.horizontal(5.0, 6.0, 7.0),
            row2 = Vector3.horizontal(9.0, 10.0, 11.0),
        )

        val bMatrix = Matrix3x3.rowMajor(
            row0 = Vector3.horizontal(-2.0, -5.0, 12.0),
            row1 = Vector3.horizontal(3.0, 4.0, -1.0),
            row2 = Vector3.horizontal(6.0, 7.0, 8.0),
        )

        val cMatrixExpected = Matrix3x3.rowMajor(
            row0 = Vector3.horizontal(22.0, 24.0, 34.0),
            row1 = Vector3.horizontal(50.0, 48.0, 110.0),
            row2 = Vector3.horizontal(78.0, 72.0, 186.0),
        )

        val cMatrix = aMatrix * bMatrix

        assertEquals(
            expected = cMatrixExpected,
            actual = cMatrix,
        )
    }

    @Test
    fun testTimesSquareCm() {
        val aMatrix = Matrix3x3.rowMajor(
            row0 = Vector3.horizontal(1.0, 2.0, 3.0),
            row1 = Vector3.horizontal(5.0, 5.0, 7.0),
            row2 = Vector3.horizontal(9.0, 10.0, 111.0),
        )

        val bMatrix = Matrix3x3.columnMajor(
            column0 = Vector3.vertical(-2.0, 3.0, 6.0),
            column1 = Vector3.vertical(-5.0, 4.0, 7.0),
            column2 = Vector3.vertical(12.0, -1.0, 8.0),
        )

        val cMatrixExpected = Matrix3x3.rowMajor(
            row0 = Vector3.horizontal(22.0, 24.0, 34.0),
            row1 = Vector3.horizontal(47.0, 44.0, 111.0),
            row2 = Vector3.horizontal(678.0, 772.0, 986.0),
        )

        val cMatrix = aMatrix * bMatrix

        assertEquals(
            expected = cMatrixExpected,
            actual = cMatrix,
        )
    }

}
