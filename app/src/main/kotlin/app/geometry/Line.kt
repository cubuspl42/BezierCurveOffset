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

    val pv: RawVector
        get() = representativePoint.pvRaw

    val dv: RawVector
        get() = biDirection.dv

    val dvRaw: RawVector
        get() = rawLine.dv

    fun findIntersectionPoint(
        other: Line,
    ): Point? {
        val l0 = this.rawLine
        val l1 = other.rawLine

        val intersection = RawLine.findUniqueIntersection(
            l0 = l0,
            l1 = l1,
        ) ?: return null

        val p0 = l0.evaluate(t = intersection.t0)

        assert(
            p0.equalsWithTolerance(
                l1.evaluate(t = intersection.t1),
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
