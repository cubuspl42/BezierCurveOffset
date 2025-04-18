package app.algebra.linear.vectors.vectorN

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.VectorOrientation
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector3.Vector3
import app.algebra.linear.vectors.vector4.Vector4
import app.splitAfter
import app.uncons

/**
 * A vector of arbitrary size N.
 *
 * @param elements - the vector elements, indexed from 0 to N-1 (the least significant
 * element is at the front of the list)
 */
data class VectorN<out Vo : VectorOrientation> internal constructor(
    val elements: List<Double>
) : NumericObject {
    companion object {
        fun horizontal(
            elements: List<Double>,
        ): Vector1xN = Vector1xN(
            elements = elements,
        )

        fun horizontal(
            vararg elements: Double,
        ): Vector1xN = Vector1xN(
            elements = elements.toList(),
        )

        fun vertical(
            elements: List<Double>,
        ): VectorNx1 = VectorNx1(
            elements = elements,
        )

        fun vertical(
            vararg elements: Double,
        ): VectorNx1 = VectorNx1(
            elements = elements.toList(),
        )

        fun ofIrr(
            vararg elements: Double,
        ) = ofIrr(
            elements = elements.toList(),
        )

        fun ofIrr(
            elements: List<Double>,
        ): VectorN<VectorOrientation.Irrelevant> = VectorN(
            elements = elements,
        )
    }

    init {
        require(elements.isNotEmpty())
    }

    val a0: Double
        get() = elements.first()

    val an: Double
        get() = elements.last()

    val lower: VectorN<Vo>
        get() = VectorN(
            elements = elements.dropLast(1),
        )

    val size: Int
        get() = elements.size

    fun dotForced(
        other: VectorN<*>,
    ): Double {
        require(elements.size == other.elements.size)
        return elements.zip(other.elements).sumOf { (a, b) -> a * b }
    }

    fun vertical(
        xs: List<Double>,
    ): VectorN<Nothing> = VectorN(
        elements = xs,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Double,
    ): Boolean = when {
        other !is VectorN<*> -> false
        !elements.equalsWithTolerance(other.elements, tolerance = tolerance) -> false
        else -> true
    }

    fun plusFirst(constant: Double): VectorN<Vo> {
        val (leastSignificantElement, moreSignificantElements) = elements.uncons() ?: throw AssertionError()

        return VectorN(
            elements = listOf(leastSignificantElement + constant) + moreSignificantElements,
        )
    }
}

typealias VectorNIrr = VectorN<VectorOrientation.Irrelevant>

// FIXME: the least significant element is at the front of the list, not end!

operator fun <Vo : VectorOrientation> VectorN<Vo>.plus(
    other: Vector2<Vo>,
): VectorN<Vo> {
    require(elements.size >= 2)
//
    val x0 = elements[0]
    val x1 = elements[1]
//
//    return VectorN(
//        xs = xs.drop(2) + listOf(
//            x0 + other.a0,
//            x1 + other.a1,
//        )
//    )

    return VectorN(
        elements = listOf(
            x0 + other.a0,
            x1 + other.a1,
        ) + elements.drop(2),
    )
}

operator fun <Vo : VectorOrientation> VectorN<Vo>.plus(
    other: Vector3<Vo>,
): VectorN<Vo> {
    require(elements.size >= 3)

    val x0 = elements[0]
    val x1 = elements[1]
    val x2 = elements[2]
//
//    return VectorN(
//        xs = xs.drop(3) + listOf(
//            x0 + other.a0,
//            x1 + other.a1,
//            x2 + other.a2,
//        )
//    )

    return VectorN(
        elements = listOf(
            x0 + other.a0,
            x1 + other.a1,
            x2 + other.a2,
        ) + elements.drop(3),
    )
}

operator fun <Vo : VectorOrientation> VectorN<Vo>.plus(
    other: Vector4<Vo>,
): VectorN<Vo> {
    require(elements.size >= 4)

    val x0 = elements[0]
    val x1 = elements[1]
    val x2 = elements[2]
    val x3 = elements[3]

//    return VectorN(
//        xs = xs.drop(4) + listOf(
//            x0 + other.a0,
//            x1 + other.a1,
//            x2 + other.a2,
//            x3 + other.a3,
//        )
//    )

    return VectorN(
        elements = listOf(
            x0 + other.a0,
            x1 + other.a1,
            x2 + other.a2,
            x3 + other.a3,
        ) + elements.drop(4),
    )
}

operator fun <Vo : VectorOrientation> VectorN<Vo>.plus(
    other: VectorN<Vo>,
): VectorN<Vo> = when {
    elements.size >= other.size -> plusNotLarger(other)
    else -> other.plusNotLarger(this)
}

private fun <Vo : VectorOrientation> VectorN<Vo>.plusNotLarger(
    other: VectorN<Vo>,
): VectorN<Vo> {
    require(size >= other.size)

    val (lessSignificantElements, moreSignificantElements) = elements.splitAfter(other.size)

    return VectorN(
        elements = lessSignificantElements.zip(other.elements) { a, b -> a + b } + moreSignificantElements,
    )
}

operator fun <Vo : VectorOrientation> VectorN<Vo>.times(
    factor: Double,
): VectorN<Vo> = VectorN(
    elements = elements.map { it * factor },
)

operator fun <Vo : VectorOrientation> Double.times(
    vector: VectorN<Vo>,
): VectorN<Vo> = vector * this

operator fun <Vo : VectorOrientation> VectorN<Vo>.unaryMinus(): VectorN<Vo> = VectorN(
    elements = elements.map { -it },
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
    elements = other.elements,
)

private fun <Vo : VectorOrientation> VectorN<Vo>.conv(
    elements: List<Double>
): VectorN<Vo> {
    val resultSize = this.elements.size + elements.size - 1

    return VectorN(
        elements = (0 until resultSize).map { k ->
            (0..k).filter { i ->
                i in this.elements.indices && (k - i) in elements.indices
            }.sumOf { i ->
                this.elements[i] * elements[k - i]
            }
        },
    )
}
