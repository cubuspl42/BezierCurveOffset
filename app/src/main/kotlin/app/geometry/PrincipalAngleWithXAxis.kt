package app.geometry

/**
 * Represents an angle with respect to the x-axis.
 */
@Suppress("DataClassPrivateConstructor")
data class PrincipalAngleWithXAxis private constructor(
    val subject: RawVector,
) : PrincipalAngle() {
    companion object {
        fun of(
            subject: RawVector,
        ): PrincipalAngleWithXAxis? = when {
            subject == RawVector.zero -> null
            else -> PrincipalAngleWithXAxis(
                subject = subject,
            )
        }
    }

    override val cosFi: Double
        get() = subject.x / subject.length

    override val sinFi: Double
        get() = subject.y / subject.length

    override val inverted: PrincipalAngle
        get() = PrincipalAngleWithXAxis(
            subject = subject.reflectY,
        )
}
