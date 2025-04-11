package app.geometry

import app.algebra.NumericObject
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector2.unaryMinus

/**
 * A direction in the 2D Euclidean space, i.e. an infinite set of non-zero vectors in which each pair (a, b) is pointing
 * in the exact same direction (b = ka, k > 0) or an infinite set of rays with the same defining angle.
 */
@JvmInline
value class Direction private constructor(
    /**
     * One of the infinitely many vectors pointing in this direction, must not
     * effectively be a zero vector
     */
    val dv: Vector2<*>,
) : NumericObject {
    companion object {
        /**
         * @return A direction described by [dv], or null if [dv] is effectively
         * a zero vector
         */
        fun of(
            dv: Vector2<*>,
        ): Direction? = when {
            dv.lengthSquared == 0.0 -> null
            else -> {
//                    require(dv.lengthSquared > 0.0001)

                Direction(dv = dv)
            }
        }
    }

    init {
        require(dv.lengthSquared != 0.0)
    }

    val perpendicular: Direction
        // If d is non-zero, its perpendicular vector will also be non-zero
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
        absoluteTolerance: Double
    ): Boolean = when {
        other !is Direction -> false
        else -> angleBetween(other).isZeroWithRadialTolerance(
            tolerance = RadialTolerance.ofAbsoluteTolerance(absoluteTolerance)
        )
    }
}

internal fun Vector2<*>.toDirection(): Direction? = Direction.of(dv = this)
