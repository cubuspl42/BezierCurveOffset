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
) : Line() {
    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): BoundLine = BoundLine(
            originPoint = point,
            direction = direction,
        )
    }

    override val biDirection: BiDirection
        get() = direction.biDirection

    override val representativePoint: Point
        get() = originPoint
}
