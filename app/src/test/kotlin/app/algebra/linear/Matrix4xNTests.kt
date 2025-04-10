package app.algebra.linear

import app.algebra.assertEqualsWithTolerance
import app.algebra.linear.matrices.matrix4.Matrix4x4
import app.algebra.linear.matrices.matrix4.RectangularMatrix4
import app.algebra.linear.matrices.matrix4.times
import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.linear.vectors.vectorN.VectorN
import kotlin.test.Test
import kotlin.test.assertEquals

class Matrix4xNTests {
    @Test
    fun testTimesMatrix() {
        val matrixA = RectangularMatrix4.horizontal(
            columns = listOf(
                Vector4.vertical(1.0, 2.0, 3.0, 4.0),
                Vector4.vertical(5.0, 6.0, 7.0, 8.0),
                Vector4.vertical(9.0, 10.0, 11.0, 12.0),
                Vector4.vertical(13.0, 14.0, 15.0, 16.0),
                Vector4.vertical(17.0, 18.0, 19.0, 20.0),
                Vector4.vertical(21.0, 22.0, 23.0, 24.0),
            )
        )

        val matrixB = RectangularMatrix4.vertical(
            rows = listOf(
                Vector4.horizontal(17.0, 18.0, 19.0, 20.0),
                Vector4.horizontal(21.0, 22.0, 23.0, 24.0),
                Vector4.horizontal(25.0, 26.0, 27.0, 28.0),
                Vector4.horizontal(29.0, 30.0, 31.0, 32.0),
                Vector4.horizontal(33.0, 34.0, 35.0, 36.0),
                Vector4.horizontal(37.0, 38.0, 39.0, 40.0),
            ),
        )

        val expected = Matrix4x4.columnMajor(
            column0 = Vector4.vertical(2062.0, 2224.0, 2386.0, 2548.0),
            column1 = Vector4.vertical(2128.0, 2296.0, 2464.0, 2632.0),
            column2 = Vector4.vertical(2194.0, 2368.0, 2542.0, 2716.0),
            column3 = Vector4.vertical(2260.0, 2440.0, 2620.0, 2800.0),
        )

        val result = matrixA * matrixB

        assertEquals(
            expected = expected,
            actual = result,
        )
    }

    @Test
    fun testTimesVector() {
        val matrixA = RectangularMatrix4.horizontal(
            columns = listOf(
                Vector4.vertical(1.0, 2.0, 3.0, 4.0),
                Vector4.vertical(5.0, 6.0, 7.0, 8.0),
                Vector4.vertical(9.0, 10.0, 11.0, 12.0),
                Vector4.vertical(13.0, 14.0, 15.0, 16.0),
                Vector4.vertical(17.0, 18.0, 19.0, 20.0),
                Vector4.vertical(21.0, 22.0, 23.0, 24.0),
            ),
        )

        val vectorB = VectorN.vertical(
            1.0,
            2.0,
            3.0,
            4.0,
            5.0,
            6.0,
        )

        val result = matrixA * vectorB

        assertEqualsWithTolerance(
            expected = Vector4.vertical(
                x = 301.0,
                y = 322.0,
                z = 343.0,
                w = 364.0,
            ),
            actual = result,
            absoluteTolerance = 0.01,
        )
    }
}
