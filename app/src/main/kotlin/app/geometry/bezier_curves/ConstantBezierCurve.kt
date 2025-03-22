package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_formulas.BezierFormula
import app.algebra.bezier_formulas.ConstantBezierFormula
import app.algebra.polynomial_formulas.ConstantFormula
import app.geometry.Point
import app.geometry.bezier_splines.OpenBezierSpline

/**
 * A constant Bézier curve (a point)
 */
@Suppress("DataClassPrivateConstructor")
data class ConstantBezierCurve private constructor(
    val point: Point,
) : BezierCurve<ConstantBezierCurve>() {
    companion object {
        fun of(
            start: Point,
            point: Point,
            end: Point,
        ): ConstantBezierCurve = ConstantBezierCurve(
            point = point,
        )
    }

    override fun splitAt(
        t: Double,
    ): Pair<ConstantBezierCurve, ConstantBezierCurve> = Pair(this, this)

    override fun splitAtMultiple(
        tValues: Set<Double>,
    ): OpenBezierSpline? = null

    override fun findOffsetSpline(
        strategy: ProperBezierCurve.OffsetStrategy,
        offset: Double,
    ): OpenBezierSpline? = null

    override val start: Point
        get() = point

    override val end: Point
        get() = point

    override val firstControl: Point
        get() = point

    override val lastControl: Point
        get() = point

    override val basisFormula = ConstantBezierFormula(
        vectorSpace = Vector.VectorVectorSpace,
        weight0 = point.pv,
    )

    override val asProper: Nothing?
        get() = null
}
