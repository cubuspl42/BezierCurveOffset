package app.algebra.linear

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import kotlin.math.sqrt

data class Vector3<out Vo : VectorOrientation>(
    val x: Double,
    val y: Double,
    val z: Double,
) : NumericObject {
    companion object {
        fun of(
            x: Double,
            y: Double,
            z: Double,
        ): Vector3<Nothing> = Vector3(
            x = x,
            y = y,
            z = z,
        )

        fun horizontal(
            x: Double,
            y: Double,
            z: Double,
        ): Vector1x3 = Vector3(
            x = x,
            y = y,
            z = z,
        )

        fun vertical(
            x: Double,
            y: Double,
            z: Double,
        ): Vector3x1 = Vector3(
            x = x,
            y = y,
            z = z,
        )
    }

    init {
        require(x.isFinite())
        require(y.isFinite())
        require(z.isFinite())
    }

    val lengthSquared: Double
        get() = x * x + y * y + z * z

    val length: Double
        get() = sqrt(lengthSquared)

    operator fun minus(
        other: Vector3<*>,
    ): Vector3<Nothing> = Vector3.of(
        x = x - other.x,
        y = y - other.y,
        z = z - other.z,
    )

    operator fun plus(
        other: Vector3<*>,
    ): Vector3<Nothing> = Vector3.of(
        x = x + other.x,
        y = y + other.y,
        z = z + other.z,
    )

    fun dotForced(
        other: Vector3<*>,
    ): Double = x * other.x + y * other.y + z * other.z

    fun cross(
        other: Vector3<*>,
    ): Vector3<Nothing> = Vector3.of(
        x = y * other.z - z * other.y,
        y = z * other.x - x * other.z,
        z = x * other.y - y * other.x,
    )

    fun scale(
        factor: Double,
    ): Vector3<Nothing> {
        require(factor.isFinite())
        return Vector3.of(
            x = x * factor,
            y = y * factor,
            z = z * factor,
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
        !x.equalsWithTolerance(other.x, absoluteTolerance) -> false
        !y.equalsWithTolerance(other.y, absoluteTolerance) -> false
        !z.equalsWithTolerance(other.z, absoluteTolerance) -> false
        else -> true
    }
}

val <Vo : VectorOrientation> Vector3<Vo>.vectorXy: Vector2<Vo>
    get() = Vector2(
        x = this.x,
        y = this.y,
    )

val <Vo : VectorOrientation> Vector3<Vo>.vectorYz: Vector2<Vo>
    get() = Vector2(
        x = this.y,
        y = this.z,
    )
