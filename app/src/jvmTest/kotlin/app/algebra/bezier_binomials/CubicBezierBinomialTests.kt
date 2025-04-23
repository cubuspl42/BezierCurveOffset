package app.algebra.bezier_binomials

import app.algebra.NumericObject
import app.algebra.Ratio
import app.algebra.assertEqualsWithAbsoluteTolerance
import app.algebra.assertEqualsWithRelativeTolerance
import app.algebra.assertEqualsWithTolerance
import app.algebra.euclidean.bezier_binomials.CubicBezierBinomial
import app.algebra.euclidean.bezier_binomials.RealFunction
import app.algebra.euclidean.bezier_binomials.sample
import app.algebra.implicit_polynomials.ImplicitCubicPolynomial
import app.algebra.polynomials.HighPolynomial
import app.algebra.polynomials.ParametricPolynomial
import app.geometry.Point
import app.geometry.RawVector
import app.SvgCurveExtractionUtils
import java.awt.Color
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotNull

private const val eps = 10e-4

class CubicBezierBinomialTests {
    private val loopWaveIntersectionCurveSet = SvgCurveExtractionUtils.extractCurves(
        clazz = CubicBezierBinomialTests::class.java,
        resourceName = "loopWaveIntersection.svg",
    )

    private val loopCurve = loopWaveIntersectionCurveSet.getBezierCurveByColor(
        color = Color(0x3399CC),
    )

    private val waveCurve = loopWaveIntersectionCurveSet.getBezierCurveByColor(
        color = Color(0xCCFF33),
    )

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
    fun testInvert() {
        val bezierCurveBasis = CubicBezierBinomial(
            weight0 = RawVector(1.0, 0.0),
            weight1 = RawVector(5.0, 0.0),
            weight2 = RawVector(5.0, 2.0),
            weight3 = RawVector(4.0, 3.0),
        )

        val invertedPolynomial = assertNotNull(
            bezierCurveBasis.invert(),
        )

        val samples = bezierCurveBasis.sample(
            strategy = RealFunction.SamplingStrategy(
                sampleCount = 1000,
            ),
        )

        val tolerance = NumericObject.Tolerance.Absolute(
            absoluteTolerance = 10e-7,
        )

        samples.forEach {
            val t = it.x
            val p = it.value
            val ratio = invertedPolynomial.apply(p)

            if (ratio.equalsWithTolerance(Ratio.ZeroByZero, tolerance = tolerance)) {
                return@forEach
            }

            assertEqualsWithTolerance(
                expected = t,
                actual = ratio.value,
                tolerance = tolerance,
            )
        }

        assertEqualsWithTolerance(
            expected = Ratio.ZeroByZero,
            actual = invertedPolynomial.apply(RawVector(1.0, 0.0)),
            tolerance = tolerance,
        )
    }

    @Test
    fun testImplicitize() {
        val bezierCurveBasis = loopCurve.basisFormula

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

        val intersectionPolynomial = implicitCubicPolynomial.substitute(
            waveCurve.basisFormula.toParametricPolynomial(),
        )

        assertIs<HighPolynomial>(intersectionPolynomial)
    }

    @Test
    fun testSolveIntersections() {
        val intersectionWaveTValues = waveCurve.basisFormula.solveIntersections(
            other = loopCurve.basisFormula,
        )

        assertEqualsWithTolerance(
            expected = listOf(
                0.9785368635066114,
                0.9147383049567882,
                0.8142156752930875,
                0.6822325289916767,
                0.43011874465177913,
                0.40251769663008713,
                0.22787694791806082,
                0.1435234395326374,
                0.08321298331285831,
            ).sorted(),
            actual = intersectionWaveTValues.sorted(),
            tolerance = NumericObject.Tolerance.Absolute(
                absoluteTolerance = 10e-11,
            ),
        )

        assertEqualsWithTolerance(
            expected = listOf(
                Point.of(590.517423417456, 365.351468585959),
                Point.of(569.693769676009, 289.328026231261),
                Point.of(544.115031887314, 226.794563840263),
                Point.of(520.705506672753, 223.610007522960),
                Point.of(491.270173012666, 324.946476047408),
                Point.of(488.126372683975, 335.012776278537),
                Point.of(462.841051803370, 345.574224761226),
                Point.of(445.046899204276, 298.915662971792),
                Point.of(429.096911279908, 236.478283237304),
            ).sortedBy { it.x },
            actual = intersectionWaveTValues.map { t ->
                waveCurve.evaluate(t = t)
            }.sortedBy { it.x },
            tolerance = NumericObject.Tolerance.Absolute(
                absoluteTolerance = 10e-8,
            ),
        )
    }
}
