package app.geometry

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

abstract class PrincipalAngle {
    object Zero : PrincipalAngle() {
        override val cosFi: Double = 1.0

        override val sinFi: Double = 0.0

        override val inverted: PrincipalAngle = PrincipalAngle.Zero
    }

    /**
     * @param fi - the angle in radians
     */
    data class Explicit(
        override val fi: Double,
    ) : PrincipalAngle() {
        override val cosFi: Double
            get() = cos(fi)

        override val sinFi: Double
            get() = sin(fi)

        override val inverted: PrincipalAngle
            get() = Explicit(
                fi = -fi,
            )
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
    open val fi: Double
        get() = atan2(
            y = sinFi,
            x = cosFi,
        )

    fun equalsWithRadialTolerance(
        other: PrincipalAngle,
        tolerance: RadialTolerance,
    ): Boolean = abs(cosFi - other.cosFi) < tolerance.cosEpsThreshold
}
