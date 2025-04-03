package app.algebra.linear

import kotlin.test.Test
import kotlin.test.assertEquals

class Matrix4xNTests {
    @Test
    fun testTimes() {
        val matrixA = Matrix4xN(
            columns = listOf(
                Vector4x1.of(1.0, 2.0, 3.0, 4.0),
                Vector4x1.of(5.0, 6.0, 7.0, 8.0),
                Vector4x1.of(9.0, 10.0, 11.0, 12.0),
                Vector4x1.of(13.0, 14.0, 15.0, 16.0),
                Vector4x1.of(17.0, 18.0, 19.0, 20.0),
                Vector4x1.of(21.0, 22.0, 23.0, 24.0),
            )
        )

        val matrixB = MatrixNx4(
            rows = listOf(
                Vector1x4.of(17.0, 18.0, 19.0, 20.0),
                Vector1x4.of(21.0, 22.0, 23.0, 24.0),
                Vector1x4.of(25.0, 26.0, 27.0, 28.0),
                Vector1x4.of(29.0, 30.0, 31.0, 32.0),
                Vector1x4.of(33.0, 34.0, 35.0, 36.0),
                Vector1x4.of(37.0, 38.0, 39.0, 40.0),
            ),
        )

        val expected = Matrix4x4(
            column0 = Vector4x1.of(2062.0, 2224.0, 2386.0, 2548.0),
            column1 = Vector4x1.of(2128.0, 2296.0, 2464.0, 2632.0),
            column2 = Vector4x1.of(2194.0, 2368.0, 2542.0, 2716.0),
            column3 = Vector4x1.of(2260.0, 2440.0, 2620.0, 2800.0),
        )

        val result = matrixA * matrixB

        assertEquals(
            expected = expected,
            actual = result,
        )
    }
}
