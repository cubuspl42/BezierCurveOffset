package app.geometry

/**
 * A directed bound line in 2D Euclidean space, i.e. a directed line going
 * through the given point (called "origin").
 */
data class BoundLine(
    /**
     * The origin
     */
    val originPoint: Point,
    /**
     * The direction of this line
     */
    val direction: Direction,
) {
    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): BoundLine = BoundLine(
            originPoint = point,
            direction = direction,
        )
    }

    val biDirection: BiDirection
        get() = direction.biDirection

    val representativePoint: Point
        get() = originPoint
}
