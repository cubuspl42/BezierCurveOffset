package app.geometry

import app.algebra.Vector

/**
 * A bi-ray in 2D Euclidean space, i.e. a pair of rays sharing the same starting point
 */
data class BiRay(
    /**
     * The initial point of the bi-ray
     */
    val point: Point,
    /**
     * The direction of the first ray
     */
    val firstDirection: Direction,
    /**
     * The direction of the second ray
     */
    val secondDirection: Direction,
) {
    companion object {
        fun fromPoints(
            basePoint: Point,
            directionPoint1: Point,
            directionPoint2: Point,
        ): BiRay {
            require(directionPoint1 != basePoint)
            require(directionPoint2 != basePoint)

            return BiRay(
                point = basePoint,
                firstDirection = basePoint.directionTo(directionPoint1),
                secondDirection = basePoint.directionTo(directionPoint2),
            )
        }
    }

    val firstRay: Ray
        get() = Ray.inDirection(
            point = point,
            direction = firstDirection,
        )

    val secondRay: Ray
        get() = Ray.inDirection(
            point = point,
            direction = secondDirection,
        )

    /**
     * The unique bisecting ray, or null if the two rays point in the totally opposite directions
     */
    val bisectingRay: Ray?
        get() {
            val b = Vector.bisector(firstDirection.d, secondDirection.d)

            if (b == Vector.zero) {
                return null
            }

            return Ray.inDirection(
                point = point,
                direction = b.toDirection(),
            )
        }

    /**
     * The proper tangent line, or null if the two rays point in the totally opposite directions
     */
    val tangentLine: Line? = bisectingRay?.perpendicularLine
}
