package app.algebra.linear

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class Matrix4x4Tests {
    @Test
    fun testTimes() {
        val matrixA = Matrix4x4(
            column0 = Vector4x1.of(1.0, 2.0, 3.0, 4.0),
            column1 = Vector4x1.of(5.0, 6.0, 7.0, 8.0),
            column2 = Vector4x1.of(9.0, 10.0, 11.0, 12.0),
            column3 = Vector4x1.of(13.0, 14.0, 15.0, 16.0)
        )

        val matrixB = Matrix4x4(
            column0 = Vector4x1.of(17.0, 18.0, 19.0, 20.0),
            column1 = Vector4x1.of(21.0, 22.0, 23.0, 24.0),
            column2 = Vector4x1.of(25.0, 26.0, 27.0, 28.0),
            column3 = Vector4x1.of(29.0, 30.0, 31.0, 32.0)
        )

        val expected = Matrix4x4(
            column0 = Vector4x1.of(538.0, 612.0, 686.0, 760.0),
            column1 = Vector4x1.of(650.0, 740.0, 830.0, 920.0),
            column2 = Vector4x1.of(762.0, 868.0, 974.0, 1080.0),
            column3 = Vector4x1.of(874.0, 996.0, 1118.0, 1240.0)
        )

        val result = matrixA * matrixB

        assertEquals(
            expected = expected,
            actual = result,
        )
    }

    @Test
    fun testLuDecompose() {
        val aMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(1.0, 22.0, 3.0, 4.0),
            row1 = Vector1x4.of(14.0, 6.0, 7.0, 8.0),
            row2 = Vector1x4.of(9.0, 10.0, 11.0, 12.0),
            row3 = Vector1x4.of(13.0, 14.0, 15.0, 16.0),
        )

        val lupDecomposition = assertNotNull(
            aMatrix.lupDecompose(),
        )

        val lMatrix = lupDecomposition.l

        assertEquals(
            expected = Vector1x4.of(1.0, 0.0, 0.0, 0.0),
            actual = lMatrix.row0
        )

        assertEquals(
            expected = Vector1x3.of(1.0, 0.0, 0.0),
            actual = lMatrix.row1.vectorYzw,
        )

        assertEquals(
            expected = Vector1x2.of(1.0, 0.0),
            actual = lMatrix.row2.vectorZw,
        )

        assertEquals(
            expected = 1.0,
            actual = lMatrix.row3.w,
        )

        val uMatrix = lupDecomposition.u

        assertEquals(
            expected = 0.0,
            actual = uMatrix.row1.x,
        )

        assertEquals(
            expected = Vector1x2.of(0.0, 0.0),
            actual = uMatrix.row2.vectorXy,
        )

        assertEquals(
            expected = Vector1x3.of(0.0, 0.0, 0.0),
            actual = uMatrix.row3.vectorXyz
        )

        val pMatrix = lupDecomposition.p

        assertEquals(
            expected = Matrix4x4.of(
                row0 = Vector1x4.of(0.0, 1.0, 0.0, 0.0),
                row1 = Vector1x4.of(0.0, 0.0, 0.0, 1.0),
                row2 = Vector1x4.of(1.0, 0.0, 0.0, 0.0),
                row3 = Vector1x4.of(0.0, 0.0, 1.0, 0.0),
            ),
            actual = pMatrix,
        )

        val paMatrix = pMatrix * aMatrix
        val luMatrix = lMatrix * uMatrix

        assertEquals(
            expected = paMatrix,
            actual = luMatrix,
        )
    }
}
