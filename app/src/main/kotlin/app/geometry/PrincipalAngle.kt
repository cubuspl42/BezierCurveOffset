package app.geometry

import kotlin.math.abs
import kotlin.math.acos

abstract class PrincipalAngle {
    object Zero : PrincipalAngle() {
        override val cosFi: Double
            get() = 1.0

        override val sinFi: Double
            get() = 0.0
    }

    /**
     * The cosine of the angle
     */
    abstract val cosFi: Double

    /**
     * The sine of this angle
     */
    abstract val sinFi: Double

    /**
     * The angle between the two vectors in radians
     */
    val fi: Double
        get() = acos(cosFi)

    fun equalsWithRadialTolerance(
        other: PrincipalAngleBetweenVectors,
        tolerance: RadialTolerance,
    ): Boolean = abs(cosFi - other.cosFi) < tolerance.cosEpsThreshold

    fun isZeroWithRadialTolerance(
        tolerance: RadialTolerance,
    ): Boolean = cosFi > tolerance.cosEpsThreshold
}
