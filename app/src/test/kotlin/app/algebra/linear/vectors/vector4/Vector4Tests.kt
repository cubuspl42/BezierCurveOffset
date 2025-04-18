package app.algebra.linear.vectors.vector4

import app.algebra.assertEqualsWithTolerance
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector3.Vector3
import app.algebra.linear.vectors.vectorN.VectorN
import kotlin.test.Test

private val eps = 10e-4

class Vector4Tests {
    @Test
    fun testConv_vector2() {
        val a = Vector4.ofIrr(
            a0 = 1.1,
            a1 = -2.2,
            a2 = 3.3,
            a3 = -4.4,
        )

        val b = Vector2.ofIrr(
            a0 = 5.5,
            a1 = -6.6,
        )

        assertEqualsWithTolerance(
            expected = VectorN.ofIrr(
                elements = listOf(
                    6.05,
                    -19.36,
                    32.67,
                    -45.98,
                    29.04,
                ),
            ),
            actual = a.conv(b),
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testConv_vector3() {
        val a = Vector4.ofIrr(
            a0 = 2.2,
            a1 = -3.3,
            a2 = 4.4,
            a3 = -5.5,
        )

        val b = Vector3.ofIrr(
            a0 = 6.6,
            a1 = -7.7,
            a2 = 8.8,
        )

        assertEqualsWithTolerance(
            expected = VectorN.ofIrr(
                elements = listOf(
                    14.52,
                    -38.72,
                    73.81,
                    -99.22,
                    81.07,
                    -48.4,
                ),
            ),
            actual = a.conv(b),
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testConv_vector4() {
        val a = Vector4.ofIrr(
            a0 = 3.3,
            a1 = -4.4,
            a2 = 5.5,
            a3 = -6.6,
        )

        val b = Vector4.ofIrr(
            a0 = 7.7,
            a1 = -8.8,
            a2 = 9.9,
            a3 = -10.1,
        )

        assertEqualsWithTolerance(
            expected = VectorN(
                elements = listOf(
                    25.41,
                    -62.92,
                    113.74,
                    -176.11,
                    156.97,
                    -120.89,
                    66.66,
                )
            ),
            actual = a.conv(b),
            absoluteTolerance = eps,
        )
    }
}
