package app.geometry

import kotlin.math.abs
import kotlin.math.acos

abstract class PrincipalAngle {
    object Zero : PrincipalAngle() {
        override val cosFi: Double = 1.0

        override val sinFi: Double = 0.0

        override val inverted: PrincipalAngle = PrincipalAngle.Zero
    }

    operator fun unaryMinus(): PrincipalAngle = inverted

    /**
     * The cosine of the angle
     */
    abstract val cosFi: Double

    /**
     * The sine of this angle
     */
    abstract val sinFi: Double

    abstract val inverted: PrincipalAngle

    /**
     * The angle between the two vectors in radians
     */
    val fi: Double
        get() = acos(cosFi)

    fun equalsWithRadialTolerance(
        other: PrincipalAngle,
        tolerance: RadialTolerance,
    ): Boolean = abs(cosFi - other.cosFi) < tolerance.cosEpsThreshold
}
