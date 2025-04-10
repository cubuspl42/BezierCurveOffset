package app.algebra.linear.vectors.vector2

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.vectors.vector3.Vector3
import app.algebra.linear.VectorOrientation
import app.algebra.linear.VectorSpace
import app.geometry.Point
import kotlin.math.sqrt

data class Vector2<out Vo : VectorOrientation>(
    val x: Double,
    val y: Double,
) : NumericObject {
    /**
     * A vectors space of two-dimensional vectors
     */
    class Vector2VectorSpace<Vo : VectorOrientation> : VectorSpace<Vector2<Vo>>() {
        override val zero: Vector2<Vo> = zero()

        override fun add(
            u: Vector2<Vo>,
            v: Vector2<Vo>,
        ): Vector2<Vo> = u + v

        override fun subtract(
            u: Vector2<Vo>,
            v: Vector2<Vo>,
        ): Vector2<Vo> = u - v

        override fun scale(
            a: Double,
            v: Vector2<Vo>,
        ): Vector2<Vo> = v.scale(a)
    }


    companion object {
        fun <Vo : VectorOrientation> zero(): Vector2<Vo> = Vector2(x = 0.0, y = 0.0)

        fun of(
            x: Double,
            y: Double,
        ): Vector2<Nothing> = Vector2(x, y)

        fun <Vo : VectorOrientation> bisector(
            a: Vector2<Vo>,
            b: Vector2<Vo>,
        ): Vector2<Vo> = b.length * a + a.length * b

    }

    init {
        require(x.isFinite())
        require(y.isFinite())
    }

    /**
     * The length^2 of this vector
     */
    val lengthSquared: Double
        get() = x * x + y * y

    /**
     * The length of this vector
     */
    val length: Double
        get() = sqrt(lengthSquared)

    fun toVec3(
        z: Double = 1.0,
    ): Vector3<Vo> = Vector3(
        x = x,
        y = y,
        z = z,
    )

    /**
     * Calculates the dot product of this vector with another vector,
     * assuming that the other has a matching orientation
     */
    fun dotForced(
        other: Vector2<*>,
    ): Double = x * other.x + y * other.y

    fun cross(
        other: Vector2<*>,
    ): Double = x * other.y - y * other.x

    fun scale(
        factor: Double,
    ): Vector2<Vo> {
        require(factor.isFinite())
        return Vector2(
            x = x * factor,
            y = y * factor,
        )
    }

    /**
     * The counterclockwise perpendicular vector
     */
    val perpendicular: Vector2<Vo>
        get() = Vector2(x = -y, y = x)

    override fun equalsWithTolerance(
        other: NumericObject, absoluteTolerance: Double
    ): Boolean = when {
        other !is Vector2<*> -> false
        !x.equalsWithTolerance(other.x, absoluteTolerance = absoluteTolerance) -> false
        !y.equalsWithTolerance(other.y, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }

    fun toPoint() = Point.of(this)
}

operator fun <Vo : VectorOrientation> Vector2<Vo>.plus(
    other: Vector2<Vo>,
): Vector2<Vo> = Vector2(
    x = x + other.x,
    y = y + other.y,
)

operator fun <Vo : VectorOrientation> Vector2<Vo>.minus(
    other: Vector2<Vo>,
): Vector2<Vo> = Vector2(
    x = x - other.x,
    y = y - other.y,
)

operator fun <Vo : VectorOrientation> Vector2<Vo>.unaryMinus(): Vector2<Vo> = Vector2.of(
    x = -x,
    y = -y,
)

operator fun <Vo : VectorOrientation> Double.times(
    v: Vector2<Vo>,
): Vector2<Vo> = Vector2.of(
    x = this * v.x,
    y = this * v.y,
)

operator fun <Vo : VectorOrientation> Vector2<Vo>.div(
    divisor: Double,
): Vector2<Vo> = Vector2.of(
    x = x / divisor,
    y = y / divisor,
)
