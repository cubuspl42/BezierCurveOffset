package app.geometry

/**
 * Represents an angle with respect to the x-axis.
 */
data class PrincipalAngleWithXAxis(
    val subject: RawVector,
) : PrincipalAngle() {
    override val cosFi: Double
        get() = subject.x / subject.length

    override val sinFi: Double
        get() = subject.y / subject.length

    override val inverted: PrincipalAngle
        get() = PrincipalAngleWithXAxis(
            subject = subject.reflectY,
        )
}
