package app.algebra.linear

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance

@Suppress("DataClassPrivateConstructor")
data class Vector4x1 private constructor(
    override val x: Double,
    override val y: Double,
    override val z: Double,
    override val w: Double,
) : Vector4() {
    companion object {
        fun of(
            x: Double,
            y: Double,
            z: Double,
            w: Double,
        ): Vector4x1 = Vector4x1(
            x = x,
            y = y,
            z = z,
            w = w,
        )

        val zero = Vector4x1.of(0.0, 0.0, 0.0, 0.0)
    }

    init {
        require(x.isFinite())
        require(y.isFinite())
        require(z.isFinite())
        require(w.isFinite())
    }

    fun dot(
        other: Vector1x4,
    ): Double = dotForced(other)

    operator fun get(
        index: Int,
    ): Double = when (index) {
        0 -> x
        1 -> y
        2 -> z
        3 -> w
        else -> throw IndexOutOfBoundsException("Index $index out of bounds for length 4")
    }

    val transposed: Vector1x4
        get() = Vector1x4.of(
            x = this.x,
            y = this.y,
            z = this.z,
            w = this.w,
        )

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when (other) {
        !is Vector4x1 -> false
        else -> x.equalsWithTolerance(other.x, absoluteTolerance = absoluteTolerance) &&
                y.equalsWithTolerance(other.y, absoluteTolerance = absoluteTolerance) &&
                z.equalsWithTolerance(other.z, absoluteTolerance = absoluteTolerance) &&
                w.equalsWithTolerance(other.w, absoluteTolerance = absoluteTolerance)
    }
}
