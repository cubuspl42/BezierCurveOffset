package app.geometry

import app.algebra.assertEqualsWithTolerance
import app.algebra.polynomials.LinearPolynomial
import app.algebra.polynomials.ParametricPolynomial
import kotlin.test.Test

class ImplicitLinearPolynomialTests {
    @Test
    fun testPut() {
        val lg0 = ImplicitLinearPolynomial(
            a2 = 0.89,
            a1 = -0.45,
            a0 = 44.5,
        )

        val pp = ParametricPolynomial(
            xFunction = LinearPolynomial.of(
                a = -0.45,
                b = 50.0,
            ),
            yFunction = LinearPolynomial.of(
                a = 0.89,
                b = 0.0,
            ),
        )

        val finalP = lg0.put(pp)

        assertEqualsWithTolerance(
            expected = LinearPolynomial.of(
                a = -0.801,
                b = 89.0,
            ),
            actual = finalP,
            absoluteTolerance = Constants.epsilon,
        )
    }
}
