package app.algebra

import app.algebra.NumericObject.Tolerance
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NumericObjectTests {
    private val tinyValue = 1.1235e-8
    private val smallValue = 10.12
    private val bigValue = 1.1876e10

    private fun testAbsoluteToleranceNearby(
        baseValue: Double,
        tolerance: Tolerance.Absolute,
    ) {
        val belowEps = tolerance.absoluteTolerance * 0.99
        val aboveEps = tolerance.absoluteTolerance * 1.01

        // Smaller, but within tolerance
        assertTrue(
            tolerance.equalsApproximately(
                value = baseValue,
                reference = baseValue - belowEps,
            ),
        )

        // Exactly the same
        assertTrue(
            tolerance.equalsApproximately(
                value = baseValue,
                reference = baseValue,
            ),
        )

        // Bigger, but within tolerance
        assertTrue(
            tolerance.equalsApproximately(
                value = baseValue,
                reference = baseValue + belowEps,
            ),
        )

        // Smaller, not within tolerance
        assertFalse(
            tolerance.equalsApproximately(
                value = baseValue,
                reference = baseValue - aboveEps,
            ),
        )

        // Bigger, not within tolerance
        assertFalse(
            tolerance.equalsApproximately(
                value = baseValue,
                reference = baseValue + aboveEps,
            ),
        )
    }

    private fun testRelativeToleranceNearby(
        reference: Double,
        tolerance: Tolerance.Relative,
    ) {
        val r = tolerance.relativeTolerance
        assert(r > 0.0 && r < 0.2)

        val scaleDownWithin = 1.0 - (r * 0.99)
        val scaleDownOutside = 1.0 - (r * 1.01)
        val scaleUpWithin = 1.0 + (r * 0.99)
        val scaleUpOutside = 1.0 + (r * 1.01)

        assert(scaleDownWithin > 0.8 && scaleDownWithin < 1.0 && scaleDownOutside < scaleDownWithin)
        assert(scaleUpWithin > 1.0 && scaleUpWithin < 1.2 && scaleUpOutside > scaleUpWithin)

        // Smaller, but within tolerance
        val smallerValueWithin = reference * scaleDownWithin
        assert(smallerValueWithin < reference)

        assertTrue(
            tolerance.equalsApproximately(
                value = smallerValueWithin,
                reference = reference,
            ),
        )

        // Exactly the same
        assertTrue(
            tolerance.equalsApproximately(
                value = reference,
                reference = reference,
            ),
        )

        val biggerValueWithin = reference * scaleUpWithin
        assert(biggerValueWithin > reference)

        // Bigger, but within tolerance
        assertTrue(
            tolerance.equalsApproximately(
                value = biggerValueWithin,
                reference = reference,
            ),
        )

        val smallerValueOutside = reference * scaleDownOutside
        assert(smallerValueWithin > 0.0 && smallerValueWithin < reference)

        // Smaller, not within tolerance
        assertFalse(
            tolerance.equalsApproximately(
                value = smallerValueOutside,
                reference = reference,
            ),
        )

        val biggerValueOutside = reference * scaleUpOutside
        assert(biggerValueOutside > reference)

        // Bigger, not within tolerance
        assertFalse(
            tolerance.equalsApproximately(
                value = biggerValueOutside,
                reference = reference,
            ),
        )
    }

    @Test
    fun testAbsoluteTolerance_zero() {
        val tolerance = Tolerance.Absolute(
            absoluteTolerance = 10e-4,
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = 10e-4 - 10e-5,
                reference = 0.0,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = 0.0,
                reference = 10e-4 - 10e-5,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = 10e-4 + 10e-5,
                reference = 0.0,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = 0.0,
                reference = 10e-4 + 10e-5,
            ),
        )
    }

    @Test
    fun testAbsoluteTolerance_tiny() {
        val tolerance = Tolerance.Absolute(
            absoluteTolerance = 10e-8,
        )

        // Tiny value

        testAbsoluteToleranceNearby(
            baseValue = tinyValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = smallValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = bigValue,
            ),
        )

        // Small value

        testAbsoluteToleranceNearby(
            baseValue = smallValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = bigValue,
            ),
        )
    }

    @Test
    fun testAbsoluteTolerance_small() {
        val tolerance = Tolerance.Absolute(
            absoluteTolerance = 10e-2,
        )

        // Tiny value

        testAbsoluteToleranceNearby(
            baseValue = tinyValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = smallValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = bigValue,
            ),
        )

        // Small value

        testAbsoluteToleranceNearby(
            baseValue = smallValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = bigValue,
            ),
        )

        // Big value

        testAbsoluteToleranceNearby(
            baseValue = bigValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = bigValue,
                reference = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = bigValue,
                reference = smallValue,
            ),
        )
    }

    @Test
    fun testAbsoluteTolerance_big() {
        val tolerance = Tolerance.Absolute(
            absoluteTolerance = 100.0,
        )

        // Tiny value

        testAbsoluteToleranceNearby(
            baseValue = tinyValue,
            tolerance = tolerance,
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = smallValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = bigValue,
            ),
        )

        // Small value

        testAbsoluteToleranceNearby(
            baseValue = smallValue,
            tolerance = tolerance,
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = bigValue,
            ),
        )

        // Big value

        testAbsoluteToleranceNearby(
            baseValue = bigValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = bigValue,
                reference = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = bigValue,
                reference = smallValue,
            ),
        )
    }

    @Test
    @Ignore("Figure out zeros")
    fun testRelativeTolerance_zero() {
        val tolerance = Tolerance.Relative(
            relativeTolerance = 10e-6,
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = 0.0,
                reference = 10e-20,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = 0.0,
                reference = 10e-16 + 10e-17
            ),
        )


        assertTrue(
            tolerance.equalsApproximately(
                value = 10e-16 - 10e-17,
                reference = 0.0,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = 10e-16 + 10e-17,
                reference = 0.0,
            ),
        )

    }

    @Test
    fun testRelativeTolerance_tiny() {
        val tolerance = Tolerance.Relative(
            relativeTolerance = 10e-8,
        )

        // Tiny value

        testRelativeToleranceNearby(
            reference = tinyValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = smallValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = bigValue,
            ),
        )

        // Small value

        testRelativeToleranceNearby(
            reference = smallValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = bigValue,
            ),
        )
    }

    @Test
    fun testRelativeTolerance_small() {
        val tolerance = Tolerance.Relative(
            relativeTolerance = 10e-2,
        )

        // Small value

        testRelativeToleranceNearby(
            reference = smallValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = bigValue,
            ),
        )

        // Big value

        testRelativeToleranceNearby(
            reference = bigValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = bigValue,
                reference = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = bigValue,
                reference = smallValue,
            ),
        )
    }

    @Test
    fun testRelativeTolerance_byExamples() {
        val tolerance = Tolerance.Relative(
            relativeTolerance = 10e-4,
        )

        val tinyValueUpOutside = 1.12523e-8
        val tinyValueUpWithin = 1.1235e-8
        val tinyValue1 = 1.123e-8
        val tinyValueDownWithin = 1.1224e-8
        val tinyValueDownOutside = 1.120e-8

        assertFalse(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = tinyValueUpOutside,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = tinyValueUpWithin,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = tinyValue1,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = tinyValueDownWithin,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = tinyValue,
                reference = tinyValueDownOutside,
            ),
        )

        val smallValueUpOutside = 10.131
        val smallValueUpWithin = 10.129
        val smallValue1 = 10.12
        val smallValueDownWithin = 10.11
        val smallValueDownOutside = 10.108

        assertFalse(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = smallValueUpOutside,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = smallValueUpWithin,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = smallValue1,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = smallValueDownWithin,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = smallValue,
                reference = smallValueDownOutside,
            ),
        )

        val bigValueUpOutside = 1.1896e10
        val bigValueUpWithin = 1.1887e10
        val bigValue1 = 1.1876e10
        val bigValueDownWithin = 1.1866e10
        val bigValueDownOutside = 1.1863e10

        assertFalse(
            tolerance.equalsApproximately(
                value = bigValue,
                reference = bigValueUpOutside,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = bigValue,
                reference = bigValueUpWithin,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = bigValue,
                reference = bigValue1,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                value = bigValue,
                reference = bigValueDownWithin,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                value = bigValue,
                reference = bigValueDownOutside,
            ),
        )
    }
}
