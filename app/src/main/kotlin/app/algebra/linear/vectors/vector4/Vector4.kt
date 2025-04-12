package app.algebra.linear.vectors.vector4

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.VectorOrientation
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector3.Vector3

data class Vector4<out Vo : VectorOrientation> internal constructor(
    val a0: Double,
    val a1: Double,
    val a2: Double,
    val a3: Double,
) : NumericObject {
    companion object {
        fun of(
            a0: Double,
            a1: Double,
            a2: Double,
            a3: Double,
        ): Vector4<Nothing> = Vector4(
            a0 = a0,
            a1 = a1,
            a2 = a2,
            a3 = a3,
        )

        fun vertical(
            a00: Double,
            a10: Double,
            a20: Double,
            a30: Double,
        ): Vector4x1 = Vector4(
            a0 = a00,
            a1 = a10,
            a2 = a20,
            a3 = a30,
        )

        fun horizontal(
            a00: Double,
            a01: Double,
            a02: Double,
            a03: Double,
        ): Vector1x4 = Vector4(
            a0 = a00,
            a1 = a01,
            a2 = a02,
            a3 = a03,
        )
    }

    init {
        require(a0.isFinite())
        require(a1.isFinite())
        require(a2.isFinite())
        require(a3.isFinite())
    }

    fun toArray(): DoubleArray = doubleArrayOf(
        a0,
        a1,
        a2,
        a3,
    )

    fun dotForced(
        other: Vector4<*>,
    ): Double = a0 * other.a0 + a1 * other.a1 + a2 * other.a2 + a3 * other.a3

    operator fun get(
        index: Int,
    ): Double = when (index) {
        0 -> a0
        1 -> a1
        2 -> a2
        3 -> a3
        else -> throw IndexOutOfBoundsException("Index $index out of bounds for length 4")
    }

    fun toList(): List<Double> = listOf(a0, a1, a2, a3)

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is Vector4<*> -> false
        !a0.equalsWithTolerance(other.a0, absoluteTolerance = absoluteTolerance) -> false
        !a1.equalsWithTolerance(other.a1, absoluteTolerance = absoluteTolerance) -> false
        !a2.equalsWithTolerance(other.a2, absoluteTolerance = absoluteTolerance) -> false
        !a3.equalsWithTolerance(other.a3, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }
}
