package app.geometry

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.VectorSpace
import app.algebra.linear.vectors.vector2.Vector2x1
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * A raw two-dimensional Euclidean vector, i.e. a pair of two numbers (x, y)
 * without an interpretation of their meaning beyond that x corresponds to the X
 * axis (the first dimension) and y to the Y axis (the second dimension).
 */
data class RawVector(
    val x: Double,
    val y: Double,
) : NumericObject {
    /**
     * A vector space of two-dimensional raw vectors
     */
    object RawVectorSpace : VectorSpace<RawVector>() {
        override val zero: RawVector = Companion.zero

        override fun add(
            u: RawVector,
            v: RawVector,
        ): RawVector = u + v

        override fun subtract(
            u: RawVector,
            v: RawVector,
        ): RawVector = u - v

        override fun scale(
            a: Double,
            v: RawVector,
        ): RawVector = v * a
    }

    companion object {
        val zero = RawVector(
            x = 0.0,
            y = 0.0,
        )

        fun bisector(
            a: RawVector,
            b: RawVector,
        ): RawVector = b.length * a + a.length * b
    }

    operator fun plus(other: RawVector): RawVector = RawVector(
        x = x + other.x,
        y = y + other.y,
    )

    operator fun minus(other: RawVector): RawVector = RawVector(
        x = x - other.x,
        y = y - other.y,
    )

    operator fun times(scalar: Double): RawVector = RawVector(
        x = x * scalar,
        y = y * scalar,
    )

    operator fun div(scalar: Double): RawVector = RawVector(
        x = x / scalar,
        y = y / scalar,
    )

    operator fun unaryMinus(): RawVector = RawVector(
        x = -x,
        y = -y,
    )

    fun dot(other: RawVector): Double = x * other.x + y * other.y

    fun cross(other: RawVector): Double = x * other.y - y * other.x

    /**
     * @param fi - the angle in radians
     */
    fun rotate(fi: Double): RawVector {
        val cosFi = cos(fi)
        val sinFi = sin(fi)

        return RawVector(
            x = x * cosFi - y * sinFi,
            y = x * sinFi + y * cosFi,
        )
    }

    val lengthSquared: Double
        get() = x * x + y * y

    val length: Double
        get() = sqrt(lengthSquared)

    val normalized: RawVector
        get() = when (lengthSquared) {
            0.0 -> zero
            else -> this / length
        }

    val perpendicular: RawVector
        get() = RawVector(
            x = -y,
            y = x,
        )

    val vertical: Vector2x1 = Vector2x1(
        a0 = x,
        a1 = y,
    )

    val asPoint: Point
        get() = Point(
            pv = this,
        )

    val asBiDirection: BiDirection?
        get() = BiDirection.of(
            dv = this,
        )

    val asDirection: Direction?
        get() = Direction.of(
            dv = this,
        )

    /**
     * Find the projection scale of this vector onto another vector
     *
     * @param b - the vector to project onto, must not be a zero vector
     */
    fun findProjectionScale(
        b: RawVector,
    ): Double = this.dot(b) / b.lengthSquared

    /**
     * Project this vector onto another vector
     *
     * @param b - the vector to project onto, must not be a zero vector
     */
    fun projectOnto(
        b: RawVector,
    ): RawVector = findProjectionScale(b) * b

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is RawVector -> false
        !x.equalsWithTolerance(other.x, absoluteTolerance = absoluteTolerance) -> false
        !y.equalsWithTolerance(other.y, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }
}

operator fun Double.times(v: RawVector): RawVector = RawVector(
    x = this * v.x,
    y = this * v.y,
)
