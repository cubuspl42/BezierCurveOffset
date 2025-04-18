package app.algebra.linear.vectors.vector2

import app.algebra.assertEqualsWithAbsoluteTolerance
import app.algebra.linear.vectors.vector3.Vector3
import kotlin.test.Test

private val eps = 10e-4

class Vector2Tests {
    @Test
    fun testConv() {
        val a = Vector2.ofIrr(
            a0 = 12.3,
            a1 = -11.2,
        )

        val b = Vector2.ofIrr(
            a0 = 17.12,
            a1 = 0.2,
        )

        /*

        Octave code:
        a = [12.3, -11.2]
        b = [17.12, 0.2]
        c = conv(a, b)

         */

        assertEqualsWithAbsoluteTolerance(
            expected = Vector3.of(
                a0 = 210.576,
                a1 = -189.284,
                a2 = -2.24,
            ),
            actual = a.conv(b),
            absoluteTolerance = eps,
        )
    }
}
