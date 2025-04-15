package app.geometry

class LineEquation(
    /**
     * The starting point of the line
     */
    internal val p0: RawVector,
    /**
     * The direction vector of the line
     */
    internal val dv: RawVector,
) {
    data class IntersectionSolution(
        val t0: Double,
        val t1: Double,
    )

    companion object {
        /**
         * Finds the unique intersection of two lines in 2D space.
         *
         * @return the intersection if it exists, or null if the lines are parallel
         */
        fun solveIntersection(
            l0: LineEquation,
            l1: LineEquation,
        ): IntersectionSolution? {
            val det = l0.dv.cross(l1.dv)
            if (det == 0.0) return null // The lines are parallel

            val pd = l1.p0 - l0.p0
            val t0 = pd.cross(l1.dv) / det
            val t1 = pd.cross(l0.dv) / det

            return IntersectionSolution(
                t0 = t0,
                t1 = t1,
            )
        }
    }

    init {
        require(dv != RawVector.zero) { "Direction vector cannot be zero" }
    }

    fun evaluate(t: Double): RawVector = p0 + dv * t

    /**
     * @param y - an y-value
     *
     * @return the t-value of the point on the line with the given y-value
     */
    fun findT(y: Double): Double = (y - p0.y) / dv.y
}
