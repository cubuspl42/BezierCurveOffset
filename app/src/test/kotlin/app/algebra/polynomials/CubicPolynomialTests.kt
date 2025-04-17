package app.algebra.polynomials

import app.algebra.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals

class CubicPolynomialTests {
    private val eps = 10e-5

    @Test
    fun testTimes_constant() {
        val pa = CubicPolynomial.of(
            d = -1.0,
            c = 2.0,
            b = -3.0,
            a = 1.0,
        )
        val pb = ConstantPolynomial.of(
            a = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = CubicPolynomial.of(
                d = -2.0,
                c = 4.0,
                b = -6.0,
                a = 2.0,
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
        val pa = CubicPolynomial.of(
            d = -1.0,
            c = 2.0,
            b = -3.0,
            a = 1.0,
        )

        val pb = LinearPolynomial.of(
            b = -1.0,
            a = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial.of(
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
        val pa = CubicPolynomial.of(
            d = -1.0,
            c = 2.0,
            b = -3.0,
            a = 1.0,
        )

        val pb = QuadraticPolynomial.of(
            c = 2.0,
            b = -3.0,
            a = 1.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = HighPolynomial.of(
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
        val pa = CubicPolynomial.of(
            d = -1.0,
            c = 2.0,
            b = -3.0,
            a = 1.0,
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
        val polynomial = CubicPolynomial.of(
            d = -1.0,
            c = 3.0,
            b = -3.0,
            a = 1.0,
        )

        val roots = polynomial.findRoots()

        assertEquals(
            expected = setOf(1.0),
            actual = roots,
        )
    }

    @Test
    fun testFindRoots_twoRoots() {
        val polynomial = CubicPolynomial.of(
            d = 2.0,
            c = -3.0,
            b = 0.0,
            a = 1.0,
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
        val polynomial = CubicPolynomial.of(
            d = -6.0,
            c = 11.0,
            b = -6.0,
            a = 1.0,
        )

        val roots = polynomial.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = listOf(1.0, 2.0, 3.0),
            actual = roots,
            absoluteTolerance = eps,
        )
    }
}
