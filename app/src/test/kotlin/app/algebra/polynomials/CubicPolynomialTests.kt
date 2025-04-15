package app.algebra.polynomials

import app.algebra.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals

class CubicPolynomialTests {
    private val eps = 10e-5

    @Test
    fun testFindRoots_singleRoot() {
        val polynomial = CubicPolynomial.of(
            a = 1.0,
            b = -3.0,
            c = 3.0,
            d = -1.0,
        ) as CubicPolynomial

        val roots = polynomial.findRoots()

        assertEquals(
            expected = setOf(1.0),
            actual = roots,
        )
    }

    @Test
    fun testFindRoots_twoRoots() {
        val polynomial = CubicPolynomial.of(
            a = 1.0,
            b = 0.0,
            c = -3.0,
            d = 2.0
        ) as CubicPolynomial

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
            a = 1.0,
            b = -6.0,
            c = 11.0,
            d = -6.0
        ) as CubicPolynomial

        val roots = polynomial.findRoots().sorted()

        assertEqualsWithTolerance(
            expected = listOf(1.0, 2.0, 3.0),
            actual = roots,
            absoluteTolerance = eps,
        )
    }
}
