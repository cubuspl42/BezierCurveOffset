package app.algebra.polynomials

import app.algebra.assertEqualsWithAbsoluteTolerance
import kotlin.test.Test

class ConstantPolynomialTests {
    private val eps = 10e-4

    @Test
    fun testPlus_scalar() {
        val pa = Polynomial.constant(
            a0 = 3.0,
        )

        val b = 2.0

        val sum = pa + b

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.constant(
                a0 = 5.0,
            ),
            actual = sum,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testPlus_constant() {
        val pa = Polynomial.constant(
            a0 = 3.0,
        )

        val pb = Polynomial.constant(
            a0 = 2.0,
        )

        val sum = pa + pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.constant(
                a0 = 5.0,
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
        val pa = Polynomial.constant(
            a0 = 3.0,
        )

        val pb = Polynomial.constant(
            a0 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.constant(
                a0 = 6.0,
            ),
            actual = product,
            absoluteTolerance = eps,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = product,
            actual = pb * pa,
            absoluteTolerance = eps,
        )
    }
}
