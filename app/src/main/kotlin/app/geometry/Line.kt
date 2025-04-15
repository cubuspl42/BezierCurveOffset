package app.geometry

/**
 * A line in 2D Euclidean space
 */
class Line(
    internal val rawLine: RawLine,
) : Curve() {
    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Line = Line(
            rawLine = RawLine.of(
                p0 = point.pv,
                p1 = point.pv +  direction.dv,
            )!!,
        )
    }

    fun findIntersection(
        other: Line,
    ): Point? {
        val l0 = this.rawLine
        val l1 = other.rawLine

        val solution = RawLine.findIntersection(
            rawLine0 = l0,
            rawLine1 = l1,
        ) ?: return null

        val pi0 = l0.evaluate(t = solution.t0)

        assert(
            pi0.equalsWithTolerance(
                l1.evaluate(t = solution.t1),
                absoluteTolerance = Constants.epsilon,
            ),
        )

        return pi0
    }

    override fun evaluate(
        t: Double,
    ): Point = rawLine.evaluate(t = t)

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }
}
