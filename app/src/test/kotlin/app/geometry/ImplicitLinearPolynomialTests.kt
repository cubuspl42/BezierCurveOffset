package app.geometry

import app.algebra.assertEqualsWithAbsoluteTolerance
import app.algebra.polynomials.ParametricPolynomial
import app.algebra.polynomials.Polynomial
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

        assertEqualsWithAbsoluteTolerance(
            expected = ImplicitQuadraticPolynomial.of(
                a2 = 6.35,
                a1b1 = 2.18,
                b2 = -0.4975,
                a1 = 133.1,
                b1 = -21.02,
                c = -5.25,
            ),
            actual = product,
            absoluteTolerance = eps,
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
            xFunction = Polynomial.linear(
                a1 = -0.45,
                a0 = 50.0,
            ),
            yFunction = Polynomial.linear(
                a1 = 0.89,
                a0 = 0.0,
            ),
        )

        val finalP = lg0.put(pp)

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.linear(
                a1 = -0.801,
                a0 = 89.0,
            ),
            actual = finalP,
            absoluteTolerance = Constants.epsilon,
        )
    }
}
