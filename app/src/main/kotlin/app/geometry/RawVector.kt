package app.geometry

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.vectors.vector2.Vector2
import kotlin.math.sqrt

data class RawVector(
    val x: Double,
    val y: Double,
) : NumericObject {
    operator fun plus(other: RawVector): RawVector = RawVector(
        x = x + other.x,
        y = y + other.y,
    )

    operator fun minus(other: RawVector): RawVector = RawVector(
        x = x - other.x,
        y = y - other.y,
    )

    operator fun RawVector.times(scalar: Double): RawVector = RawVector(
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
