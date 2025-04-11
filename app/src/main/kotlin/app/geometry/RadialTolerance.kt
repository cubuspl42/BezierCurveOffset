package app.geometry

import kotlin.math.cos

data class RadialTolerance(
    val cosEps: Double,
) {
    companion object {
        fun ofAbsoluteTolerance(
            absoluteTolerance: Double,
        ): RadialTolerance {
            require(absoluteTolerance <= 0.01)
            return RadialTolerance(
                // For small eps values, (1 - eps) and cos(eps) can be used interchangeably.
                cosEps = 1 - absoluteTolerance,
            )
        }

        fun of(
            /**
             * The angle in radians
             */
            angle: Double,
        ): RadialTolerance = RadialTolerance(
            cosEps = cos(angle),
        )
    }

    val cosEpsThreshold: Double
        get() = 1.0 - cosEps

    /**
     * The cos^2 of the tolerance angle
     */
    val cosSqEps: Double = cosEps * cosEps

    val cosSqEpsThreshold: Double
        get() = 1.0 - cosSqEps

    init {
        require(cosEps in -1.0..1.0) { "cosEps must be in [-1, 1]" }
    }
}
