package app.algebra.polynomials

import app.algebra.assertEqualsWithTolerance
import app.algebra.linear.vectors.vectorN.VectorN
import kotlin.test.Test
import kotlin.test.assertEquals

class HighPolynomialTests {
    private val eps = 10e-5

    @Test
    fun testTimes_constant() {
        val pa = HighPolynomial.of(
            -4.0, 3.0, -2.0, 1.0, 6.0, 12.2
        )

        val pb = ConstantPolynomial.of(
            a = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial.of(
                -8.0,
                6.0,
                -4.0,
                2.0,
                12.0,
                24.4,
            ),
            actual = product,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testTimes_quadratic() {
        val pa = HighPolynomial.of(
            -4.0,
            3.0,
            -2.0,
            1.0,
            17.9,
            -2.3,
        )

        val pb = QuadraticPolynomial.of(
            c = 4.0,
            b = -1.0,
            a = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial.of(
                -16.0,
                16.0,
                -19.0,
                12.0,
                66.6,
                -25.1,
                38.1,
                -4.6,
            ),
            actual = product,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testTimes_cubic() {
        val pa = HighPolynomial.of(
            -4.0,
            3.0,
            -2.0,
            1.0,
            18.9,
        )

        val pb = CubicPolynomial.of(
            d = -3.0,
            c = 4.0,
            b = -1.0,
            a = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial.of(
                12.0,
                -25.0,
                22.0,
                -22.0,
                -44.7,
                70.6,
                -16.9,
                37.8,
            ),
            actual = product,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testTimes_high() {
        val pa = HighPolynomial.of(
            -4.0,
            3.0,
            -2.0,
            1.0,
            18.9,
            -2.0,
        )

        val pb = HighPolynomial.of(
            -4.0,
            23.0,
            -2.2,
            1.0,
            77.2,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial.of(
                16.0,
                -104.0,
                85.8,
                -60.6,
                -354.0,
                670.1,
                -240.98,
                100.5,
                1457.08,
                -154.4,
            ),
            actual = product,
            absoluteTolerance = eps,
        )
    }
}
