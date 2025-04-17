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

        fun ofIrr(
            a0: Double,
            a1: Double,
        ): Vector2<VectorOrientation.Irrelevant> = Vector2(
            a0 = a0,
            a1 = a1,
        )

        fun <Vo : VectorOrientation> of(
            a0: Double,
            a1: Double,
        ): Vector2<Vo> = Vector2(
            a0 = a0,
            a1 = a1,
        )
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

    fun toList(): List<Double> = listOf(a0, a1)
}

typealias Vector2Irr = Vector2<VectorOrientation.Irrelevant>

fun <Vo : VectorOrientation> Vector2<Vo>.plusFirst(
    scalar: Double
): Vector2<Vo> = copy(
    a0 = a0 + scalar,
)

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

operator fun <Vo : VectorOrientation> Vector2<Vo>.unaryMinus(): Vector2<Vo> = Vector2(
    a0 = -a0,
    a1 = -a1,
)

operator fun <Vo : VectorOrientation> Double.times(
    v: Vector2<Vo>,
): Vector2<Vo> = Vector2(
    a0 = this * v.a0,
    a1 = this * v.a1,
)

operator fun <Vo : VectorOrientation> Vector2<Vo>.div(
    divisor: Double,
): Vector2<Vo> = Vector2(
    a0 = a0 / divisor,
    a1 = a1 / divisor,
)

fun <Vo : VectorOrientation> Vector2<Vo>.conv(
    other: Vector2<Vo>
): Vector3<Vo> = Vector3(
    a0 = a0 * other.a0,
    a1 = a0 * other.a1 + a1 * other.a0,
    a2 = a1 * other.a1,
)
