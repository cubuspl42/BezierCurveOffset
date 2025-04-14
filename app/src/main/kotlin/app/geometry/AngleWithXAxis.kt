package app.geometry

/**
 * Represents an angle with respect to the x-axis.
 */
data class AngleWithXAxis(
    val a: RawVector,
) : Angle() {
    override val cosSqFi: Double
        get() = a.x * a.x / a.lengthSquared

    override val cosFi: Double
        get() = a.x / a.length
}
