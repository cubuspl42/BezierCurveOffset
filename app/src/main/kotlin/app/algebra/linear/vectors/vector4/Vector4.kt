package app.algebra.linear.vectors.vector4

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsWithTolerance
import app.algebra.linear.VectorOrientation
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector3.Vector3
import app.algebra.linear.vectors.vectorN.VectorN
import app.algebra.linear.vectors.vectorN.conv

data class Vector4<out Vo : VectorOrientation> internal constructor(
    val a0: Double,
    val a1: Double,
    val a2: Double,
    val a3: Double,
) : NumericObject {
    companion object {
        fun <Vo : VectorOrientation> of(
            a0: Double,
            a1: Double,
            a2: Double,
            a3: Double,
        ): Vector4<Vo> = Vector4(
            a0 = a0,
            a1 = a1,
            a2 = a2,
            a3 = a3,
        )

        fun ofIrr(
            a0: Double,
            a1: Double,
            a2: Double,
            a3: Double,
        ): Vector4<VectorOrientation.Irrelevant> = Vector4(
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
        tolerance: Tolerance,
    ): Boolean = when {
        other !is Vector4<*> -> false
        !a0.equalsWithTolerance(other.a0, tolerance = tolerance) -> false
        !a1.equalsWithTolerance(other.a1, tolerance = tolerance) -> false
        !a2.equalsWithTolerance(other.a2, tolerance = tolerance) -> false
        !a3.equalsWithTolerance(other.a3, tolerance = tolerance) -> false
        else -> true
    }
}


fun <Vo : VectorOrientation> Vector4<Vo>.plusFirst(
    scalar: Double,
): Vector4<Vo> = copy(
    a0 = a0 + scalar,
)

operator fun <Vo : VectorOrientation> Vector4<Vo>.plus(
    other: Vector2<Vo>,
): Vector4<Vo> = copy(
    a0 = a0 + other.a0,
    a1 = a1 + other.a1,
)

operator fun <Vo : VectorOrientation> Vector4<Vo>.plus(
    other: Vector3<Vo>,
): Vector4<Vo> = copy(
    a0 = a0 + other.a0,
    a1 = a1 + other.a1,
    a2 = a2 + other.a2,
)

operator fun <Vo : VectorOrientation> Vector4<Vo>.plus(
    other: Vector4<Vo>,
): Vector4<Vo> = Vector4.of(
    a0 = a0 + other.a0,
    a1 = a1 + other.a1,
    a2 = a2 + other.a2,
    a3 = a3 + other.a3,
)

operator fun <Vo : VectorOrientation> Vector4<Vo>.times(
    scalar: Double,
): Vector4<Vo> = Vector4(
    a0 = a0 * scalar,
    a1 = a1 * scalar,
    a2 = a2 * scalar,
    a3 = a3 * scalar,
)

operator fun <Vo : VectorOrientation> Double.times(
    v: Vector4<Vo>,
): Vector4<Vo> = v * this

/**
 * Convolve two vectors a and b.
 *
 * The output convolution is a vector with length equal to length (a) + length (b) - 1.
 * When a and b are the coefficient vectors of two polynomials, the convolution
 * represents the coefficient vector of the product polynomial.
 *
 * @return the convolution of this vector with another vector (size 5)
 */
fun <Vo : VectorOrientation> Vector4<Vo>.conv(
    other: Vector2<Vo>
): VectorN<Vo> = VectorN(
    elements = listOf(
        a0 * other.a0,
        a0 * other.a1 + a1 * other.a0,
        a1 * other.a1 + a2 * other.a0,
        a2 * other.a1 + a3 * other.a0,
        a3 * other.a1,
    ),
)

/**
 * Convolve two vectors a and b.
 *
 * The output convolution is a vector with length equal to length (a) + length (b) - 1.
 * When a and b are the coefficient vectors of two polynomials, the convolution
 * represents the coefficient vector of the product polynomial.
 *
 * @return the convolution of this vector with another vector (size 6)
 */
fun <Vo : VectorOrientation> Vector4<Vo>.conv(
    other: Vector3<Vo>
): VectorN<Vo> = VectorN(
    elements = listOf(
        a0 * other.a0,
        a0 * other.a1 + a1 * other.a0,
        a0 * other.a2 + a1 * other.a1 + a2 * other.a0,
        a1 * other.a2 + a2 * other.a1 + a3 * other.a0,
        a2 * other.a2 + a3 * other.a1,
        a3 * other.a2,
    ),
)

/**
 * Convolve two vectors a and b.
 *
 * The output convolution is a vector with length equal to length (a) + length (b) - 1.
 * When a and b are the coefficient vectors of two polynomials, the convolution
 * represents the coefficient vector of the product polynomial.
 *
 * @return the convolution of this vector with another vector (size 7)
 */
fun <Vo : VectorOrientation> Vector4<Vo>.conv(
    other: Vector4<Vo>
): VectorN<Vo> = VectorN(
    elements = listOf(
        a0 * other.a0,
        a0 * other.a1 + a1 * other.a0,
        a0 * other.a2 + a1 * other.a1 + a2 * other.a0,
        a0 * other.a3 + a1 * other.a2 + a2 * other.a1 + a3 * other.a0,
        a1 * other.a3 + a2 * other.a2 + a3 * other.a1,
        a2 * other.a3 + a3 * other.a2,
        a3 * other.a3,
    ),
)

fun <Vo : VectorOrientation> Vector4<Vo>.conv(
    other: VectorN<Vo>
): VectorN<Vo> = other.conv(this)

fun <Vo : VectorOrientation> Vector4<Vo>.scale(
    factor: Double,
): Vector4<Vo> {
    require(factor.isFinite())
    return Vector4.of(
        a0 = a0 * factor,
        a1 = a1 * factor,
        a2 = a2 * factor,
        a3 = a3 * factor,
    )
}

val <Vo : VectorOrientation> Vector4<Vo>.vector3: Vector3<Vo>
    get() = Vector3(
        a0 = this.a0,
        a1 = this.a1,
        a2 = this.a2,
    )

val <Vo : VectorOrientation> Vector4<Vo>.lower: Vector3<Vo>
    get() = vector3

operator fun <Vo : VectorOrientation> Vector4<Vo>.unaryMinus(): Vector4<Vo> = Vector4(
    a0 = -a0,
    a1 = -a1,
    a2 = -a2,
    a3 = -a3,
)

typealias Vector4Irr = Vector4<VectorOrientation.Irrelevant>
