package app.geometry.bezier_curves

import app.geometry.Point
import app.geometry.Ray
import app.geometry.Transformation
import app.geometry.splines.OpenSpline
import app.geometry.splines.Spline

abstract class SegmentCurve<out CurveT : SegmentCurve<CurveT>> {
    abstract class OffsetSplineApproximationResult<out CurveT : SegmentCurve<CurveT>> {
        abstract val offsetSpline: OpenSpline<CurveT>

        abstract val globalDeviation: Double
    }

    abstract fun findOffsetSpline(
        strategy: ProperBezierCurve.OffsetStrategy,
        offset: Double,
    ): OffsetSplineApproximationResult<CurveT>?

     fun toSpline(): OpenSpline<CurveT> = OpenSpline(
         segments = listOf(segment),
         terminator = Spline.Terminator(
             endKnot = end,
         ),
     )

    abstract class Edge<out CurveT : SegmentCurve<CurveT>> {
        abstract fun bind(
            startKnot: Point,
            endKnot: Point,
        ): CurveT

        abstract fun dump(): String

        abstract fun transformVia(
            transformation: Transformation,
        ): Edge<CurveT>
    }

    val segment: Spline.Segment<CurveT>
        get() = Spline.Segment(
            startKnot = start,
            edge = edge,
        )

    abstract val start: Point

    abstract val end: Point

    abstract val edge: Edge<CurveT>

    abstract val frontRay: Ray

    abstract val backRay: Ray
}
