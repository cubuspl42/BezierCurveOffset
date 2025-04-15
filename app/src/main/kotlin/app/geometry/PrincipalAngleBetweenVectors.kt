package app.geometry

data class PrincipalAngleBetweenVectors(
    val reference: RawVector,
    val subject: RawVector,
) : PrincipalAngle() {
    init {
        require(reference != RawVector.zero)
        require(subject != RawVector.zero)
    }

    override val cosFi: Double
        get() = reference.dot(subject) / (reference.length * subject.length)

    override val sinFi: Double
        get() = reference.cross(subject) / (reference.length * subject.length)

    override val inverted: PrincipalAngle
        get() = PrincipalAngleBetweenVectors(
            reference = subject,
            subject = reference,
        )
}
