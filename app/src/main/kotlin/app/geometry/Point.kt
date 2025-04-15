package app.geometry

import app.algebra.NumericObject
import app.equalsZeroApproximately
import app.geometry.transformations.Transformation
import app.geometry.transformations.Translation
import java.awt.geom.Path2D

@Suppress("DataClassPrivateConstructor")
data class Point internal constructor(
    val pv: RawVector,
) : NumericObject {
    companion object {
        val origin = Point(
            pv = RawVector.zero,
        )

        fun of(
            pv: RawVector,
        ): Point = Point(
            pv = pv,
        )

        fun of(
            px: Double,
            py: Double,
        ): Point = of(
            pv = RawVector(
                x = px,
                y = py,
            ),
        )

        fun midPoint(
            a: Point,
            b: Point,
        ): Point = Point.of(
            pv = a.pv + (b.pv - a.pv) / 2.0,
        )

        fun areCollinear(
            a: Point,
            b: Point,
            c: Point,
            epsilon: Double = Constants.epsilon,
        ): Boolean {
            val ab = b.pv - a.pv
            val ac = c.pv - a.pv
            return ab.cross(ac).equalsZeroApproximately(epsilon = epsilon)
        }

        fun makeCollinear(
            a: Point,
            b: Point,
            base: Point,
        ): Pair<Point, Point> {
            val biRay = BiRay.fromPoints(
                basePoint = base,
                directionPoint1 = a,
                directionPoint2 = b,
            ) ?: run {
                // If one of the points is the same as the base point, these
                // are essentially two points. Two points are always collinear.
                return Pair(a, b)
            }

            val bisectingRay = biRay.bisectingRay ?: run {
                // If both rays (BA and AB) point in the opposite directions,
                // A and B are definitely collinear
                return Pair(a, b)
            }

            val projectionLine = bisectingRay.perpendicularLine

            return Pair(
                a.snapTo(projectionLine),
                b.snapTo(projectionLine),
            )
        }
    }

    constructor(
        px: Double,
        py: Double,
    ) : this(
        pv = RawVector(
            x = px,
            y = py,
        ),
    )

    constructor(
        px: Int,
        py: Int,
    ) : this(
        px = px.toDouble(),
        py = py.toDouble(),
    )

    val x: Double
        get() = this.pv.x

    val y: Double
        get() = this.pv.y

    fun translationTo(
        other: Point,
    ): Translation = Translation.of(
        tv = other.pv - this.pv,
    )

    fun distanceTo(
        other: Point,
    ): Double = (other.pv - this.pv).length

    fun distanceSquaredTo(
        other: Point,
    ): Double = (other.pv - this.pv).lengthSquared

    /**
     * @param other - point to find the direction to
     * @return direction, or null if this point is effectively the same as the
     * other point
     */
    fun directionTo(
        other: Point,
    ): Direction? = Direction.of(
        dv = other.pv - this.pv,
    )

    /**
     * @param direction - direction to move in, must not be a zero vector
     * @param distance - distance to move in the direction
     * @return point moved in the given direction, or null if the direction was
     * numerically
     */
    fun translateInDirection(
        direction: Direction,
        distance: Double,
    ): Point = Translation.inDirection(
        direction, distance,
    ).translate(this)

    /**
     * @param origin - point to move away from, must be a different point
     * @param distance - distance to move away from the origin
     * @return the moved point, or null if [origin] was the same as this point
     */
    fun moveAway(
        origin: Point,
        distance: Double,
    ): Point? {
        require(distance.isFinite())

        val direction = directionTo(origin) ?: return null

        return translateInDirection(
            direction = direction,
            distance = distance,
        )
    }

    fun castRay(
        direction: Direction,
    ): Ray = Ray.inDirection(
        point = this,
        direction = direction,
    )

    fun snapTo(line: Line): Point {
        val l = line.rawLine
        val pd = l.p0 - this.pv
        val t = pd.findProjectionScale(l.dv)
        return l.evaluate(t = t)
    }

    fun dump(): String = "Point.of(${"%.2f".format(this.pv.x)}, ${"%.2f".format(this.pv.y)})"

    fun transformVia(
        transformation: Transformation,
    ): Point = transformation.transform(this)

    fun translateVia(
        translation: Translation,
    ): Point = translation.translate(this)

    // TODO: Geometric tolerance
    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double
    ): Boolean = when {
        other !is Point -> false
        !other.pv.equalsWithTolerance(other.pv, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }
}

fun Path2D.moveTo(p: Point) {
    moveTo(p.x, p.y)
}

fun Path2D.lineTo(p: Point) {
    lineTo(p.x, p.y)
}

fun Path2D.quadTo(p1: Point, p2: Point) {
    quadTo(p1.x, p1.y, p2.x, p2.y)
}

fun Path2D.cubicTo(p1: Point, p2: Point, p3: Point) {
    curveTo(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y)
}
