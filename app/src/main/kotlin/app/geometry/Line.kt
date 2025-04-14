package app.geometry

/**
 * A line in 2D Euclidean space
 */
class Line(
    internal val lineEquation: LineEquation,
) : Curve() {
    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Line = Line(
            lineEquation = LineEquation(
                p0 = point.pvRaw,
                dv = direction.dvRaw,
            ),
        )
    }

    fun findIntersection(
        other: Line,
    ): Point? {
        val l0 = this.lineEquation
        val l1 = other.lineEquation

        val solution = LineEquation.solveIntersection(
            l0 = l0,
            l1 = l1,
        ) ?: return null

        val pi0 = l0.evaluate(t = solution.t0)

        assert(
            pi0.equalsWithTolerance(
                l1.evaluate(t = solution.t1),
                absoluteTolerance = Constants.epsilon,
            ),
        )

        return pi0.asPoint
    }

    override fun evaluate(
        t: Double,
    ): Point = lineEquation.evaluate(t = t).asPoint

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }
}
