package app.algebra.bezier_binomials

import app.algebra.linear.VectorSpace
import app.algebra.polynomials.CubicPolynomial
import app.algebra.polynomials.QuadraticPolynomial
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class QuadraticBezierBinomialTests {
    private val eps = 10e-5

    @Test
    fun testToPolynomialFormulaCubic() {
        val bezierBinomial = QuadraticBezierBinomial(
            vectorSpace = VectorSpace.DoubleVectorSpace,
            weight0 = -11.0,
            weight1 = 3.4,
            weight2 = 14.2,
        )

        val quadraticPolynomial = assertIs<QuadraticPolynomial>(
            bezierBinomial.toPolynomialFormulaQuadratic(),
        )

        bezierBinomial.sample(
            strategy = RealFunction.SamplingStrategy(sampleCount = 10),
        ).forEach { sample ->
            assertEquals(
                expected = sample.value,
                actual = quadraticPolynomial.apply(sample.x),
                absoluteTolerance = eps,
            )
        }
    }
}
