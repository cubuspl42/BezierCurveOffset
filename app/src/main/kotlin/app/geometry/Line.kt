package app.geometry

/**
 * A line in 2D Euclidean space
 */
class Line(
    internal val lineEquation: LineEquation,
) {
    // TODO: Nuke?
    /**
     * One of the infinitely many points lying on the line
     */
    val representativePoint: Point
        get() = lineEquation.p0.asPoint

    /**
     * The bi-direction of this line
     */
    val biDirection: BiDirection
        get() = lineEquation.dv.asBiDirection!!

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

    val pv: RawVector
        get() = representativePoint.pvRaw

    val dv: RawVector
        get() = biDirection.dv

    val dvRaw: RawVector
        get() = lineEquation.dv

    fun findIntersection(
        other: Line,
    ): Point? {
        val l0 = this.lineEquation
        val l1 = other.lineEquation

        val solution = LineEquation.solveIntersection(
            l0 = l0,
            l1 = l1,
        ) ?: return null

        val p0 = l0.evaluate(t = solution.t0)

        assert(
            p0.equalsWithTolerance(
                l1.evaluate(t = solution.t1),
                absoluteTolerance = 0.0001,
            ),
        )

        return p0.asPoint
    }

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }
}
