package app.geometry.curves.bezier

import app.algebra.bezier_binomials.*
import app.geometry.*
import app.geometry.transformations.Rotation
import app.geometry.transformations.Transformation
import app.geometry.transformations.Translation

/**
 * A raw cubic Bézier curve (a Bézier curve of degree 3)
 */
@Suppress("DataClassPrivateConstructor")
data class RawCubicBezierCurve private constructor(
    val point0: Point,
    val point1: Point,
    val point2: Point,
    val point3: Point,
) : RawBezierCurve() {
    companion object {
        fun of(
            point0: Point,
            point1: Point,
            point2: Point,
            point3: Point,
        ): RawCubicBezierCurve = RawCubicBezierCurve(
            point0 = point0,
            point1 = point1,
            point2 = point2,
            point3 = point3,
        )

        fun findIntersections(
            rawLine: RawLine,
            bezierCurve: RawCubicBezierCurve,
        ): Set<IntersectionDetails<RawLine, RawCubicBezierCurve>> {
            val p0 = rawLine.p0
            val dv = rawLine.dv

            val angleBetweenXAxis = dv.angleBetweenXAxis() ?: return emptySet()
            val rotation = Rotation.byAngle(angle = -angleBetweenXAxis)
            val transformation = Translation.of(-p0).combineWith(rotation)

            val transformedBezierCurve = bezierCurve.transformVia(
                transformation = transformation,
            )

            val transformedBezierCurveY = transformedBezierCurve.basisFormula.componentYCubic
            val roots = transformedBezierCurveY.findRoots()

            return roots.map { t1 ->
                object : IntersectionDetails<RawLine, RawCubicBezierCurve>() {
                    override val point: Point
                        get() = bezierCurve.evaluate(t = t1)

                    override val t0: Double
                        get() = rawLine.findT(y = point.y)

                    override val t1: Double = t1
                }
            }.toSet()
        }

        fun findIntersections(
            rawBezierCurve0: RawCubicBezierCurve,
            rawBezierCurve1: RawCubicBezierCurve,
        ): Set<IntersectionDetails<RawCubicBezierCurve, RawCubicBezierCurve>> {
            TODO()
        }
    }

    fun transformVia(
        transformation: Transformation,
    ): RawCubicBezierCurve = mapPointWise {
        it.transformVia(
            transformation = transformation,
        )
    }

    fun mapPointWise(
        transform: (Point) -> Point,
    ): RawCubicBezierCurve = RawCubicBezierCurve(
        point0 = transform(point0),
        point1 = transform(point1),
        point2 = transform(point2),
        point3 = transform(point3),
    )

    override val basisFormula = CubicBezierBinomial(
        vectorSpace = RawVector.RawVectorSpace,
        weight0 = point0.pv,
        weight1 = point1.pv,
        weight2 = point2.pv,
        weight3 = point3.pv,
    )

    override fun evaluate(
        t: Double,
    ): Point = basisFormula.evaluate(t = t).asPoint
}
