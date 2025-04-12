package app.geometry

import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt

data class Angle(
    val a: RawVector,
    val b: RawVector,
) {
    private val dotProduct: Double = a.dot(b)

    /**
     * Whether the angle between the two vectors is acute
     */
    val isAcute: Boolean
        get() = dotProduct > 0.0

    /**
     * The cos^2 of the angle between the two vectors
     */
    internal val cosSqFi: Double
        get() = dotProduct * dotProduct / (a.lengthSquared * b.lengthSquared)

    /**
     * The cos of the angle between the two vectors, assuming the angle is acute
     */
    internal val cosFiAcute: Double
        get() = sqrt(cosSqFi)

    /**
     * The cos of the angle between the two vectors (in the general case)
     */
    internal val cosFi: Double
        get() = dotProduct / (a.length * b.length)

    /**
     * The angle between the two vectors in radians
     */
    val fi: Double
        get() = when {
            isAcute -> acos(cosFiAcute)
            else -> acos(cosFi)
        }

    fun equalsWithRadialTolerance(
        other: Angle,
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
