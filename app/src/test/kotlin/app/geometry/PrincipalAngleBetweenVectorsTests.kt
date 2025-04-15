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

        assertEquals(
            expected = fi,
            actual = angle.fi,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testAngleZero() {
        test(
            reference = RawVector(1.0, 0.0),
            fi = 0.0,
        )
    }

    @Test
    fun testAngleAcute1() {
        test(
            reference = RawVector(-2.0, 1.0),
            fi = PI / 8,
        )
    }

    @Test
    fun testAngleOrthogonal() {
        test(
            reference = RawVector(-1.0, -4.0),
            fi = PI / 2,
        )
    }

    @Test
    fun testAngleObtuse() {
        test(
            reference = RawVector(2.0, 3.0),
            fi = 3 * PI / 8,
        )
    }

    @Test
    fun testAngleStraight() {
        test(
            reference = RawVector(3.5, 1.1),
            fi = PI,
        )
    }

    @Test
    fun testAngleReflex() {
        test(
            reference = RawVector(1.0, -2.0),
            fi = 5 * PI / 8,
        )
    }
}
