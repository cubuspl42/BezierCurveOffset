package app.geometry

import app.algebra.assertEqualsWithAbsoluteTolerance
import app.algebra.polynomials.LinearPolynomial
import app.algebra.polynomials.ParametricPolynomial
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ParametricLineFunctionTests {
    @Test
    fun testSolve() {
        val l0 = ParametricLineFunction(
            s = RawVector(50.0, 0.0),
            d = RawVector(-0.45, 0.89),
        )

        val l1 = ParametricLineFunction(
            s = RawVector(-50.0, 0.0),
            d = RawVector(0.45, 0.89),
        )

        val t0 = assertNotNull(l0.solve(l1))

        val p0 = l0.apply(t0)

        assertEquals(
            expected = 0.0,
            actual = p0.x,
            absoluteTolerance = Constants.epsilon,
        )
    }

    @Test
    fun testToGeneralLineFunction() {
        val l0 = ParametricLineFunction(
            s = RawVector(50.0, 0.0),
            d = RawVector(-0.45, 0.89),
        )

        val lg0 = l0.toGeneralLineFunction()

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
                xFunction = LinearPolynomial.of(
                    a1 = -0.45,
                    a0 = 50.0,
                ),
                yFunction = LinearPolynomial.of(
                    a1 = 0.89,
                    a0 = 0.0,
                ),
            ),
            actual = lp0,
            absoluteTolerance = Constants.epsilon,
        )
    }
}
