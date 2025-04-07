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

    @Test
    fun testSolveByBackSubstitutionVector() {
        val aMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(2.0, 3.0, 1.0, 4.0),
            row1 = Vector1x4.of(0.0, 5.0, 2.0, 1.0),
            row2 = Vector1x4.of(0.0, 0.0, 3.0, 6.0),
            row3 = Vector1x4.of(0.0, 0.0, 0.0, 7.0),
        )

        val yVector = Vector4x1.of(1.0, 2.0, 3.0, 4.0)

        val xVector = aMatrix.solveByBackSubstitution(
            yVector = yVector,
        )

        assertEquals(
            expected = Vector4x1.of(
                x = -1.0857,
                y = 0.3429,
                z = -0.1429,
                w = 0.5714,
            ),
            actual = xVector,
            absoluteTolerance = 0.001,
        )
    }

    @Test
    fun testSolveByBackSubstitutionMatrix() {
        val aMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(2.0, 3.0, 1.0, 4.0),
            row1 = Vector1x4.of(0.0, 5.0, 2.0, 1.0),
            row2 = Vector1x4.of(0.0, 0.0, 3.0, 6.0),
            row3 = Vector1x4.of(0.0, 0.0, 0.0, 7.0),
        )

        val yMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(1.0, 2.0, 3.0, 4.0),
            row1 = Vector1x4.of(5.0, 6.0, 7.0, 8.0),
            row2 = Vector1x4.of(9.0, 10.0, 11.0, 12.0),
            row3 = Vector1x4.of(13.0, 14.0, 15.0, 16.0),
        )

        val xMatrix = aMatrix.solveByBackSubstitution(
            yMatrix = yMatrix,
        )

        assertEquals(
            expected = Matrix4x4.of(
                row0 = Vector1x4.of(-4.2286, -4.2667, -4.3048, -4.3429),
                row1 = Vector1x4.of(0.9143, 1.0667, 1.2190, 1.3714),
                row2 = Vector1x4.of(-0.7143, -0.6667, -0.6190, -0.5714),
                row3 = Vector1x4.of(1.8571, 2.0000, 2.1429, 2.2857),
            ),
            actual = xMatrix,
            absoluteTolerance = 0.001,
        )
    }

    @Test
    fun testSolveByForwardSubstitutionVector() {
        val aMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(7.0, 0.0, 0.0, 0.0),
            row1 = Vector1x4.of(3.0, 6.0, 0.0, 0.0),
            row2 = Vector1x4.of(5.0, 2.0, 1.0, 0.0),
            row3 = Vector1x4.of(2.0, 3.0, 1.0, 4.0),
        )

        val yVector = Vector4x1.of(1.0, 2.0, 3.0, 4.0)

        val xVector = aMatrix.solveByForwardSubstitution(
            yVector = yVector,
        )

        assertEquals(
            expected = Vector4x1.of(
                x = 0.1429,
                y = 0.2619,
                z = 1.7619,
                w = 0.2917,
            ),
            actual = xVector,
            absoluteTolerance = 0.001,
        )
    }

    @Test
    fun testSolveByForwardSubstitutionMatrix() {
        val aMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(7.0, 0.0, 0.0, 0.0),
            row1 = Vector1x4.of(3.0, 6.0, 0.0, 0.0),
            row2 = Vector1x4.of(5.0, 2.0, 1.0, 0.0),
            row3 = Vector1x4.of(2.0, 3.0, 1.0, 4.0),
        )

        val yMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(1.0, 2.0, 3.0, 4.0),
            row1 = Vector1x4.of(5.0, 6.0, 7.0, 8.0),
            row2 = Vector1x4.of(9.0, 10.0, 11.0, 12.0),
            row3 = Vector1x4.of(13.0, 14.0, 15.0, 16.0),
        )

        val xMatrix = aMatrix.solveByForwardSubstitution(
            yMatrix = yMatrix,
        )

        assertEquals(
            expected = Matrix4x4.of(
                row0 = Vector1x4.of(0.1429, 0.2857, 0.4286, 0.5714),
                row1 = Vector1x4.of(0.7619, 0.8571, 0.9524, 1.0476),
                row2 = Vector1x4.of(6.7619, 6.8571, 6.9524, 7.0476),
                row3 = Vector1x4.of(0.9167, 1.0000, 1.0833, 1.1667),
            ),
            actual = xMatrix,
            absoluteTolerance = 0.001,
        )
    }

    @Test
    fun testInvertTimes() {
        val aMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(7.0, 3.3, 3.0, 2.5),
            row1 = Vector1x4.of(3.0, 6.0, 0.0, 5.0),
            row2 = Vector1x4.of(5.0, 2.0, -1.0, 0.0),
            row3 = Vector1x4.of(2.0, 3.0, 1.0, 4.0),
        )

        val bMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(1.0, 2.0, 3.0, 4.0),
            row1 = Vector1x4.of(5.5, 6.0, -7.0, 8.0),
            row2 = Vector1x4.of(9.0, -10.0, 11.0, 12.0),
            row3 = Vector1x4.of(13.0, 14.0, 15.0, 16.0),
        )

        val aMatrixInverted = assertNotNull(aMatrix.invert())

        val cMatrix = aMatrixInverted * bMatrix

        assertEquals(
            expected = Matrix4x4.of(
                row0 = Vector1x4.of(4.2536, 0.0466, 7.6841, 5.1801),
                row1 = Vector1x4.of(-9.8332, -5.0723, -18.7341, -11.1410),
                row2 = Vector1x4.of(-7.3983, 0.0884, -10.0470, -8.3826),
                row3 = Vector1x4.of(10.3480, 7.2588, 16.4700, 11.8620),
            ),
            actual = cMatrix,
            absoluteTolerance = 0.001,
        )
    }

    @Test
    fun testInvertCalculate() {
        val aMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(7.0, 3.3, 3.0, 2.5),
            row1 = Vector1x4.of(3.0, 6.0, 0.0, 5.0),
            row2 = Vector1x4.of(5.0, 2.0, -1.0, 0.0),
            row3 = Vector1x4.of(2.0, 3.0, 1.0, 4.0),
        )

        val aMatrixInverted = assertNotNull(aMatrix.invert())

        val bMatrix = aMatrixInverted.calculate()

        assertEquals(
            expected = Matrix4x4.of(
                row0 = Vector1x4.of(-0.0080, -0.1986, 0.2291, 0.2532),
                row1 = Vector1x4.of(0.1849, 0.5667, -0.2693, -0.8239),
                row2 = Vector1x4.of(0.3296, 0.1407, -0.3931, -0.3818),
                row3 = Vector1x4.of(-0.2170, -0.3609, 0.1857, 0.8368),
            ),
            actual = bMatrix,
            absoluteTolerance = 0.001,
        )
    }
}
