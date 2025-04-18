package app.geometry

import app.algebra.assertEqualsWithTolerance
import app.algebra.polynomials.LinearPolynomial
import app.algebra.polynomials.ParametricPolynomial
import kotlin.test.Test
import kotlin.test.assertEquals

class ImplicitLinearPolynomialTests {
    private val eps = 10e-4

    @Test
    fun testTimes_linear() {
        val a = ImplicitLinearPolynomial.of(
            a1 = 12.7,
            b1 = -1.99,
            c = -0.5,
        )

        val b = ImplicitLinearPolynomial.of(
            a1 = 0.5,
            b1 = 0.25,
            c = 10.5,
        )

        val product = a * b

        assertEqualsWithTolerance(
            expected = ImplicitQuadraticPolynomial.of(
                a2 = 6.35,
                a1b1 = 2.18,
                b2 = -0.4975,
                a1 = 133.1,
                b1 = -21.02,
                c = -5.25,
            ),
            actual = product,
            tolerance = eps,
        )

        assertEquals(
            expected = product,
            actual = b * a,
        )
    }


    @Test
    fun testPut() {
        val lg0 = ImplicitLinearPolynomial(
            a1 = 0.89,
            b1 = -0.45,
            c = 44.5,
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
            tolerance = Constants.epsilon,
        )
    }
}
