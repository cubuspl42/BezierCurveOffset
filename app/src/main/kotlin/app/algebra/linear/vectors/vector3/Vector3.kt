package app.algebra.linear.vectors.vector3

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.VectorOrientation
import app.algebra.linear.vectors.vector2.Vector2

data class Vector3<out Vo : VectorOrientation>(
    val a0: Double,
    val a1: Double,
    val a2: Double,
) : NumericObject {
    companion object {
        fun of(
            a0: Double,
            a1: Double,
            a2: Double,
        ): Vector3<Nothing> = Vector3(
            a0 = a0,
            a1 = a1,
            a2 = a2,
        )

        fun horizontal(
            a00: Double,
            a01: Double,
            a02: Double,
        ): Vector1x3 = Vector3(
            a0 = a00,
            a1 = a01,
            a2 = a02,
        )

        fun vertical(
            a00: Double,
            a10: Double,
            a20: Double,
        ): Vector3x1 = Vector3(
            a0 = a00,
            a1 = a10,
            a2 = a20,
        )
    }

    init {
        require(a0.isFinite())
        require(a1.isFinite())
        require(a2.isFinite())
    }

    operator fun minus(
        other: Vector3<*>,
    ): Vector3<Nothing> = of(
        a0 = a0 - other.a0,
        a1 = a1 - other.a1,
        a2 = a2 - other.a2,
    )

    operator fun plus(
        other: Vector3<*>,
    ): Vector3<Nothing> = of(
        a0 = a0 + other.a0,
        a1 = a1 + other.a1,
        a2 = a2 + other.a2,
    )

    fun dotForced(
        other: Vector3<*>,
    ): Double = a0 * other.a0 + a1 * other.a1 + a2 * other.a2

    fun cross(
        other: Vector3<*>,
    ): Vector3<Nothing> = of(
        a0 = a1 * other.a2 - a2 * other.a1,
        a1 = a2 * other.a0 - a0 * other.a2,
        a2 = a0 * other.a1 - a1 * other.a0,
    )

    fun scale(
        factor: Double,
    ): Vector3<Nothing> {
        require(factor.isFinite())
        return of(
            a0 = a0 * factor,
            a1 = a1 * factor,
            a2 = a2 * factor,
        )
    }

    val asVertical: Vector3x1
        get() {
            @Suppress("UNCHECKED_CAST") return this as Vector3x1
        }

    val asHorizontal: Vector1x3
        get() {
            @Suppress("UNCHECKED_CAST") return this as Vector1x3
        }

    override fun equalsWithTolerance(
        other: NumericObject, absoluteTolerance: Double
    ): Boolean = when {
        other !is Vector3<*> -> false
        !a0.equalsWithTolerance(other.a0, absoluteTolerance) -> false
        !a1.equalsWithTolerance(other.a1, absoluteTolerance) -> false
        !a2.equalsWithTolerance(other.a2, absoluteTolerance) -> false
        else -> true
    }
}

val <Vo : VectorOrientation> Vector3<Vo>.vector2: Vector2<Vo>
    get() = Vector2(
        a0 = this.a0,
        a1 = this.a1,
    )
