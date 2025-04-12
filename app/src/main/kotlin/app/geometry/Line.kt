package app.geometry

import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector2.minus
import app.algebra.linear.vectors.vector2.plus

/**
 * A line in 2D Euclidean space
 */
class Line(
    internal val rawLine: RawLine,
) {
    // TODO: Nuke?
    /**
     * One of the infinitely many points lying on the line
     */
    val representativePoint: Point
        get() = rawLine.p0.asPoint

    /**
     * The bi-direction of this line
     */
    val biDirection: BiDirection
        get() = rawLine.dv.asBiDirection!!

    companion object {
        fun inDirection(
            point: Point,
            direction: Direction,
        ): Line = Line(
            rawLine = RawLine(
                p0 = point.pvRaw,
                dv = direction.dvRaw,
            ),
        )
    }

    val pv: Vector2<*>
        get() = representativePoint.pv

    val dv: Vector2<*>
        get() = biDirection.dv


    val dvRaw: RawVector
        get() = rawLine.dv

    private fun evaluate(
        t: Double,
    ): Vector2<*> = pv + dv.scale(t)

    fun findIntersectionPoint(
        other: Line,
    ): Point? {
        val det = dv.cross(other.dv)
        if (det == 0.0) return null // The lines are parallel

        val ds = other.pv - pv
        val u = ds.cross(other.dv) / det

        return Point.of(
            pv = evaluate(u)
        )
    }

    override fun equals(other: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun hashCode(): Int {
        throw UnsupportedOperationException()
    }
}
