package app.algebra.linear.vectors.vectorN

import app.algebra.assertEqualsWithTolerance
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector3.Vector3
import app.algebra.linear.vectors.vector4.Vector4
import kotlin.test.Test

private val eps = 10e-4

class VectorNTests {
    @Test
    fun testPlus_vector2() {
        val a = VectorN.ofIrr(
            elements = listOf(1.0, -2.0, 3.0, 12.0, -7.2),
        )

        val b = Vector2.ofIrr(
            a0 = 4.0,
            a1 = -5.0,
        )

        assertEqualsWithTolerance(
            expected = VectorN.ofIrr(
                5.0, -7.0, 3.0, 12.0, -7.2
            ),
            actual = a + b,
            tolerance = eps,
        )
    }

    @Test
    fun testPlus_vector3() {
        val a = VectorN.ofIrr(
            elements = listOf(1.0, -2.0, 3.0, 12.0, -7.2),
        )

        val b = Vector3.ofIrr(
            a0 = 4.0,
            a1 = -5.0,
            a2 = 6.0,
        )

        assertEqualsWithTolerance(
            expected = VectorN.ofIrr(
                5.0, -7.0, 9.0, 12.0, -7.2
            ),
            actual = a + b,
            tolerance = eps,
        )
    }


    @Test
    fun testPlus_vector4() {
        val a = VectorN.ofIrr(
            elements = listOf(1.0, -2.0, 3.0, 12.0, -7.2),
        )

        val b = Vector4.ofIrr(
            a0 = 4.0,
            a1 = -5.0,
            a2 = 6.0,
            a3 = 7.0,
        )

        assertEqualsWithTolerance(
            expected = VectorN.ofIrr(
                5.0, -7.0, 9.0, 19.0, -7.2
            ),
            actual = a + b,
            tolerance = eps,
        )
    }

    @Test
    fun testPlus_sameSize() {
        val a = VectorN.ofIrr(
            elements = listOf(1.0, -2.0, 3.0)
        )

        val b = VectorN.ofIrr(
            elements = listOf(4.0, -5.0, 6.0)
        )

        assertEqualsWithTolerance(
            expected = VectorN.ofIrr(
                elements = listOf(5.0, -7.0, 9.0)
            ),
            actual = a + b,
            tolerance = eps,
        )
    }

    @Test
    fun testPlus_smaller() {
        val a = VectorN.ofIrr(
            elements = listOf(1.0, -2.0, 3.0)
        )

        val b = VectorN.ofIrr(
            elements = listOf(4.0, -5.0)
        )

        assertEqualsWithTolerance(
            expected = VectorN.ofIrr(
                elements = listOf(5.0, -7.0, 3.0)
            ),
            actual = a + b,
            tolerance = eps,
        )
    }

    @Test
    fun testPlus_larger() {
        val a = VectorN.ofIrr(
            elements = listOf(1.0, -2.0, 3.0)
        )

        val b = VectorN.ofIrr(
            elements = listOf(4.0, -5.0, 6.0, 7.0)
        )

        assertEqualsWithTolerance(
            expected = VectorN.ofIrr(
                elements = listOf(5.0, -7.0, 9.0, 7.0)
            ),
            actual = a + b,
            tolerance = eps,
        )
    }

    @Test
    fun testConv_vector2() {
        val a = VectorN.ofIrr(
            elements = listOf(1.0, -2.0, 3.0)
        )

        val b = Vector2.ofIrr(
            a0 = 4.0,
            a1 = -5.0,
        )

        assertEqualsWithTolerance(
            expected = VectorN.ofIrr(
                elements = listOf(
                    4.0,
                    -13.0,
                    22.0,
                    -15.0,
                )
            ),
            actual = a.conv(b),
            tolerance = eps,
        )
    }

    @Test
    fun testConv_vector3() {
        val a = VectorN.ofIrr(
            elements = listOf(2.0, -3.0, 4.0, -5.0)
        )

        val b = Vector3.ofIrr(
            a0 = 6.0,
            a1 = -7.0,
            a2 = 8.0,
        )

        assertEqualsWithTolerance(
            expected = VectorN.ofIrr(
                elements = listOf(
                    12.0,
                    -32.0,
                    61.0,
                    -82.0,
                    67.0,
                    -40.0,
                ),
            ),
            actual = a.conv(b),
            tolerance = eps,
        )
    }

    @Test
    fun testConv_vector4() {
        val a = VectorN.ofIrr(
            elements = listOf(3.0, -4.0, 5.0, -6.0, 7.0)
        )

        val b = Vector4.ofIrr(
            a0 = 8.0,
            a1 = -9.0,
            a2 = 10.0,
            a3 = -11.0,
        )

        assertEqualsWithTolerance(
            expected = VectorN(
                elements = listOf(
                    24.0,
                    -59.0,
                    106.0,
                    -166.0,
                    204.0,
                    -178.0,
                    136.0,
                    -77.0,
                )
            ),
            actual = a.conv(b),
            tolerance = eps,
        )
    }

    @Test
    fun testConv_vectorN() {
        val a = VectorN.ofIrr(
            elements = listOf(3.0, -4.0, 5.0, -6.0, 7.0)
        )

        val b = VectorN.ofIrr(
            elements = listOf(8.0, -9.0, 10.0, -11.0, 12.0)
        )

        assertEqualsWithTolerance(
            expected = VectorN.ofIrr(
                elements = listOf(
                    24.0,
                    -59.0,
                    106.0,
                    -166.0,
                    240.0,
                    -226.0,
                    196.0,
                    -149.0,
                    84.0,
                ),
            ),
            actual = a.conv(b),
            tolerance = eps,
        )
    }
}
