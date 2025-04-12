package app.geometry

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector2.minus
import app.algebra.linear.vectors.vector2.unaryMinus

/**
 * A direction in the 2D Euclidean space, i.e. a unit vector with a given direction and orientation.
 */
@JvmInline
value class Direction private constructor(
    /**
     * The unit vector determining the direction
     */
    val dv: Vector2<*>,
) : NumericObject, GeometricObject {
    companion object {
        /**
         * @return A direction described by [dv], or null if [dv] is effectively
         * a zero vector
         */
        fun of(
            dv: Vector2<*>,
        ): Direction? = when {
            dv.lengthSquared == 0.0 -> null
            else -> Direction(dv = dv.normalized)
        }
    }

    init {
        require(
            dv.lengthSquared.equalsWithTolerance(
                1.0,
                absoluteTolerance = 1e-6,
            ),
        )
    }

    val dvRaw: RawVector
        get() = dv.raw

    val perpendicular: Direction
        get() = Direction(dv = dv.perpendicular)

    val biDirection: BiDirection
        get() = BiDirection(
            representativeDirection = this,
        )

    val opposite: Direction
        get() = Direction(dv = -dv)

    fun angleBetween(
        other: Direction
    ): Angle = Angle(
        a = dv,
        b = other.dv,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is Direction -> false
        else -> angleBetween(other).isZeroWithRadialTolerance(
            tolerance = RadialTolerance.ofAbsoluteTolerance(absoluteTolerance)
        )
    }

    override fun equalsWithTolerance(
        other: GeometricObject,
        tolerance: GeometricTolerance,
    ): Boolean = when {
        other !is Direction -> false
        else -> {
            val deltaDv = other.dv - dv
            deltaDv.lengthSquared < tolerance.directionDeltaSqTolerance
        }
    }
}

internal fun Vector2<*>.toDirection(): Direction? = Direction.of(dv = this)
