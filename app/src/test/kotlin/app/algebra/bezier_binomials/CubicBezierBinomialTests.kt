package app.algebra.bezier_binomials

import app.algebra.assertEqualsWithAbsoluteTolerance
import app.algebra.assertEqualsWithRelativeTolerance
import app.algebra.polynomials.HighPolynomial
import app.algebra.polynomials.ParametricPolynomial
import app.geometry.ImplicitCubicPolynomial
import app.geometry.RawVector
import app.geometry.SvgCurveExtractionUtils
import java.awt.Color
import kotlin.test.Test
import kotlin.test.assertIs

private const val eps = 10e-4

class CubicBezierBinomialTests {

    @Test
    fun testToPolynomialFormulaCubic() {
        val bezierBinomial = CubicBezierBinomial(
            weight0 = RawVector(-11.0, 0.0),
            weight1 = RawVector(3.4, 0.0),
            weight2 = RawVector(14.2, 0.0),
            weight3 = RawVector(-7.6, 0.0),
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

    @Test
    fun testImplicitize() {
        val extractedCurveSet = SvgCurveExtractionUtils.extractCurves(
            clazz = CubicBezierBinomialTests::class.java,
            resourceName = "loopWaveIntersection.svg",
        )

        val bezierCurve = extractedCurveSet.getBezierCurveByColor(
            color = Color(0x3399CC),
        )

        val bezierCurve2 = extractedCurveSet.getBezierCurveByColor(
            color = Color(0xCCFF33),
        )

        val bezierCurveBasis = bezierCurve.basisFormula

        val implicitCubicPolynomial = bezierCurveBasis.implicitize()

        assertEqualsWithRelativeTolerance(
            expected = ImplicitCubicPolynomial.of(
                a3 = -14.9783841620665,
                a2b1 = 72520.0239738822,
                a1b2 = -117038745.468702,
                b3 = 62962233004.7317,
                a2 = -1424546570533.26,
                a1b1 = 49488957526.3629,
                b2 = -55451477620472.4,
                a1 = 1.42286949475598e+15,
                b1 = 1.60502408149014e+16,
                c = -1.87768253678108e+18,
            ),
            actual = implicitCubicPolynomial,
            relativeTolerance = eps,
        )

        val intersectionPolynomial = implicitCubicPolynomial.put(
            bezierCurve2.basisFormula.toParametricPolynomial(),
        )

        assertIs<HighPolynomial>(intersectionPolynomial)
    }
}
