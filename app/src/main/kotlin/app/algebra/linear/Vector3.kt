package app.algebra.linear

abstract class Vector3 {
    companion object {
        fun of(
            x: Double,
            y: Double,
            z: Double,
        ): Vector3 = Vector3x1.of(
            x = x,
            y = y,
            z = z,
        )
    }

    abstract val x: Double

    abstract val y: Double

    abstract val z: Double

    protected fun dotForced(
        other: Vector3,
    ): Double = x * other.x + y * other.y + z * other.z

    fun toArray(): DoubleArray = doubleArrayOf(
        x,
        y,
        z,
    )
}
