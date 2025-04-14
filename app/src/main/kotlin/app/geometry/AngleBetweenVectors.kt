package app.geometry

data class AngleBetweenVectors(
    val a: RawVector,
    val b: RawVector,
) : Angle() {
    private val dotProduct: Double = a.dot(b)

    override val isAcute: Boolean
        get() = dotProduct > 0.0

    override val cosSqFi: Double
        get() = dotProduct * dotProduct / (a.lengthSquared * b.lengthSquared)

    override val cosFi: Double
        get() = dotProduct / (a.length * b.length)
}
