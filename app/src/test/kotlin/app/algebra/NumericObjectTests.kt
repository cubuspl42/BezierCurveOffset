package app.algebra

import app.algebra.NumericObject.Tolerance
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NumericObjectTests {
    private val tinyValue = 1.1235e-8
    private val smallValue = 10.12
    private val bigValue = 1.1876e10

    private fun testtoleranceNearby(
        baseValue: Double,
        tolerance: Tolerance.Absolute,
    ) {
        val belowEps = tolerance.absoluteTolerance * 0.99
        val aboveEps = tolerance.absoluteTolerance * 1.01

        // Smaller, but within tolerance
        assertTrue(
            tolerance.equalsApproximately(
                first = baseValue,
                second = baseValue - belowEps,
            ),
        )

        // Exactly the same
        assertTrue(
            tolerance.equalsApproximately(
                first = baseValue,
                second = baseValue,
            ),
        )

        // Bigger, but within tolerance
        assertTrue(
            tolerance.equalsApproximately(
                first = baseValue,
                second = baseValue + belowEps,
            ),
        )

        // Smaller, not within tolerance
        assertFalse(
            tolerance.equalsApproximately(
                first = baseValue,
                second = baseValue - aboveEps,
            ),
        )

        // Bigger, not within tolerance
        assertFalse(
            tolerance.equalsApproximately(
                first = baseValue,
                second = baseValue + aboveEps,
            ),
        )
    }

    private fun testRelativeToleranceNearby(
        baseValue: Double,
        tolerance: Tolerance.Relative,
    ) {
        val r = tolerance.relativeTolerance
        assert(r > 0.0 && r < 0.2)

        val d = r / (1.0 - r)
        assert(d > 0.0 && d < 0.2)

        val scaleWithin = 1.0 + d * 0.99
        val scaleOutside = 1.0 + d * 1.01

//        assert(scaleWithin < (1.0 + r) && scaleWithin > 1.0)
//        assert(scaleOutside > (1.0 + r) && scaleOutside < 1.1)

        // Smaller, but within tolerance
        val smallerValueWithin = baseValue / scaleWithin
        assert(smallerValueWithin > 0.0 && smallerValueWithin < baseValue)

        assertTrue(
            tolerance.equalsApproximately(
                first = baseValue,
                second = smallerValueWithin,
            ),
        )

        // Exactly the same
        assertTrue(
            tolerance.equalsApproximately(
                first = baseValue,
                second = baseValue,
            ),
        )

        val biggerValueWithin = baseValue * scaleWithin
        assert(biggerValueWithin > baseValue)

        // Bigger, but within tolerance
        assertTrue(
            tolerance.equalsApproximately(
                first = baseValue,
                second = biggerValueWithin,
            ),
        )

        val smallerValueOutside = baseValue / scaleOutside
        assert(smallerValueWithin > 0.0 && smallerValueWithin < baseValue)

        // Smaller, not within tolerance
        assertFalse(
            tolerance.equalsApproximately(
                first = baseValue,
                second = smallerValueOutside,
            ),
        )

        val biggerValueOutside = baseValue * scaleOutside
        assert(biggerValueOutside > baseValue)

        // Bigger, not within tolerance
        assertFalse(
            tolerance.equalsApproximately(
                first = baseValue,
                second = biggerValueOutside,
            ),
        )
    }

    @Test
    fun testtolerance_tiny() {
        val tolerance = Tolerance.Absolute(
            absoluteTolerance = 10e-8,
        )

        // Tiny value

        testtoleranceNearby(
            baseValue = tinyValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = tinyValue,
                second = smallValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = tinyValue,
                second = bigValue,
            ),
        )

        // Small value

        testtoleranceNearby(
            baseValue = smallValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = smallValue,
                second = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = smallValue,
                second = bigValue,
            ),
        )
    }

    @Test
    fun testtolerance_small() {
        val tolerance = Tolerance.Absolute(
            absoluteTolerance = 10e-2,
        )

        // Tiny value

        testtoleranceNearby(
            baseValue = tinyValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = tinyValue,
                second = smallValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = tinyValue,
                second = bigValue,
            ),
        )

        // Small value

        testtoleranceNearby(
            baseValue = smallValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = smallValue,
                second = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = smallValue,
                second = bigValue,
            ),
        )

        // Big value

        testtoleranceNearby(
            baseValue = bigValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = bigValue,
                second = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = bigValue,
                second = smallValue,
            ),
        )
    }

    @Test
    fun testtolerance_big() {
        val tolerance = Tolerance.Absolute(
            absoluteTolerance = 100.0,
        )

        // Tiny value

        testtoleranceNearby(
            baseValue = tinyValue,
            tolerance = tolerance,
        )

        assertTrue(
            tolerance.equalsApproximately(
                first = tinyValue,
                second = smallValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = tinyValue,
                second = bigValue,
            ),
        )

        // Small value

        testtoleranceNearby(
            baseValue = smallValue,
            tolerance = tolerance,
        )

        assertTrue(
            tolerance.equalsApproximately(
                first = smallValue,
                second = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = smallValue,
                second = bigValue,
            ),
        )

        // Big value

        testtoleranceNearby(
            baseValue = bigValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = bigValue,
                second = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = bigValue,
                second = smallValue,
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
            baseValue = tinyValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = tinyValue,
                second = smallValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = tinyValue,
                second = bigValue,
            ),
        )

        // Small value

        testRelativeToleranceNearby(
            baseValue = smallValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = smallValue,
                second = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = smallValue,
                second = bigValue,
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
            baseValue = smallValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = smallValue,
                second = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = smallValue,
                second = bigValue,
            ),
        )

        // Big value

        testRelativeToleranceNearby(
            baseValue = bigValue,
            tolerance = tolerance,
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = bigValue,
                second = tinyValue,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = bigValue,
                second = smallValue,
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
                first = tinyValue,
                second = tinyValueUpOutside,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                first = tinyValue,
                second = tinyValueUpWithin,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                first = tinyValue,
                second = tinyValue1,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                first = tinyValue,
                second = tinyValueDownWithin,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = tinyValue,
                second = tinyValueDownOutside,
            ),
        )

        val smallValueUpOutside = 10.131
        val smallValueUpWithin = 10.129
        val smallValue1 = 10.12
        val smallValueDownWithin = 10.11
        val smallValueDownOutside = 10.108

        assertFalse(
            tolerance.equalsApproximately(
                first = smallValue,
                second = smallValueUpOutside,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                first = smallValue,
                second = smallValueUpWithin,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                first = smallValue,
                second = smallValue1,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                first = smallValue,
                second = smallValueDownWithin,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = smallValue,
                second = smallValueDownOutside,
            ),
        )

        val bigValueUpOutside = 1.1896e10
        val bigValueUpWithin = 1.1887e10
        val bigValue1 = 1.1876e10
        val bigValueDownWithin = 1.1866e10
        val bigValueDownOutside = 1.1863e10

        assertFalse(
            tolerance.equalsApproximately(
                first = bigValue,
                second = bigValueUpOutside,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                first = bigValue,
                second = bigValueUpWithin,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                first = bigValue,
                second = bigValue1,
            ),
        )

        assertTrue(
            tolerance.equalsApproximately(
                first = bigValue,
                second = bigValueDownWithin,
            ),
        )

        assertFalse(
            tolerance.equalsApproximately(
                first = bigValue,
                second = bigValueDownOutside,
            ),
        )
    }
}
