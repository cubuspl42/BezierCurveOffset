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
        /**
         * @return a bi-ray, or null if (at least) one of the given points is the
         * same as the base point
         */
        fun fromPoints(
            basePoint: Point,
            directionPoint1: Point,
            directionPoint2: Point,
        ): BiRay? {
            val firstDirection = basePoint.directionTo(directionPoint1) ?: return null
            val secondDirection = basePoint.directionTo(directionPoint2) ?: return null
            return BiRay(
                point = basePoint,
                firstDirection = firstDirection,
                secondDirection = secondDirection,
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
     * The unique bisecting ray, or null if the two rays point in the totally
     * opposite directions (which would indicate there are two bisecting rays)
     */
    val bisectingRay: Ray?
        get() {
            val b = Vector.bisector(firstDirection.dv, secondDirection.dv)

            val d = b.toDirection() ?: return null

            return Ray.inDirection(
                point = point,
                direction = d,
            )
        }
}
