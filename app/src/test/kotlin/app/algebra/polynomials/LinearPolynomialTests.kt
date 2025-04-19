package app.algebra.polynomials

import app.algebra.assertEqualsWithTolerance
import app.algebra.assertEqualsWithAbsoluteTolerance
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearPolynomialTests {
    private val eps = 10e-4

    @Test
    fun testTimes_constant() {
        val pa = LinearPolynomial.of(
            a0 = -11.9,
            a1 = 12.3,
        )

        val pb = ConstantPolynomial.of(
            a = 10.9,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = LinearPolynomial.of(
                a0 = -129.71,
                a1 = 134.07,
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
    fun testTimes_linear() {
        val pa = LinearPolynomial.of(
            a0 = -11.9,
            a1 = 12.3,
        )

        val pb = LinearPolynomial.of(
            a0 = 10.9,
            a1 = -2.3,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = QuadraticPolynomial.of(
                c = -129.71,
                b = 161.44,
                a = -28.29,
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
    fun testFindRoots() {
        val pa = LinearPolynomial.of(
            a0 = 2.0,
            a1 = -3.0,
        )

        val roots = pa.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = listOf(0.666),
            actual = roots,
            absoluteTolerance = eps,
        )
    }
}
