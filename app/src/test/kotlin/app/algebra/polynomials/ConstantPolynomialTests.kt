package app.algebra.polynomials

import app.algebra.assertEqualsWithTolerance
import kotlin.test.Test

class ConstantPolynomialTests {
    private val eps = 10e-4

    @Test
    fun testTimes_constant() {
        val pa = ConstantPolynomial.of(
            a = 3.0,
        )
        val pb = ConstantPolynomial.of(
            a = 2.0,
        )

        val product = pa * pb

        assertEqualsWithTolerance(
            expected = ConstantPolynomial.of(
                a = 6.0,
            ),
            actual = product,
            absoluteTolerance = eps,
        )

        assertEqualsWithTolerance(
            expected = product,
            actual = pb * pa,
            absoluteTolerance = eps,
        )
    }
}
