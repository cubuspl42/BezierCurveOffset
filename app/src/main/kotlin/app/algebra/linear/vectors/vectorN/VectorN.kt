package app.algebra.linear.vectors.vectorN

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.VectorOrientation
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector3.Vector3
import app.algebra.linear.vectors.vector4.Vector4

data class VectorN<out Vo : VectorOrientation> internal constructor(
    val xs: List<Double>
) : NumericObject {
    companion object {
        fun horizontal(
            xs: List<Double>,
        ): Vector1xN = Vector1xN(
            xs = xs,
        )

        fun horizontal(
            vararg xs: Double,
        ): Vector1xN = Vector1xN(
            xs = xs.toList(),
        )

        fun vertical(
            xs: List<Double>,
        ): VectorNx1 = VectorNx1(
            xs = xs,
        )

        fun vertical(
            vararg xs: Double,
        ): VectorNx1 = VectorNx1(
            xs = xs.toList(),
        )

        fun ofIrr(
            xs: List<Double>,
        ): VectorN<VectorOrientation.Irrelevant> = VectorN(
            xs = xs,
        )
    }

    init {
        require(xs.isNotEmpty())
    }

    val a0: Double
        get() = xs.first()

    val an: Double
        get() = xs.last()

    val lower: VectorN<Vo>
        get() = VectorN(
            xs = xs.dropLast(1),
        )

    val size: Int
        get() = xs.size

    fun dotForced(
        other: VectorN<*>,
    ): Double {
        require(xs.size == other.xs.size)
        return xs.zip(other.xs).sumOf { (a, b) -> a * b }
    }

    fun vertical(
        xs: List<Double>,
    ): VectorN<Nothing> = VectorN(
        xs = xs,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is VectorN<*> -> false
        !xs.equalsWithTolerance(other.xs, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }
}

typealias VectorNIrr = VectorN<VectorOrientation.Irrelevant>

operator fun <Vo : VectorOrientation> VectorN<Vo>.plus(
    other: Vector2<Vo>,
): VectorN<Vo> {
    require(xs.size >= 2)

    val x0 = xs[0]
    val x1 = xs[1]

    return VectorN(
        xs = xs.drop(2) + listOf(
            x0 + other.a0,
            x1 + other.a1,
        )
    )
}

operator fun <Vo : VectorOrientation> VectorN<Vo>.plus(
    other: Vector3<Vo>,
): VectorN<Vo> {
    require(xs.size >= 3)

    val x0 = xs[0]
    val x1 = xs[1]
    val x2 = xs[2]

    return VectorN(
        xs = xs.drop(3) + listOf(
            x0 + other.a0,
            x1 + other.a1,
            x2 + other.a2,
        )
    )
}

operator fun <Vo : VectorOrientation> VectorN<Vo>.plus(
    other: Vector4<Vo>,
): VectorN<Vo> {
    require(xs.size >= 4)

    val x0 = xs[0]
    val x1 = xs[1]
    val x2 = xs[2]
    val x3 = xs[3]

    return VectorN(
        xs = xs.drop(4) + listOf(
            x0 + other.a0,
            x1 + other.a1,
            x2 + other.a2,
            x3 + other.a3,
        )
    )
}

operator fun <Vo : VectorOrientation> VectorN<Vo>.plus(
    other: VectorN<Vo>,
): VectorN<Vo> {
    require(xs.size == other.xs.size)

    return VectorN(
        xs = xs.zip(other.xs).map { (a, b) -> a + b },
    )
}

operator fun <Vo : VectorOrientation> VectorN<Vo>.times(
    factor: Double,
): VectorN<Vo> = VectorN(
    xs = xs.map { it * factor },
)

operator fun <Vo : VectorOrientation> Double.times(
    vector: VectorN<Vo>,
): VectorN<Vo> = vector * this

operator fun <Vo : VectorOrientation> VectorN<Vo>.unaryMinus(): VectorN<Vo> = VectorN(
    xs = xs.map { -it },
)

/**
 * Convolution of this vector with another vector
 */
fun <Vo : VectorOrientation> VectorN<Vo>.conv(
    other: Vector2<Vo>
): VectorN<Vo> = conv(
    elements = other.toList(),
)

/**
 * Convolution of this vector with another vector
 */
fun <Vo : VectorOrientation> VectorN<Vo>.conv(
    other: Vector3<Vo>
): VectorN<Vo> = conv(
    elements = other.toList(),
)

/**
 * Convolution of this vector with another vector
 */
fun <Vo : VectorOrientation> VectorN<Vo>.conv(
    other: Vector4<Vo>
): VectorN<Vo> = conv(
    elements = other.toList(),
)


/**
 * Convolution of this vector with another vector
 */
fun <Vo : VectorOrientation> VectorN<Vo>.conv(
    other: VectorN<Vo>
): VectorN<Vo> = conv(
    elements = other.xs,
)

private fun <Vo : VectorOrientation> VectorN<Vo>.conv(
    elements: List<Double>
): VectorN<Vo> {
    val resultSize = xs.size + elements.size - 1

    return VectorN(
        xs = (0 until resultSize).map { k ->
            (0..k).filter { i ->
                i in xs.indices && (k - i) in elements.indices
            }.sumOf { i ->
                xs[i] * elements[k - i]
            }
        },
    )
}
