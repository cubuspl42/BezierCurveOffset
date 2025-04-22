package app.algebra.bezier_binomials

import app.algebra.assertEqualsWithAbsoluteTolerance
import app.algebra.euclidean.bezier_binomials.QuadraticBezierBinomial
import app.algebra.euclidean.bezier_binomials.RealFunction
import app.algebra.euclidean.bezier_binomials.sample
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
            assertEqualsWithAbsoluteTolerance(
                expected = sample.value,
                actual = parametricPolynomial.apply(sample.x),
                absoluteTolerance = eps,
            )
        }
    }
}
