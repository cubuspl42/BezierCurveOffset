package app.algebra.bezier_binomials

import app.algebra.assertEqualsWithTolerance
import app.algebra.polynomials.ParametricPolynomial
import app.geometry.RawVector
import kotlin.test.Test
import kotlin.test.assertIs

class QuadraticBezierBinomialTests {
    private val eps = 10e-5

    @Test
    fun testToPolynomialFormulaCubic() {
        val bezierBinomial = QuadraticBezierBinomial(
            weight0 = RawVector(-11.0, 0.0),
            weight1 = RawVector(3.4, 0.0),
            weight2 = RawVector(14.2, 0.0),
        )

        val parametricPolynomial = assertIs<ParametricPolynomial>(
            bezierBinomial.toParametricPolynomial(),
        )

        bezierBinomial.sample(
            strategy = RealFunction.SamplingStrategy(sampleCount = 10),
        ).forEach { sample ->
            assertEqualsWithTolerance(
                expected = sample.value,
                actual = parametricPolynomial.apply(sample.x),
                tolerance = eps,
            )
        }
    }
}
