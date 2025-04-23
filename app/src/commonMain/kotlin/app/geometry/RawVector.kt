package app.geometry

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsWithTolerance
import app.algebra.linear.VectorSpace
import app.algebra.linear.vectors.vector2.Vector1x2
import app.algebra.linear.vectors.vector2.Vector2x1
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
    fun rotate(
        fi: Double,
    ): RawVector = rotate(
        angle = PrincipalAngle.Explicit(fi = fi),
    )

    fun rotate(angle: PrincipalAngle): RawVector {
        val cosFi = angle.cosFi
        val sinFi = angle.sinFi

        return RawVector(
            x = x * cosFi - y * sinFi,
            y = x * sinFi + y * cosFi,
        )
    }

    val reflectY: RawVector
        get() = RawVector(
            x = x,
            y = -y,
        )

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

    val horizontal: Vector1x2 = Vector1x2(
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

    fun angleBetween(
        reference: RawVector
    ): PrincipalAngleBetweenVectors = PrincipalAngleBetweenVectors(
        reference = reference,
        subject = this,
    )

    fun angleBetweenXAxis(): PrincipalAngle? = PrincipalAngleWithXAxis.of(
        subject = this,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is RawVector -> false
        !x.equalsWithTolerance(other.x, tolerance = tolerance) -> false
        !y.equalsWithTolerance(other.y, tolerance = tolerance) -> false
        else -> true
    }
}

operator fun Double.times(v: RawVector): RawVector = RawVector(
    x = this * v.x,
    y = this * v.y,
)
