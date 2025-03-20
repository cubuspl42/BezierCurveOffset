package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_formulas.CubicBezierFormula
import app.geometry.*
import java.awt.geom.Path2D

data class CubicBezierCurve(
    override val start: Point,
    val control0: Point,
    val control1: Point,
    override val end: Point,
) : BezierCurve() {
    override val basisFormula = CubicBezierFormula(
        vectorSpace = Vector.VectorVectorSpace,
        weight0 = start.toVector(),
        weight1 = control0.toVector(),
        weight2 = control1.toVector(),
        weight3 = end.toVector(),
    )

    fun mapPointWise(
        transform: (Point) -> Point,
    ): CubicBezierCurve = CubicBezierCurve(
        start = transform(start),
        control0 = transform(control0),
        control1 = transform(control1),
        end = transform(end),
    )

    fun moveAwayPointWise(
        origin: Point,
        distance: Double,
    ): CubicBezierCurve = mapPointWise {
        it.moveAway(
            origin = origin,
            distance = distance,
        )
    }

    fun moveInDirectionPointWise(
        direction: Direction,
        distance: Double,
    ): CubicBezierCurve = mapPointWise {
        it.moveInDirection(
            direction = direction,
            distance = distance,
        )
    }

    fun translate(
        translation: Translation,
    ): CubicBezierCurve = mapPointWise {
        it.translate(translation = translation)
    }

    fun moveByOffset(
        offset: Double,
    ): CubicBezierCurve {
        val startNormalRay = normalRayFunction.startValue
        val startNormalLine = startNormalRay.containingLine

        val endNormalRay = normalRayFunction.endValue
        val endNormalLine = endNormalRay.containingLine

        val normalIntersectionPoint = startNormalLine.intersect(endNormalLine) ?: return moveInDirectionPointWise(
            // If there's no intersection point, the start and end vectors are parallel. We could choose either.
            direction = startNormalRay.direction,
            distance = offset,
        )

        return moveAwayPointWise(
            origin = normalIntersectionPoint,
            distance = offset,
        )
    }

    override fun toPath2D(): Path2D.Double = Path2D.Double().apply {
        moveTo(start)
        cubicTo(control0, control1, end)
    }
}
