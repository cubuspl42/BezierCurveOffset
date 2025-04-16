package app.geometry

import app.algebra.polynomials.ParametricPolynomial

class RawLine(
    /**
     * The starting point of the line
     */
    internal val p0: RawVector,
    /**
     * The direction vector of the line
     */
    internal val p1: RawVector,
) : Curve() {
    companion object {
        fun of(
            p0: RawVector,
            p1: RawVector,
        ): RawLine? = when {
            p0 != p1 -> RawLine(
                p0 = p0,
                p1 = p1,
            )

            else -> null
        }

        /**
         * Finds the unique intersection of two lines in 2D space.
         *
         * @return the intersection if it exists, or null if the lines are parallel
         */
        fun findIntersection(
            rawLine0: RawLine,
            rawLine1: RawLine,
        ): IntersectionDetails<RawLine, RawLine>? {
            val det = rawLine0.dv.cross(rawLine1.dv)
            if (det == 0.0) return null // The lines are parallel

            val pd = rawLine1.p0 - rawLine0.p0

            return object : IntersectionDetails<RawLine, RawLine>() {
                override val point: Point
                    get() = rawLine0.evaluate(t0)

                override val t0: Double
                    get() = pd.cross(rawLine1.dv) / det

                override val t1: Double
                    get() = pd.cross(rawLine0.dv) / det

            }
        }
    }

    internal val dv: RawVector
        get() = p1 - p0

    init {
        require(p0 != p1)
    }

    fun toParametricLineFunction(): ParametricLineFunction = ParametricLineFunction(
        s = p0,
        d = dv,
    )

    fun toGeneralLineFunction(): GeneralLineFunction = toParametricLineFunction().toGeneralLineFunction()

    fun toParametricPolynomial(): ParametricPolynomial = ParametricPolynomial.linear(
        a = dv,
        b = p0,
    )

    fun toGeneral(): GeneralLineFunction = GeneralLineFunction(
        a = dv.y,
        b = -dv.x,
        c = -(dv.y * p0.x + -dv.x * p0.y),
    )

    override fun evaluate(t: Double): Point = (p0 + dv * t).asPoint

    /**
     * @param y - an y-value
     *
     * @return the t-value of the point on the line with the given y-value
     */
    fun findT(y: Double): Double = (y - p0.y) / dv.y
}
