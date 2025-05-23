package app.geometry

import app.algebra.assertEqualsWithAbsoluteTolerance
import app.algebra.implicit_polynomials.ImplicitCubicPolynomial
import app.algebra.implicit_polynomials.ImplicitLinearPolynomial
import app.algebra.implicit_polynomials.ImplicitQuadraticPolynomial
import app.algebra.polynomials.ParametricPolynomial
import app.algebra.polynomials.Polynomial
import kotlin.test.Test
import kotlin.test.assertEquals

class ImplicitQuadraticPolynomialTests {
    private val eps = 10e-4

    @Test
    fun testTimes_linear() {
        val a = ImplicitQuadraticPolynomial.of(
            a2 = 12.7,
            a1b1 = -1.99,
            b2 = -0.5,
            a1 = 0.5,
            b1 = 0.25,
            c = 10.5,
        )

        val b = ImplicitLinearPolynomial.of(
            a1 = 0.5,
            b1 = 0.25,
            c = 10.5,
        )

        val product = a * b

        assertEqualsWithAbsoluteTolerance(
            expected = ImplicitCubicPolynomial.of(
                a3 = 6.35,
                a2b1 = 2.18,
                a1b2 = -0.7475,
                b3 = -0.125,
                a2 = 133.6,
                a1b1 = -20.645,
                b2 = -5.1875,
                a1 = 10.5,
                b1 = 5.25,
                c = 110.25,
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

        val finalP = lg0.substitute(pp)

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
