package app

/**
 * A direction in 2D Euclidean space, i.e. an infinite set of vectors with cross product equal to zero
 */
data class Direction(
    /**
     * One of the vectors pointing in this direction, must not be a zero vector
     */
    val representativeVector: Vector,
) {
    init {
        assert(representativeVector != Vector.zero)
    }
}
