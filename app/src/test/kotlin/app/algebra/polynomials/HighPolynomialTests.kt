package app.algebra.polynomials

import app.algebra.NumericObject.Tolerance
import app.algebra.assertEqualsWithAbsoluteTolerance
import app.algebra.assertEqualsWithTolerance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class HighPolynomialTests {
    private val eps = 10e-5

    @Test
    fun testTimes_constant() {
        val pa = Polynomial.of(
            -4.0, 3.0, -2.0, 1.0, 6.0, 12.2
        )

        val pb = Polynomial.constant(
            a0 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.of(
                -8.0,
                6.0,
                -4.0,
                2.0,
                12.0,
                24.4,
            ),
            actual = product,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testTimes_quadratic() {
        val pa = Polynomial.of(
            -4.0,
            3.0,
            -2.0,
            1.0,
            17.9,
            -2.3,
        )

        val pb = Polynomial.quadratic(
            a0 = 4.0,
            a1 = -1.0,
            a2 = 2.0,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.of(
                -16.0,
                16.0,
                -19.0,
                12.0,
                66.6,
                -25.1,
                38.1,
                -4.6,
            ),
            actual = product,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testTimes_cubic() {
        val pa = Polynomial.of(
            -4.0,
            3.0,
            -2.0,
            1.0,
            18.9,
        )

        val pb = Polynomial.cubic(
            a0 = -3.0,
            a1 = 4.0,
            a2 = -1.0,
            a3 = 2.0,
        )

        val product = pa * pb

        val expectedPolynomial = Polynomial.of(
            12.0,
            -25.0,
            22.0,
            -22.0,
            -44.7,
            70.6,
            -16.9,
            37.8,
        )

        assertEqualsWithAbsoluteTolerance(
            expected = expectedPolynomial,
            actual = product,
            absoluteTolerance = eps,
        )

        assertEquals(
            expected = product,
            actual = pb * pa,
        )
    }

    @Test
    fun testTimes_high() {
        val pa = Polynomial.of(
            -4.0,
            3.0,
            -2.0,
            1.0,
            18.9,
            -2.0,
        )

        val pb = Polynomial.of(
            -4.0,
            23.0,
            -2.2,
            1.0,
            77.2,
        )

        val product = pa * pb

        assertEqualsWithAbsoluteTolerance(
            expected = Polynomial.of(
                16.0,
                -104.0,
                85.8,
                -60.6,
                -354.0,
                670.1,
                -240.98,
                100.5,
                1457.08,
                -154.4,
            ),
            actual = product,
            absoluteTolerance = eps,
        )
    }

    @Test
    fun testDerivative() {
        val highPolynomial = Polynomial.of(
            1.0, 2.1, 3.4, 4.5, 5.7, 6.7
        )

        val derivative = assertNotNull(
            highPolynomial.derivative,
        )

        assertEqualsWithTolerance(
            expected = Polynomial.of(
                2.1,
                6.8,
                13.5,
                22.8,
                33.5,
            ),
            actual = derivative,
            tolerance = Tolerance.Absolute(absoluteTolerance = eps),
        )
    }

    @Test
    fun testDivide() {
        val highPolynomial = Polynomial.of(
            -6.0,
            11.0,
            -6.0,
            1.0,
        )

        val (quotient, remainder) = assertNotNull(
            highPolynomial.divide(x0 = 1.0),
        )

        val tolerance = Tolerance.Absolute(absoluteTolerance = eps)

        assertEqualsWithTolerance(
            expected = Polynomial.of(
                6.0,
                -5.0,
                1.0,
            ),
            actual = quotient,
            tolerance = tolerance,
        )

        assertEqualsWithTolerance(
            expected = 0.0,
            actual = remainder,
            tolerance = tolerance,
        )
    }

    @Test
    fun testFindRoots() {
        val highPolynomial = Polynomial.of(
            -4.05318211480636e+17,
            1.33720916235669e+19,
            -1.74033656459737e+20,
            1.18641205512086e+21,
            -4.72731353333192e+21,
            1.15564116811744e+22,
            -1.75176752296017e+22,
            1.60246744255751e+22,
            -8.09146929050218e+21,
            1.73006535868332e+21,
        ) as HighPolynomial

        val expectedRoots = listOf(
            0.08321298331285831,
            0.1435234395326374,
            0.22787694791806082,
            0.40251769663008713,
            0.43011874465177913,
            0.6822325289916767,
            0.8142156752930875,
            0.9147383049567882,
            0.9785368635066114,
        )

        val tolerance = Tolerance.Absolute(
            absoluteTolerance = 10e-11,
        )

        val roots = highPolynomial.findRoots()

        assertEqualsWithTolerance(
            expected = expectedRoots,
            actual = roots.sorted(),
            tolerance = tolerance,
        )
    }
}
