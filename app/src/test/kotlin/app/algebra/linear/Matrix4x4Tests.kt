package app.algebra.linear

import app.algebra.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class Matrix4x4Tests {
    @Test
    fun testTimesSquare() {
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
    fun testTimesRectangular() {
        val aMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(1.0, 2.0, 3.0, 4.0),
            row1 = Vector1x4.of(5.0, 6.0, 7.0, 8.0),
            row2 = Vector1x4.of(9.0, 10.0, 11.0, 12.0),
            row3 = Vector1x4.of(13.0, 14.0, 15.0, 16.0)
        )

        val bMatrix = Matrix4xN(
            columns = listOf(
                Vector4x1.of(17.0, 18.0, 19.0, 20.0),
                Vector4x1.of(21.0, 22.0, 23.0, 24.0),
                Vector4x1.of(25.0, 26.0, 27.0, 28.0),
                Vector4x1.of(29.0, 30.0, 31.0, 32.0),
                Vector4x1.of(33.0, 34.0, 35.0, 36.0),
                Vector4x1.of(37.0, 38.0, 39.0, 40.0),
            ),
        )

        val cMatrix = aMatrix * bMatrix

        val expectedMatrix = Matrix4xN(
            columns = listOf(
                // column-oriented!
                Vector4x1.of(190.0, 486.0, 782.0, 1078.0),
                Vector4x1.of(230.0, 590.0, 950.0, 1310.0),
                Vector4x1.of(270.0, 694.0, 1118.0, 1542.0),
                Vector4x1.of(310.0, 798.0, 1286.0, 1774.0),
                Vector4x1.of(350.0, 902.0, 1454.0, 2006.0),
                Vector4x1.of(390.0, 1006.0, 1622.0, 2238.0),
            ),
        )

        assertEqualsWithTolerance(
            expected = expectedMatrix,
            actual = cMatrix,
            absoluteTolerance = 0.001,
        )
    }

    @Test
    fun testLuDecompose() {
        val aMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(1.0308, 1.0560, 1.1034, 1.1960),
            row1 = Vector1x4.of(1.0560, 1.1034, 1.1960, 1.3899),
            row2 = Vector1x4.of(1.1034, 1.1960, 1.3899, 1.8377),
            row3 = Vector1x4.of(1.1960, 1.3899, 1.8377, 4.0),
        )

        val lupDecomposition = assertNotNull(
            aMatrix.luDecompose(),
        )

        val lMatrix = lupDecomposition.l
        val uMatrix = lupDecomposition.u

        assertTrue(actual = lMatrix.isLowerTriangular())

        assertTrue(actual = uMatrix.isUpperTriangular())

        val luMatrix = lMatrix * uMatrix

        assertEqualsWithTolerance(
            expected = aMatrix,
            actual = luMatrix,
            absoluteTolerance = 0.001,
        )
    }

    @Test
    fun testLupDecompose() {
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
        val uMatrix = lupDecomposition.u
        val pMatrix = lupDecomposition.p

        assertTrue(actual = lMatrix.isLowerTriangular())

        assertTrue(actual = uMatrix.isUpperTriangular())

        val paMatrix = pMatrix * aMatrix
        val luMatrix = lMatrix * uMatrix

        assertEqualsWithTolerance(
            expected = paMatrix,
            actual = luMatrix,
            absoluteTolerance = 0.001,
        )
    }

    @Test
    fun testLupDecompose2() {
        val aMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(1.0308, 1.0560, 1.1034, 1.1960),
            row1 = Vector1x4.of(1.0560, 1.1034, 1.1960, 1.3899),
            row2 = Vector1x4.of(1.1034, 1.1960, 1.3899, 1.8377),
            row3 = Vector1x4.of(1.1960, 1.3899, 1.8377, 4.0),
        )

        val lupDecomposition = assertNotNull(
            aMatrix.lupDecompose(),
        )

        val lMatrix = lupDecomposition.l
        val uMatrix = lupDecomposition.u
        val pMatrix = lupDecomposition.p

        assertTrue(actual = lMatrix.isLowerTriangular())

        assertTrue(actual = uMatrix.isUpperTriangular())

        val paMatrix = pMatrix * aMatrix
        val luMatrix = lMatrix * uMatrix

        assertEqualsWithTolerance(
            expected = paMatrix,
            actual = luMatrix,
            absoluteTolerance = 0.001,
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

        assertEqualsWithTolerance(
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

        assertEqualsWithTolerance(
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

        assertEqualsWithTolerance(
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

        assertEqualsWithTolerance(
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
    fun testSolveByForwardSubstitutionMatrix2() {
        val aMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(1.0, 0.0, 0.0, 0.0),
            row1 = Vector1x4.of(0.8618729096989967, 1.0, 0.0, 0.0),
            row2 = Vector1x4.of(0.8829431438127091, 0.8723587622247958, 1.0, 0.0),
            row3 = Vector1x4.of(0.9225752508361204, 0.6080120462194373, 1.7976835424854054, 1.0),
        )

        val yMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(0.0, 1.0, 0.0, 0.0),
            row1 = Vector1x4.of(0.0, 0.0, 1.0, 0.0),
            row2 = Vector1x4.of(0.0, 0.0, 0.0, 1.0),
            row3 = Vector1x4.of(1.0, 0.0, 0.0, 0.0),
        )

        val xMatrix = aMatrix.solveByForwardSubstitution(
            yMatrix = yMatrix,
        )

        assertEqualsWithTolerance(
            expected = Matrix4x4.of(
                row0 = Vector1x4.of(0.0, 1.0, 0.0, 0.0),
                row1 = Vector1x4.of(0.0, -0.8619, 1.0, 0.0),
                row2 = Vector1x4.of(0.0, -0.1311, -0.8724, 1.0),
                row3 = Vector1x4.of(1.0, -0.1629, 0.9602, -1.7977),
            ),
            actual = xMatrix,
            absoluteTolerance = 0.001,
        )
    }

    @Test
    fun testInvertSingular() {
        assertNull(
            actual = Matrix4x4.zero.invert(),
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

        assertEqualsWithTolerance(
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

        assertEqualsWithTolerance(
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

    @Test
    fun testInvertCalculate2() {
        val aMatrix = Matrix4x4.of(
            row0 = Vector1x4.of(1.0308, 1.0560, 1.1034, 1.1960),
            row1 = Vector1x4.of(1.0560, 1.1034, 1.1960, 1.3899),
            row2 = Vector1x4.of(1.1034, 1.1960, 1.3899, 1.8377),
            row3 = Vector1x4.of(1.1960, 1.3899, 1.8377, 4.0),
        )

        val aMatrixInverted = assertNotNull(aMatrix.invert())

        val bMatrix = aMatrixInverted.calculate()

        assertEqualsWithTolerance(
            expected = Matrix4x4.of(
                row0 = Vector1x4.of(535.62246, -783.58183, 256.78892, -5.85127),
                row1 = Vector1x4.of(-783.58183, 1165.97234, -395.73348, 10.95458),
                row2 = Vector1x4.of(256.78892, -395.73348, 145.44556, -6.09372),
                row3 = Vector1x4.of(-5.85127, 10.95458, -6.09372, 0.99269),
            ),
            actual = bMatrix,
            absoluteTolerance = 0.0001,
        )
    }
}
