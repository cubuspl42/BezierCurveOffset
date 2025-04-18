package app.algebra.linear.vectors.vector3

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.VectorOrientation
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.linear.vectors.vectorN.VectorN

data class Vector3<out Vo : VectorOrientation>(
    val a0: Double,
    val a1: Double,
    val a2: Double,
) : NumericObject {
    companion object {
        fun <Vo : VectorOrientation> of(
            a0: Double,
            a1: Double,
            a2: Double,
        ): Vector3<Vo> = Vector3(
            a0 = a0,
            a1 = a1,
            a2 = a2,
        )

        fun ofIrr(
            a0: Double,
            a1: Double,
            a2: Double
        ): Vector3<VectorOrientation.Irrelevant> = Vector3(
            a0 = a0,
            a1 = a1,
            a2 = a2,
        )

        fun zero(): Vector3<VectorOrientation.Irrelevant> {
            return Vector3(
                a0 = 0.0,
                a1 = 0.0,
                a2 = 0.0,
            )
        }

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

    // plus vector2

    fun dotForced(
        other: Vector3<*>,
    ): Double = a0 * other.a0 + a1 * other.a1 + a2 * other.a2

    fun cross(
        other: Vector3<*>,
    ): Vector3<VectorOrientation.Irrelevant> = Vector3(
        a0 = a1 * other.a2 - a2 * other.a1,
        a1 = a2 * other.a0 - a0 * other.a2,
        a2 = a0 * other.a1 - a1 * other.a0,
    )

    val asVertical: Vector3x1
        get() {
            @Suppress("UNCHECKED_CAST") return this as Vector3x1
        }

    val asHorizontal: Vector1x3
        get() {
            @Suppress("UNCHECKED_CAST") return this as Vector1x3
        }

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: Double
    ): Boolean = when {
        other !is Vector3<*> -> false
        !a0.equalsWithTolerance(other.a0, tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance) -> false
        !a2.equalsWithTolerance(other.a2, tolerance) -> false
        else -> true
    }

    fun toList(): List<Double> = listOf(a0, a1, a2)
}

fun <Vo : VectorOrientation> Vector3<Vo>.scale(
    factor: Double,
): Vector3<Vo> {
    require(factor.isFinite())

    return Vector3(
        a0 = a0 * factor,
        a1 = a1 * factor,
        a2 = a2 * factor,
    )
}

operator fun <Vo : VectorOrientation> Vector3<Vo>.minus(
    other: Vector3<Vo>,
): Vector3<Vo> = Vector3(
    a0 = a0 - other.a0,
    a1 = a1 - other.a1,
    a2 = a2 - other.a2,
)

typealias Vector3Irr = Vector3<VectorOrientation.Irrelevant>

fun <Vo : VectorOrientation> Vector3<Vo>.plusFirst(
    scalar: Double,
): Vector3<Vo> = copy(
    a0 = a0 + scalar,
)

operator fun <Vo : VectorOrientation> Vector3<Vo>.plus(
    other: Vector2<Vo>,
): Vector3<Vo> = copy(
    a0 = a0 + other.a0,
    a1 = a1 + other.a1,
)

operator fun <Vo : VectorOrientation> Vector3<Vo>.plus(
    other: Vector3<Vo>,
): Vector3<Vo> = Vector3(
    a0 = a0 + other.a0,
    a1 = a1 + other.a1,
    a2 = a2 + other.a2,
)

/**
 * Convolution of this vector with another vector
 */
fun <Vo : VectorOrientation> Vector3<Vo>.conv(
    other: Vector2<Vo>
): Vector4<Vo> = Vector4(
    a0 = a0 * other.a0,
    a1 = a0 * other.a1 + a1 * other.a0,
    a2 = a1 * other.a1 + a2 * other.a0,
    a3 = a2 * other.a1,
)

/**
 * Convolution of this vector with another vector
 */
fun <Vo : VectorOrientation> Vector3<Vo>.conv(
    other: Vector3<Vo>
): VectorN<Vo> = VectorN(
    elements = listOf(
        a0 * other.a0,
        a0 * other.a1 + a1 * other.a0,
        a0 * other.a2 + a1 * other.a1 + a2 * other.a0,
        a1 * other.a2 + a2 * other.a1,
        a2 * other.a2,
    ),
)

val <Vo : VectorOrientation> Vector3<Vo>.vector2: Vector2<Vo>
    get() = Vector2(
        a0 = this.a0,
        a1 = this.a1,
    )

val <Vo : VectorOrientation> Vector3<Vo>.lower: Vector2<Vo>
    get() = vector2

operator fun <Vo : VectorOrientation> Vector3<Vo>.unaryMinus(): Vector3<Vo> = Vector3(
    a0 = -a0,
    a1 = -a1,
    a2 = -a2,
)
