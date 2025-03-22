package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_binomials.BezierBinomial
import app.geometry.Point

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


    override fun moveInNormalDirection(
        distance: Double,
    ): QuadraticBezierCurve {
        TODO()
    }
}
