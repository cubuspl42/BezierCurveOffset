package app.algebra.linear.vectors.vector3

import app.algebra.assertEqualsWithTolerance
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.linear.vectors.vectorN.VectorN
import kotlin.test.Test

private val eps = 10e-4

class Vector3Tests {
    @Test
    fun testConv_vector2() {
        val a = Vector3.ofIrr(
            a0 = 1.2,
            a1 = -3.4,
            a2 = 5.6,
        )

        val b = Vector2.ofIrr(
            a0 = 7.8,
            a1 = -9.0,
        )

        assertEqualsWithTolerance(
            expected = Vector4.of(
                a0 = 9.36,
                a1 = -37.32,
                a2 = 74.28,
                a3 = -50.4,
            ),
            actual = a.conv(b),
            tolerance = eps,
        )
    }

    @Test
    fun testConv_vector3() {
        val a = Vector3.ofIrr(
            a0 = 2.1,
            a1 = -4.3,
            a2 = 6.5,
        )

        val b = Vector3.ofIrr(
            a0 = 8.7,
            a1 = -9.8,
            a2 = 10.9,
        )

        assertEqualsWithTolerance(
            expected = VectorN(
                elements = listOf(
                    18.27,
                    -57.99,
                    121.58,
                    -110.57,
                    70.85,
                ),
            ),
            actual = a.conv(b),
            tolerance = eps,
        )
    }
}
