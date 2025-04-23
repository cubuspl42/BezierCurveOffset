package app.algebra.polynomials

import app.algebra.assertEqualsWithTolerance
import app.algebra.assertEqualsWithAbsoluteTolerance
import kotlin.test.Test
import kotlin.test.assertEquals

class QuadraticPolynomialTests {
    private val eps = 10e-5

    @Test
    fun testPlus_linear() {
        val pa = Polynomial.quadratic(
            a0 = 12.0,
            a1 = 2.5,
            a2 = 3.4,
        )

        val pb = Polynomial.linear(
            a0 = 2.0,
            a1 = 21.5,
        )

        val sum = pa + pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.quadratic(
                a0 = 14.0,
                a1 = 24.0,
                a2 = 3.4,
            ),
            actual = sum,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = sum,
            actual = pb + pa,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testPlus_quadratic() {
        val pa = Polynomial.quadratic(
            a0 = 12.0,
            a1 = 2.5,
            a2 = 3.4,
        )

        val pb = Polynomial.quadratic(
            a0 = 2.0,
            a1 = 21.5,
            a2 = 13.4,
        )

        val sum = pa + pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.quadratic(
                a0 = 14.0,
                a1 = 24.0,
                a2 = 16.8,
            ),
            actual = sum,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = sum,
            actual = pb + pa,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testTimes_constant() {
        val pa = Polynomial.quadratic(
            a0 = 2.0,
            a1 = -3.0,
            a2 = 1.0,
        )
        val pb = Polynomial.constant(
            a0 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.quadratic(
                a0 = 4.0,
                a1 = -6.0,
                a2 = 2.0,
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
        val pa = Polynomial.quadratic(
            a0 = 2.0,
            a1 = -3.0,
            a2 = 1.0,
        )

        val pb = Polynomial.linear(
            a0 = -1.0,
            a1 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.cubic(
                a0 = -2.0,
                a1 = 7.0,
                a2 = -7.0,
                a3 = 2.0,
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
        val pa = Polynomial.quadratic(
            a0 = 2.0,
            a1 = -3.0,
            a2 = 1.0,
        )

        val pb = Polynomial.quadratic(
            a0 = 4.0,
            a1 = -1.0,
            a2 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.of(
                8.0, -14.0, 11.0, -7.0, 2.0,
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
    fun testFindRoots_singleRoot() {
        val pa = Polynomial.quadratic(
            a0 = 1.0,
            a1 = -2.0,
            a2 = 1.0,
        )

        val roots = pa.findRoots().toSet().sorted()

        assertEqualsWithTolerance(
            expected = listOf(1.0),
            actual = roots,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testFindRoots_twoRoots() {
        val pa = Polynomial.quadratic(
            a0 = 2.0,
            a1 = -3.0,
            a2 = 1.0,
        )

        val roots = pa.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = listOf(1.0, 2.0),
            actual = roots,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testFindRoots_noRoots() {
        val pa = Polynomial.quadratic(
            a0 = 1.0,
            a1 = 0.0,
            a2 = 1.0,
        )

        val roots = pa.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = emptyList(),
            actual = roots,
            absoluteTolerance = eps,
        )
    }
}
