package app.geometry

import app.algebra.linear.vectors.vector2.Vector2
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AngleTests {
    companion object {
        private const val eps = 1e-4
        private const val fiEps = eps * 2 * PI

        private fun buildVector(
            fi: Double,
        ): Vector2<*> = Vector2.of(
            x = cos(fi),
            y = sin(fi),
        )

        private fun testAngleForFi(
            fi: Double,
        ) {

        }

        private fun testIsZeroWithRadialTolerance(
            fi: Double,
            fiTolerance: Double,
        ) {
            require(fiTolerance <= PI)

            val vecFi = buildVector(fi)

            fun isZeroWithRadialTolerance(
                other: Vector2<*>,
            ) = Angle(
                a = vecFi,
                b = other,
            ).isZeroWithRadialTolerance(
                tolerance = RadialTolerance.of(angle = fiTolerance),
            )

            val vecFiNonAcceptableVeryFarCcw1 = buildVector(fi - PI / 4)
            val vecFiNonAcceptableVeryFarCcw2 = buildVector(fi - PI / 2)
            val vecFiNonAcceptableVeryFarCcw3 = buildVector(fi - PI / 2)
            val vecFiNonAcceptableVeryFarCw1 = buildVector(fi + PI / 4)
            val vecFiNonAcceptableVeryFarCw2 = buildVector(fi + PI / 2)
            val vecFiNonAcceptableVeryFarCw3 = buildVector(fi + PI / 2)


            assertFalse(isZeroWithRadialTolerance(vecFiNonAcceptableVeryFarCcw1))
            assertFalse(isZeroWithRadialTolerance(vecFiNonAcceptableVeryFarCcw2))
            assertFalse(isZeroWithRadialTolerance(vecFiNonAcceptableVeryFarCcw3))
            assertFalse(isZeroWithRadialTolerance(vecFiNonAcceptableVeryFarCw1))
            assertFalse(isZeroWithRadialTolerance(vecFiNonAcceptableVeryFarCw2))
            assertFalse(isZeroWithRadialTolerance(vecFiNonAcceptableVeryFarCw3))

            val vecFiNonAcceptableCcwClose = buildVector(fi - fiTolerance - eps)
            val vecFiAcceptableCcwFar = buildVector(fi - fiTolerance + eps)
            val vecFiAcceptableCcwClose = buildVector(fi - eps)
            val vecFiAcceptableCwClose = buildVector(fi + eps)
            val vecFiAcceptableCwFar = buildVector(fi + fiTolerance - eps)
            val vecFiNonAcceptableCwClose = buildVector(fi + fiTolerance + eps)

            assertFalse(isZeroWithRadialTolerance(vecFiNonAcceptableCcwClose))
            assertTrue(isZeroWithRadialTolerance(vecFiAcceptableCcwFar))
            assertTrue(isZeroWithRadialTolerance(vecFiAcceptableCcwClose))
            assertTrue(isZeroWithRadialTolerance(vecFiAcceptableCwClose))
            assertTrue(isZeroWithRadialTolerance(vecFiAcceptableCwFar))
            assertFalse(isZeroWithRadialTolerance(vecFiNonAcceptableCwClose))
        }
    }

    @Test
    fun testAngleZero() {
        val angle = Angle(
            a = Vector2.of(1.0, 0.0),
            b = Vector2.of(1.1, 0.0),
        )

        val expectedFi = 0.0
        val expectedCosFi = 1.0
        val expectedCosSqFi = 1.0

        assertTrue(angle.isAcute)

        assertEquals(
            expected = expectedCosSqFi,
            actual = angle.cosSqFi,
        )

        assertEquals(
            expected = expectedCosFi,
            actual = angle.cosFiAcute,
        )

        assertEquals(
            expected = expectedFi,
            actual = angle.fi,
            absoluteTolerance = 1e-6,
        )
    }

    @Test
    fun testAngleAcute1() {
        val angle = Angle(
            a = Vector2.of(1.0, 0.0),
            b = Vector2.of(2.0, 1.0),
        )

        val expectedFi = 0.46365
        val expectedCosFi = cos(expectedFi)
        val expectedCosSqFi = expectedCosFi * expectedCosFi

        assertTrue(angle.isAcute)

        assertEquals(
            expected = expectedCosSqFi,
            actual = angle.cosSqFi,
            absoluteTolerance = Companion.eps,
        )

        assertEquals(
            expected = expectedCosFi,
            actual = angle.cosFiAcute,
            absoluteTolerance = Companion.eps,
        )

        assertEquals(
            expected = expectedFi,
            actual = angle.fi,
            absoluteTolerance = Companion.eps,
        )
    }

    @Test
    fun testAngleOrthogonal() {
        val angle = Angle(
            a = Vector2.of(1.0, 0.0),
            b = Vector2.of(0.0, 1.0),
        )

        val expectedFi = Math.PI / 2
        val expectedCosFi = cos(expectedFi)
        val expectedCosSqFi = expectedCosFi * expectedCosFi

        assertFalse(angle.isAcute)

        assertEquals(
            expected = expectedCosSqFi,
            actual = angle.cosSqFi,
            absoluteTolerance = Companion.eps,
        )

        assertEquals(
            expected = expectedCosFi,
            actual = angle.cosFiAcute,
            absoluteTolerance = Companion.eps,
        )

        assertEquals(
            expected = expectedFi,
            actual = angle.fi,
            absoluteTolerance = Companion.eps,
        )
    }

    @Test
    fun testAngleObtuse() {
        val angle = Angle(
            a = Vector2.of(1.0, 0.0),
            b = Vector2.of(-1.0, 1.0),
        )

        /*
        Octave code

        a = [1.0, 0.0];
        b = [-1.0, 1.0];
        fi = vectorAngle(a, b);
         */

        val expectedFi = 2.35619
        val expectedCosFi = cos(expectedFi)
        val expectedCosSqFi = expectedCosFi * expectedCosFi

        assertFalse(angle.isAcute)

        assertEquals(
            expected = expectedCosSqFi,
            actual = angle.cosSqFi,
            absoluteTolerance = Companion.eps,
        )

        assertEquals(
            expected = expectedCosFi,
            actual = angle.cosFi,
            absoluteTolerance = Companion.eps,
        )

        assertEquals(
            expected = expectedFi,
            actual = angle.fi,
            absoluteTolerance = Companion.eps,
        )
    }

    @Test
    fun testAngleStraight() {
        val angle = Angle(
            a = Vector2.of(1.0, 0.0),
            b = Vector2.of(-1.0, 0.0),
        )

        val expectedFi = Math.PI
        val expectedCosFi = cos(expectedFi)
        val expectedCosSqFi = expectedCosFi * expectedCosFi

        assertFalse(angle.isAcute)

        assertEquals(
            expected = expectedCosSqFi,
            actual = angle.cosSqFi,
            absoluteTolerance = Companion.eps,
        )

        assertEquals(
            expected = expectedCosFi,
            actual = angle.cosFi,
            absoluteTolerance = Companion.eps,
        )

        assertEquals(
            expected = expectedFi,
            actual = angle.fi,
            absoluteTolerance = Companion.eps,
        )
    }

    @Test
    fun testIsZeroWithRadialTolerance1() {
        testIsZeroWithRadialTolerance(
            fi = 0.0,
            fiTolerance = fiEps,
        )
    }

    @Test
    fun testIsZeroWithRadialTolerance2() {
        testIsZeroWithRadialTolerance(
            fi = Math.PI / 8,
            fiTolerance = fiEps,
        )
    }

    @Test
    fun testIsZeroWithRadialTolerance2b() {
        testIsZeroWithRadialTolerance(
            fi = Math.PI / 8,
            fiTolerance = fiEps * 2,
        )
    }

    @Test
    fun testIsZeroWithRadialTolerance3() {
        testIsZeroWithRadialTolerance(
            fi = Math.PI / 4,
            fiTolerance = fiEps,
        )
    }

    @Test
    fun testIsZeroWithRadialTolerance4() {
        testIsZeroWithRadialTolerance(
            fi = Math.PI / 2,
            fiTolerance = fiEps,
        )
    }

    @Test
    fun testIsZeroWithRadialTolerance5() {
        testIsZeroWithRadialTolerance(
            fi = Math.PI,
            fiTolerance = fiEps,
        )
    }
}
