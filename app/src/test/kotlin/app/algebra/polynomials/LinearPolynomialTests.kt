package app.algebra.polynomials

import app.algebra.assertEqualsWithTolerance
import app.algebra.assertEqualsWithAbsoluteTolerance
import kotlin.test.Test
import kotlin.test.assertEquals

class LinearPolynomialTests {
    private val eps = 10e-4

    @Test
    fun testPlus_scalar() {
        val pa = Polynomial.linear(
            a1 = 11.0,
            a0 = 3.0,
        )

        val b = 2.0

        val sum = pa + b

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.linear(
                a1 = 11.0,
                a0 = 5.0,
            ),
            actual = sum,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testTimes_scalar() {
        val pa = Polynomial.linear(
            a0 = -11.9,
            a1 = 12.3,
        )

        val s = 2.0

        val product = pa * s

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.linear(
                a0 = -23.8,
                a1 = 24.6,
            ),
            actual = product,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = product,
            actual = s * pa,
        )
    }


    @Test
    fun testTimes_constant() {
        val pa = Polynomial.linear(
            a0 = -11.9,
            a1 = 12.3,
        )

        val pb = Polynomial.constant(
            a0 = 10.9,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.linear(
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
        val pa = Polynomial.linear(
            a0 = -11.9,
            a1 = 12.3,
        )

        val pb = Polynomial.linear(
            a0 = 10.9,
            a1 = -2.3,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.quadratic(
                a0 = -129.71,
                a1 = 161.44,
                a2 = -28.29,
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
        val pa = Polynomial.linear(
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
