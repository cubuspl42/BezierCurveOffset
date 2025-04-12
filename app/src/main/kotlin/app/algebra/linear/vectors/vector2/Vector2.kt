package app.algebra.linear.vectors.vector2

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.VectorOrientation
import app.algebra.linear.vectors.vector3.Vector3
import app.geometry.RawVector

data class Vector2<out Vo : VectorOrientation>(
    val a0: Double,
    val a1: Double,
) : NumericObject {
    companion object {
        fun <Vo : VectorOrientation> zero(): Vector2<Vo> = Vector2(a0 = 0.0, a1 = 0.0)

        fun of(
            x: Double,
            y: Double,
        ): Vector2<Nothing> = Vector2(x, y)
    }

    init {
        require(a0.isFinite())
        require(a1.isFinite())
    }

    val raw: RawVector
        get() = RawVector(
            x = a0,
            y = a1,
        )

    fun toVec3(
        z: Double = 1.0,
    ): Vector3<Vo> = Vector3(
        a0 = a0,
        a1 = a1,
        a2 = z,
    )

    /**
     * Calculates the dot product of this vector with another vector,
     * assuming that the other has a matching orientation
     */
    fun dotForced(
        other: Vector2<*>,
    ): Double = a0 * other.a0 + a1 * other.a1

    fun cross(
        other: Vector2<*>,
    ): Double = a0 * other.a1 - a1 * other.a0

    fun scale(
        factor: Double,
    ): Vector2<Vo> {
        require(factor.isFinite())
        return Vector2(
            a0 = a0 * factor,
            a1 = a1 * factor,
        )
    }

    /**
     * The counterclockwise perpendicular vector
     */
    val perpendicular: Vector2<Vo>
        get() = Vector2(a0 = -a1, a1 = a0)

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is Vector2<*> -> false
        !a0.equalsWithTolerance(other.a0, absoluteTolerance = absoluteTolerance) -> false
        !a1.equalsWithTolerance(other.a1, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }
}

operator fun <Vo : VectorOrientation> Vector2<Vo>.plus(
    other: Vector2<Vo>,
): Vector2<Vo> = Vector2(
    a0 = a0 + other.a0,
    a1 = a1 + other.a1,
)

operator fun <Vo : VectorOrientation> Vector2<Vo>.minus(
    other: Vector2<Vo>,
): Vector2<Vo> = Vector2(
    a0 = a0 - other.a0,
    a1 = a1 - other.a1,
)

operator fun <Vo : VectorOrientation> Vector2<Vo>.unaryMinus(): Vector2<Vo> = Vector2.of(
    x = -a0,
    y = -a1,
)

operator fun <Vo : VectorOrientation> Double.times(
    v: Vector2<Vo>,
): Vector2<Vo> = Vector2.of(
    x = this * v.a0,
    y = this * v.a1,
)

operator fun <Vo : VectorOrientation> Vector2<Vo>.div(
    divisor: Double,
): Vector2<Vo> = Vector2.of(
    x = a0 / divisor,
    y = a1 / divisor,
)
