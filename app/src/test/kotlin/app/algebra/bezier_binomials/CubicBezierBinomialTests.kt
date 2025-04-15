package app.algebra.bezier_binomials

import app.algebra.linear.VectorSpace
import app.algebra.polynomials.CubicPolynomial
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CubicBezierBinomialTests {
    private val eps = 10e-5

    @Test
    fun testToPolynomialFormulaCubic() {
        val bezierBinomial = CubicBezierBinomial(
            vectorSpace = VectorSpace.DoubleVectorSpace,
            weight0 = -11.0,
            weight1 = 3.4,
            weight2 = 14.2,
            weight3 = -7.6,
        )

        val cubicPolynomial = assertIs<CubicPolynomial>(
            bezierBinomial.toPolynomialFormulaCubic(),
        )

        bezierBinomial.sample(
            strategy = RealFunction.SamplingStrategy(sampleCount = 10),
        ).forEach { sample ->
            assertEquals(
                expected = sample.value,
                actual = cubicPolynomial.apply(sample.x),
                absoluteTolerance = eps,
            )
        }
    }
}
