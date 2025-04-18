package app.algebra.polynomials

import app.algebra.assertEqualsWithTolerance
import app.algebra.assertEqualsWithAbsoluteTolerance
import kotlin.test.Test
import kotlin.test.assertEquals

class QuadraticPolynomialTests {
    private val eps = 10e-5

    @Test
    fun testTimes_constant() {
        val pa = QuadraticPolynomial.of(
            c = 2.0,
            b = -3.0,
            a = 1.0,
        )
        val pb = ConstantPolynomial.of(
            a = 2.0,
        )

        val product = pa * pb

        /* Octave code:
        pa = [2, -3, 1]
        pb = [2]
        product = conv(pa, pb)
        */

        assertEqualsWithAbsoluteTolerance(
            expected = QuadraticPolynomial.of(
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
        val pa = QuadraticPolynomial.of(
            c = 2.0,
            b = -3.0,
            a = 1.0,
        )
        val pb = LinearPolynomial.of(
            b = -1.0,
            a = 2.0,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = CubicPolynomial.of(
                d = -2.0,
                c = 7.0,
                b = -7.0,
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
    fun testTimes_quadratic() {
        val pa = QuadraticPolynomial.of(
            c = 2.0,
            b = -3.0,
            a = 1.0,
        )

        val pb = QuadraticPolynomial.of(
            c = 4.0,
            b = -1.0,
            a = 2.0,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = HighPolynomial.of(
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
        val pa = QuadraticPolynomial.of(
            c = 1.0,
            b = -2.0,
            a = 1.0,
        )

        val roots = pa.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = listOf(1.0),
            actual = roots,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testFindRoots_twoRoots() {
        val pa = QuadraticPolynomial.of(
            c = 2.0,
            b = -3.0,
            a = 1.0,
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
        val pa = QuadraticPolynomial.of(
            c = 1.0,
            b = 0.0,
            a = 1.0,
        )

        val roots = pa.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = emptyList(),
            actual = roots,
            absoluteTolerance = eps,
        )
    }
}
