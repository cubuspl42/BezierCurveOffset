package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_binomials.*
import app.geometry.Point
import java.awt.geom.Path2D

/**
 * A quadratic Bézier curve
 */
@Suppress("DataClassPrivateConstructor")
data class QuadraticBezierCurve private constructor(
    override val start: Point,
    val control: Point,
    override val end: Point,
) : ProperBezierCurve<QuadraticBezierCurve>() {
    companion object {
        /**
         * @return A best-effort non-degenerate quadratic Bézier curve with the
         * given points, or a respective lower-level Bézier curve
         */
        fun of(
            start: Point,
            control: Point,
            end: Point,
        ): BezierCurve<*> = when {
            start == control || control == end -> LineSegmentBezierCurve.of(
                start = start,
                end = end,
            )

            // If start == end, this constructs a degenerate curve
            else -> QuadraticBezierCurve(
                start = start,
                control = control,
                end = end,
            )
        }
    }

    init {
        require(start != control)
        require(control != end)
    }

    override val firstControl: Point
        get() = control

    override val lastControl: Point
        get() = control

    override val basisFormula = QuadraticBezierBinomial(
        vectorSpace = Vector.VectorVectorSpace,
        weight0 = start.toVector(),
        weight1 = control.toVector(),
        weight2 = end.toVector(),
    )

    override fun splitAt(
        t: Double,
    ): Pair<BezierCurve<*>, BezierCurve<*>> {
        val skeleton = basisFormula.findSkeletonQuadratic(t = t)
        val midPoint = skeleton.evaluateLinear(t = t).toPoint()

        return Pair(
            QuadraticBezierCurve.of(
                start = start,
                control = skeleton.point0,
                end = midPoint,
            ),
            QuadraticBezierCurve.of(
                start = midPoint,
                control = skeleton.point1,
                end = end,
            ),
        )
    }

    override fun toPath2D(): Path2D.Double {
        TODO("Not yet implemented")
    }

    override fun moveInNormalDirection(
        distance: Double,
    ): QuadraticBezierCurve {
        // For proper quadratic Bézier curves, the normals should theoretically
        // be defined for the whole range [0, 1], _but_ there's a huge caveat:
        // this class

        TODO()
    }
}
