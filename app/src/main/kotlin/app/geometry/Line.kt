package app.geometry

import app.algebra.euclidean.ParametricLineFunction

/**
 * A line in 2D Euclidean space
 */
class Line(
    internal val representativePoint: Point,
    internal val biDirection: BiDirection,
) : Curve() {
    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Line = Line(
            representativePoint = point,
            biDirection = direction.biDirection,
        )
    }

    fun findIntersection(
        other: Line,
    ): Point? = toParametricLineFunction().solveIntersection(
        other.toParametricLineFunction()
    )?.let { t ->
        evaluate(t = t)
    }

    fun toParametricLineFunction() = ParametricLineFunction(
        s = representativePoint.pv,
        d = biDirection.dv,
    )

    override fun containsTValue(t: Double): Boolean = true

    override fun evaluate(
        t: Double,
    ): Point = toParametricLineFunction().apply(t).asPoint

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }
}
