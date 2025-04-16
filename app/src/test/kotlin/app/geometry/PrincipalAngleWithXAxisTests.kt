package app.geometry

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals

class PrincipalAngleWithXAxisTests {
    private val eps = 1e-4

    private fun test(
        fi: Double,
    ) {
        require(fi >= 0.0 && fi <= 2 * PI)

        val unitX = RawVector(x = 1.0, y = 0.0)
        val subject = unitX.rotate(fi)

        val angle = PrincipalAngleWithXAxis(
            subject = subject,
        )

        assertEquals(
            expected = cos(fi),
            actual = angle.cosFi,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = sin(fi),
            actual = angle.sinFi,
            absoluteTolerance = eps,
        )

        val expectedFi = when {
            fi <= PI -> fi
            else -> fi - 2 * PI
        }

        assertEquals(
            expected = expectedFi,
            actual = angle.fi,
            absoluteTolerance = eps,
        )
    }

    /**
     * Boundary between 4th and 1st quadrants (+X)
     */
    @Test
    fun testAngleZero() {
        test(
            fi = 0.0,
        )
    }

    /**
     * 1s quadrant
     */
    @Test
    fun testAngleAcute1() {
        test(
            fi = PI / 8,
        )
    }

    /**
     * Boundary between 1st and 2nd quadrants (+Y)
     */
    @Test
    fun testAngleOrthogonal() {
        test(
            fi = PI / 2,
        )
    }

    /**
     * 2nd quadrant
     */
    @Test
    fun testAngleObtuse() {
        test(
            fi = 5 * PI / 8,
        )
    }

    /**
     * Boundary between 2nd and 3rd quadrants (-X)
     */
    @Test
    fun testAngleStraight() {
        test(
            fi = PI,
        )
    }

    /**
     * 3rd quadrant
     */
    @Test
    fun testAngleReflex1() {
        test(
            fi = 5 * PI / 4,
        )
    }

    /**
     * Boundary between 3rd and 4th quadrants (-Y)
     */
    @Test
    fun testAngleReflexOrthogonal() {
        test(
            fi = 3 * PI / 2,
        )
    }

    /**
     * 4th quadrant
     */
    @Test
    fun testAngleReflex2() {
        test(
            fi = 7 * PI / 4,
        )
    }
}
