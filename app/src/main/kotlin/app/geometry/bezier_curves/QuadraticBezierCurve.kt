package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_binomials.BezierBinomial
import app.geometry.Point
import java.awt.geom.Path2D

/**
 * A quadratic Bézier curve. This model allows a specific case that could be
 * considered a degenerate curve, i.e. when the start point is the same as the
 * end point. Mathematically, this is a line segment, but lowering such a curve
 * to a linear Bézier curve is non-trivial. At the tip, such a curve has its
 * velocity equal to zero, which causes unfortunate corner cases.
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
        ): QuadraticBezierCurve = when {
            start == control -> QuadraticBezierCurve.of(
                start = start,
                control = control,
                end = end,
            )

            control == end -> QuadraticBezierCurve.of(
                start = start,
                control = control,
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

    override val basisFormula: BezierBinomial<Vector>
        get() = TODO("Not yet implemented")

    override fun splitAt(t: Double): Pair<BezierCurve<*>, BezierCurve<*>> {
        TODO("Not yet implemented")
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
