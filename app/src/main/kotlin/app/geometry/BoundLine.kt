package app.geometry

/**
 * A bound line in 2D Euclidean space, i.e. a line going through the given point (called "origin").
 */
data class BoundLine(
    /**
     * The origin
     */
    val originPoint: Point,
    /**
     * The bi-direction of this line
     */
    override val biDirection: BiDirection,
): Line() {
    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): BoundLine = Ray.inDirection(
            point = point,
            direction = direction,
        ).containingLine
    }

    override val representativePoint: Point
        get() = originPoint
}
