package app.geometry

/**
 * A directed line bound to a point in 2D Euclidean space
 */
class BoundDirectedLine(
    internal val lineEquation: LineEquation,
) {
    enum class Section {
        Front, Back;

        companion object {
            fun of(t: Double): Section = if (t >= 0) Front else Back
        }
    }

    data class Intersection(
        val point: Point,
        val section: Section,
        val otherSection: Section,
    )

    companion object {
        fun inDirection(
            bindingPoint: Point,
            direction: Direction,
        ): BoundDirectedLine = BoundDirectedLine(
            lineEquation = LineEquation(
                p0 = bindingPoint.pvRaw,
                dv = direction.dvRaw,
            ),
        )
    }

    fun findIntersection(
        other: BoundDirectedLine,
    ): Intersection? {
        val l0 = this.lineEquation
        val l1 = other.lineEquation

        val solution = LineEquation.solveIntersection(
            l0 = l0,
            l1 = l1,
        ) ?: return null

        val t0 = solution.t0
        val t1 = solution.t1

        val pi0 = l0.evaluate(t = t0)

        assert(
            pi0.equalsWithTolerance(
                l1.evaluate(t = t1),
                absoluteTolerance = Constants.epsilon,
            ),
        )

        return Intersection(
            point = pi0.asPoint,
            section = Section.of(t = t0),
            otherSection = Section.of(t = t1),
        )
    }

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }
}
