package app.algebra.bezier_binomials

import app.algebra.assertEqualsWithTolerance
import app.algebra.polynomials.ParametricPolynomial
import app.geometry.ImplicitCubicPolynomial
import app.geometry.RawVector
import app.geometry.SvgCurveExtractionUtils
import java.awt.Color
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CubicBezierBinomialTests {
    private val eps = 10e-4

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
            assertEqualsWithTolerance(
                expected = sample.value,
                actual = parametricPolynomial.apply(sample.x),
                tolerance = eps,
            )
        }
    }

    @Test
    @Ignore // TODO: rtol
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

        val finalPolynomial = implicitCubicPolynomial.put(bezierCurve2.basisFormula.toParametricPolynomial())

        // -14.9783841620665*x**3 + 72520.0239738822*x**2*y - 1424546570533.26*x**2 - 117038745.468702*x*y**2 + 49488957526.3629*x*y + 1.42286949475598e+15*x + 62962233004.7317*y**3 - 55451477620472.4*y**2 + 1.60502408149014e+16*y - 1.87768253678108e+18

        // a3 = -14.978384153917432
        // a2b1 = 72520.02397376299
        // a1b2 = -1.1703874546870059E8
        // b3 = 6.2962233004731705E10
        // a2 = -1.4245465705332568E12
        // a1b1 = 4.9488957526362305E10
        // b2 = -5.545147762047243E13
        // a1 = 1.4228694947559742E15
        // b1 = 1.6050240814901382E16
        // c = -1.8776825367810824E18

        // -14.9783841620665*x**3 + 72520.0239738822*x**2*y - 1424546570533.26*x**2 - 117038745.468702*x*y**2 + 49488957526.3629*x*y + 1.42286949475598e+15*x + 62962233004.7317*y**3 - 55451477620472.4*y**2 + 1.60502408149014e+16*y - 1.87768253678108e+18
        val expectedImplicitCubicPolynomial = ImplicitCubicPolynomial.of(
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
        )

        assertEqualsWithTolerance(
            expected = expectedImplicitCubicPolynomial,
            actual = implicitCubicPolynomial,
            tolerance = eps,
        )

        val samples = bezierCurveBasis.sample(
            strategy = RealFunction.SamplingStrategy(sampleCount = 100),
        )

        val values = samples.map {
            implicitCubicPolynomial.apply(it.value)
        }

        samples.forEach { sample ->
            assertEquals(
                expected = 0.0,
                actual = implicitCubicPolynomial.apply(sample.value),
                absoluteTolerance = eps,
            )
        }
    }
}
