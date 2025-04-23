package app.geometry

import app.algebra.NumericObject.Tolerance

data class GeometricTolerance(
    val negligibleDistance: Double,
    val greatDistance: Double,
) {
    /**
     * The tolerance for a distance between two points
     */
    val distanceTolerance: Tolerance.Absolute
        get() = Tolerance.Absolute(
            absoluteTolerance = negligibleDistance,
        )

    /**
     * The tolerance for the |l|^2, where l is a distance between two points
     */
    val distanceSqTolerance: Double = negligibleDistance * negligibleDistance

    /**
     * The tolerance for |Δd|^2, where Δd is d2 - d1, where d1 and d2 are two normalized direction vectors
     */
    val directionDeltaSqTolerance = (negligibleDistance * negligibleDistance) / (greatDistance * greatDistance)
}
