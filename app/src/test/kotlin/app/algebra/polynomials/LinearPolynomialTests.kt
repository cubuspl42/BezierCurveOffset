package app.algebra.polynomials

import app.algebra.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearPolynomialTests {
    private val eps = 10e-4

    @Test
    fun testTimes_constant() {
        val pa = LinearPolynomial.of(
            b = -11.9,
            a = 12.3,
        )

        val pb = ConstantPolynomial.of(
            a = 10.9,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = LinearPolynomial.of(
                b = -129.71,
                a = 134.07,
            ),
            actual = product,
            tolerance = eps,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testTimes_linear() {
        val pa = LinearPolynomial.of(
            b = -11.9,
            a = 12.3,
        )

        val pb = LinearPolynomial.of(
            b = 10.9,
            a = -2.3,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = QuadraticPolynomial.of(
                c = -129.71,
                b = 161.44,
                a = -28.29,
            ),
            actual = product,
            tolerance = eps,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testFindRoots() {
        val pa = LinearPolynomial.of(
            b = 2.0,
            a = -3.0,
        )

        val roots = pa.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = listOf(0.666),
            actual = roots,
            tolerance = eps,
        )
    }
}
