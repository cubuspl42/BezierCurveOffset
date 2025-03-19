package app

import app.geometry.Direction
import app.geometry.Line
import app.geometry.Point

/**
 * A two-dimensional bound vector
 */
data class BoundVector(
    val point: Point,
    val vector: Vector,
) {
    val directionOrNull: Direction?
        get() = vector.directionOrNull

    val direction: Direction
        get() = vector.direction

    val length: Double
        get() = vector.length

    /**
     * The single line that contains this bound vector, or null if there are infinitely many such lines (the bound
     * vector is a zero vector).
     */
    val containingLineOrNull: Line?
        get() = vector.directionOrNull?.let {
            Line(
                representativePoint = point,
                representativeDirection = it,
            )
        }

    /**
     * The single line that contains this bound vector
     *
     * @throws IllegalStateException if this bound vector lies on infinitely many lines (is a bound zero vector)
     */
    val containingLine: Line =
        containingLineOrNull ?: throw IllegalStateException("Bound zero vectors lye on infinitely many lines")
}
