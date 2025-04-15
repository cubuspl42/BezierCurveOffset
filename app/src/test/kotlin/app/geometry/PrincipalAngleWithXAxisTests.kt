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

        assertEquals(
            expected = fi,
            actual = angle.fi,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testAngleZero() {
        test(
            fi = 0.0,
        )
    }

    @Test
    fun testAngleAcute1() {
        test(
            fi = PI / 8,
        )
    }

    @Test
    fun testAngleOrthogonal() {
        test(
            fi = PI / 2,
        )
    }

    @Test
    fun testAngleObtuse() {
        test(
            fi = 3 * PI / 8,
        )
    }

    @Test
    fun testAngleStraight() {
        test(
            fi = PI,
        )
    }

    @Test
    fun testAngleReflex() {
        test(
            fi = 5 * PI / 8,
        )
    }
}
