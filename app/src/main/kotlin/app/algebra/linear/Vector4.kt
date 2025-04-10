package app.algebra.linear

import app.algebra.NumericObject

abstract class Vector4 : NumericObject {
    companion object {
        fun of(
            x: Double,
            y: Double,
            z: Double,
            w: Double,
        ): Vector4 = Vector4x1.of(
            x = x,
            y = y,
            z = z,
            w = w,
        )
    }

    abstract val x: Double

    abstract val y: Double

    abstract val z: Double

    abstract val w: Double

    fun toArray(): DoubleArray = doubleArrayOf(
        x,
        y,
        z,
        w,
    )

    protected fun dotForced(
        other: Vector4,
    ): Double = x * other.x + y * other.y + z * other.z + w * other.w
}
