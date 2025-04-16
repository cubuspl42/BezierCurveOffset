package app.geometry

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.test.Test
import kotlin.test.assertEquals

class PrincipalAngleBetweenVectorsTests {
    private val eps = 1e-4

    private fun test(
        reference: RawVector,
        fi: Double,
    ) {
        require(fi >= 0.0 && fi <= 2 * PI)

        val subject = reference.rotate(fi)

        val angle = PrincipalAngleBetweenVectors(
            reference = reference,
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
            reference = RawVector(1.0, 0.0),
            fi = 0.0,
        )
    }

    /**
     * 1s quadrant
     */
    @Test
    fun testAngleAcute1() {
        test(
            reference = RawVector(-2.0, 1.0),
            fi = PI / 8,
        )
    }

    /**
     * Boundary between 1st and 2nd quadrants (+Y)
     */
    @Test
    fun testAngleOrthogonal() {
        test(
            reference = RawVector(-1.0, -4.0),
            fi = PI / 2,
        )
    }

    /**
     * 2nd quadrant
     */
    @Test
    fun testAngleObtuse() {
        test(
            reference = RawVector(2.0, 3.0),
            fi = 5 * PI / 8,
        )
    }

    /**
     * Boundary between 2nd and 3rd quadrants (-X)
     */
    @Test
    fun testAngleStraight() {
        test(
            reference = RawVector(3.5, 1.1),
            fi = PI,
        )
    }

    /**
     * 3rd quadrant
     */
    @Test
    fun testAngleReflex1() {
        test(
            reference = RawVector(1.0, -2.0),
            fi = 5 * PI / 4,
        )
    }

    /**
     * Boundary between 3rd and 4th quadrants (-Y)
     */
    @Test
    fun testAngleReflexOrthogonal() {
        test(
            reference = RawVector(1.0, 2.0),
            fi = 3 * PI / 2,
        )
    }

    /**
     * 4th quadrant
     */
    @Test
    fun testAngleReflex2() {
        test(
            reference = RawVector(1.0, -2.0),
            fi = 7 * PI / 4,
        )
    }
}
