package app.geometry

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector2.times
import app.geometry.transformations.Translation
import kotlin.math.sqrt

data class RawVector(
    val x: Double,
    val y: Double,
) : NumericObject {
    companion object {
        val zero = RawVector(
            x = 0.0,
            y = 0.0,
        )
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

    val lengthSq: Double
        get() = x * x + y * y

    val length: Double
        get() = sqrt(lengthSq)

    val asVector2: Vector2<Nothing>
        get() = Vector2.of(
            x = x,
            y = y,
        )

    val asPoint: Point
        get() = Point(
            pv = asVector2,
        )

    val asBiDirection: BiDirection?
        get() = BiDirection.of(
            dv = asVector2,
        )

    val asDirection: Direction?
        get() = Direction.of(
            dv = asVector2,
        )

    /**
     * Find the projection scale of this vector onto another vector
     *
     * @param b - the vector to project onto, must not be a zero vector
     */
    fun findProjectionScale(
        b: RawVector,
    ): Double = this.dot(b) / b.lengthSq

    /**
     * Project this vector onto another vector
     *
     * @param b - the vector to project onto, must not be a zero vector
     */
    fun projectOnto(
        b: RawVector,
    ): RawVector = findProjectionScale(b) * b

    override fun equalsWithTolerance(
        other: NumericObject, absoluteTolerance: Double
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
