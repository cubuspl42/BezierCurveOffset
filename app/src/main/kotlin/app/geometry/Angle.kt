package app.geometry

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt

abstract class Angle {
    /**
     * Whether the angle is acute (0 < fi < 90 degrees)
     */
    open val isAcute: Boolean
        get() = cosFi > 0

    /**
     * The cos^2 of the angle between the two vectors
     */
    abstract val cosSqFi: Double

    /**
     * The angle between the two vectors in radians
     */
    val fi: Double
        get() = when {
            isAcute -> sqrt(cosSqFi)
            else -> acos(cosFi)
        }

    /**
     * The cosine of this angle
     */
    abstract val cosFi: Double

    fun equalsWithRadialTolerance(
        other: AngleBetweenVectors,
        tolerance: RadialTolerance,
    ): Boolean = when {
        isAcute != other.isAcute -> false
        else -> when {
            isAcute -> abs(cosSqFi - other.cosSqFi) < tolerance.cosSqEpsThreshold
            else -> abs(cosFi - other.cosFi) < tolerance.cosEpsThreshold
        }
    }

    fun isZeroWithRadialTolerance(
        tolerance: RadialTolerance,
    ): Boolean = when {
        !isAcute -> false
        else -> cosSqFi > tolerance.cosSqEps
    }
}
