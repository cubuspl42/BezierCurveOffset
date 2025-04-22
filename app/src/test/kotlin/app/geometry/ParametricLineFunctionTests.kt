package app.geometry

import app.algebra.assertEqualsWithAbsoluteTolerance
import app.algebra.implicit_polynomials.ImplicitLinearPolynomial
import app.algebra.polynomials.ParametricPolynomial
import app.algebra.polynomials.Polynomial
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ParametricLineFunctionTests {
    @Test
    fun testSolveIntersection() {
        val l0 = ParametricLineFunction(
            s = RawVector(50.0, 0.0),
            d = RawVector(-0.45, 0.89),
        )

        val l1 = ParametricLineFunction(
            s = RawVector(-50.0, 0.0),
            d = RawVector(0.45, 0.89),
        )

        val t0 = assertNotNull(l0.solveIntersection(l1))

        val p0 = l0.apply(t0)

        assertEquals(
            expected = 0.0,
            actual = p0.x,
            absoluteTolerance = Constants.epsilon,
        )
    }

    @Test
    fun testImplicitize() {
        val l0 = ParametricLineFunction(
            s = RawVector(50.0, 0.0),
            d = RawVector(-0.45, 0.89),
        )

        val lg0 = l0.implicitize()

        assertEqualsWithAbsoluteTolerance(
            expected = ImplicitLinearPolynomial(
                a1 = 0.89,
                b1 = 0.45,
                c = -44.5,
            ),
            actual = lg0,
            absoluteTolerance = Constants.epsilon,
        )
    }

    @Test
    fun testToParametricPolynomial() {
        val l0 = ParametricLineFunction(
            s = RawVector(50.0, 0.0),
            d = RawVector(-0.45, 0.89),
        )

        val lp0 = l0.toParametricPolynomial()

        assertEqualsWithAbsoluteTolerance(
            expected = ParametricPolynomial(
                xFunction = Polynomial.linear(
                    a1 = -0.45,
                    a0 = 50.0,
                ),
                yFunction = Polynomial.linear(
                    a1 = 0.89,
                    a0 = 0.0,
                ),
            ),
            actual = lp0,
            absoluteTolerance = Constants.epsilon,
        )
    }
}
