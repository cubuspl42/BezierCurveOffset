package app.algebra.linear

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance

@Suppress("DataClassPrivateConstructor")
data class Vector4<out Vo : VectorOrientation> private constructor(
    val x: Double,
    val y: Double,
    val z: Double,
    val w: Double,
) : NumericObject {
    companion object {
        fun of(
            x: Double,
            y: Double,
            z: Double,
            w: Double,
        ): Vector4<Nothing> = Vector4.of(
            x = x,
            y = y,
            z = z,
            w = w,
        )

        fun vertical(
            x: Double,
            y: Double,
            z: Double,
            w: Double,
        ): Vector4x1 = Vector4(
            x = x,
            y = y,
            z = z,
            w = w,
        )

        fun horizontal(
            x: Double,
            y: Double,
            z: Double,
            w: Double,
        ): Vector1x4 = Vector4(
            x = x,
            y = y,
            z = z,
            w = w,
        )
    }

    init {
        require(x.isFinite())
        require(y.isFinite())
        require(z.isFinite())
        require(w.isFinite())
    }


    fun toArray(): DoubleArray = doubleArrayOf(
        x,
        y,
        z,
        w,
    )

    fun dotForced(
        other: Vector4<*>,
    ): Double = x * other.x + y * other.y + z * other.z + w * other.w


    operator fun get(
        index: Int,
    ): Double = when (index) {
        0 -> x
        1 -> y
        2 -> z
        3 -> w
        else -> throw IndexOutOfBoundsException("Index $index out of bounds for length 4")
    }


    fun toList(): List<Double> = listOf(x, y, z, w)

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is Vector4<*> -> false
        !x.equalsWithTolerance(other.x, absoluteTolerance = absoluteTolerance) -> false
        !y.equalsWithTolerance(other.y, absoluteTolerance = absoluteTolerance) -> false
        !z.equalsWithTolerance(other.z, absoluteTolerance = absoluteTolerance) -> false
        !w.equalsWithTolerance(other.w, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }
}
