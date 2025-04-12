package app.geometry

class RawLine(
    internal val p0: RawVector,
    internal val dv: RawVector,
) {
    data class Intersection(
        val t0: Double,
        val t1: Double,
    )

    companion object {
        /**
         * Finds the unique intersection of two lines in 2D space.
         *
         * @return the intersection if it exists, or null if the lines are parallel
         */
        fun findUniqueIntersection(
            l0: RawLine,
            l1: RawLine,
        ): Intersection? {
            val det = l0.dv.cross(l1.dv)
            if (det == 0.0) return null // The rays are parallel

            val pd = l1.p0 - l0.p0
            val t0 = pd.cross(l1.dv) / det
            val t1 = pd.cross(l0.dv) / det

            return Intersection(
                t0 = t0,
                t1 = t1,
            )
        }
    }

    fun evaluate(t: Double): RawVector {
        return RawVector(
            x = p0.x + t * dv.x,
            y = p0.y + t * dv.y,
        )
    }
}
