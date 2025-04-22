package app.algebra.polynomials

import app.algebra.assertEqualsWithTolerance
import app.algebra.assertEqualsWithAbsoluteTolerance
import kotlin.test.Test
import kotlin.test.assertEquals

class CubicPolynomialTests {
    private val eps = 10e-5

    @Test
    fun testPlus_constant() {
        val pa = Polynomial.cubic(
            a0 = 12.0,
            a1 = 2.5,
            a2 = 3.4,
            a3 = 5.7,
        )

        val pb = Polynomial.constant(
            a0 = 3.5,
        )

        val sum = pa + pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.cubic(
                a0 = 15.5,
                a1 = 2.5,
                a2 = 3.4,
                a3 = 5.7,
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
    fun testPlus_cubic() {
        val pa = Polynomial.cubic(
            a0 = 12.0,
            a1 = 2.5,
            a2 = 3.4,
            a3 = 5.7,
        )

        val pb = Polynomial.cubic(
            a0 = 2.0,
            a1 = 21.5,
            a2 = 13.4,
            a3 = 7.2,
        )

        val sum = pa + pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.cubic(
                a0 = 14.0,
                a1 = 24.0,
                a2 = 16.8,
                a3 = 12.9,
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
        val pa = Polynomial.cubic(
            a0 = -1.0,
            a1 = 2.0,
            a2 = -3.0,
            a3 = 1.0,
        )
        val pb = Polynomial.constant(
            a0 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.cubic(
                a0 = -2.0,
                a1 = 4.0,
                a2 = -6.0,
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
    fun testTimes_linear() {
        val pa = Polynomial.cubic(
            a0 = -1.0,
            a1 = 2.0,
            a2 = -3.0,
            a3 = 1.0,
        )

        val pb = Polynomial.linear(
            a0 = -1.0,
            a1 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.of(
                1.0,
                -4.0,
                7.0,
                -7.0,
                2.0,
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
        val pa = Polynomial.cubic(
            a0 = -1.0,
            a1 = 2.0,
            a2 = -3.0,
            a3 = 1.0,
        )

        val pb = Polynomial.quadratic(
            a0 = 2.0,
            a1 = -3.0,
            a2 = 1.0,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.of(
                -2.0,
                7.0,
                -13.0,
                13.0,
                -6.0,
                1.0,
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
        val pa = Polynomial.cubic(
            a0 = -1.0,
            a1 = 2.0,
            a2 = -3.0,
            a3 = 1.0,
        )
        val pb = Polynomial.cubic(
            a0 = -3.0,
            a1 = 4.0,
            a2 = -1.0,
            a3 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.of(
                3.0,
                -10.0,
                18.0,
                -19.0,
                11.0,
                -7.0,
                2.0,
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
        val polynomial = Polynomial.cubic(
            a0 = -1.0,
            a1 = 3.0,
            a2 = -3.0,
            a3 = 1.0,
        )

        val roots = polynomial.findRoots().toSet()

        assertEquals(
            expected = setOf(1.0),
            actual = roots,
        )
    }

    @Test
    fun testFindRoots_twoRoots() {
        val polynomial = Polynomial.cubic(
            a0 = 2.0,
            a1 = -3.0,
            a2 = 0.0,
            a3 = 1.0,
        )

        val roots = polynomial.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = listOf(-2.0, 1.0),
            actual = roots,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testFindRoots_threeRoots() {
        val polynomial = Polynomial.cubic(
            a0 = -6.0,
            a1 = 11.0,
            a2 = -6.0,
            a3 = 1.0,
        )

        val roots = polynomial.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = listOf(1.0, 2.0, 3.0),
            actual = roots,
            absoluteTolerance = eps,
        )
    }
}
