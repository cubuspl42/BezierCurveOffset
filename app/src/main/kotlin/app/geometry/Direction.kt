package app.geometry

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance

/**
 * A direction in the 2D Euclidean space, i.e. a unit vector with a given direction and orientation.
 */
class Direction private constructor(
    /**
     * The unit vector determining the direction
     */
    internal val dv: RawVector,
) : NumericObject, GeometricObject {
    companion object {
        /**
         * @return A direction described by [dv], or null if [dv] is effectively
         * a zero vector
         */
        fun of(
            dv: RawVector,
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
        get() = dv

    val perpendicular: Direction
        get() = Direction(dv = dv.perpendicular)

    val biDirection: BiDirection
        get() = BiDirection(
            representativeDirection = this,
        )

    val opposite: Direction
        get() = Direction(dv = -dv)

    fun angleBetween(
        reference: Direction
    ): PrincipalAngleBetweenVectors = dv.angleBetween(
        reference = reference.dv,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is Direction -> false
        else -> angleBetween(other).equalsWithRadialTolerance(
            other = PrincipalAngle.Zero,
            tolerance = RadialTolerance.ofAbsoluteTolerance(absoluteTolerance = absoluteTolerance),
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
