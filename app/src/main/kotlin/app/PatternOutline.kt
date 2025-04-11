package app

import app.geometry.Point
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.splines.ClosedSpline
import app.geometry.splines.Spline

data class PatternOutline(
    val segments: List<Segment>,
) {
    sealed class Knot {
        abstract val knotPosition: Point
        abstract val readHandlePosition: Point
        abstract val frontHandlePosition: Point
    }

    data class OuterKnot(
        override val knotPosition: Point,
        override val readHandlePosition: Point,
        override val frontHandlePosition: Point,
    ) : Knot()

    data class InnerKnot(
        override val knotPosition: Point,
        override val readHandlePosition: Point,
        val frontStringLength: Double,
    ) : Knot() {
        override val frontHandlePosition: Point
            get() = knotPosition.translateVia(
                readHandlePosition.translationTo(knotPosition).extend(frontStringLength),
            )

        init {
            require(frontStringLength > 0.0)
        }
    }

    data class Segment(
        val originKnot: OuterKnot,
        val innerKnots: List<InnerKnot>,
        val seamAllowanceKind: SeamAllowanceKind,
    ) {
        val knots: List<Knot>
            get() = listOf(originKnot) + innerKnots
    }

    val closedSpline: ClosedSpline<CubicBezierCurve, SeamAllowanceKind>
        get() = ClosedSpline(
            segments = segments.withNextCyclic().flatMap { (segment, nextSegment) ->
                segment.knots.withNext(
                    outerRight = nextSegment.originKnot,
                ).map { (knot, nextKnot) ->
                    Spline.Segment.bezier(
                        startKnot = knot.knotPosition,
                        control0 = knot.frontHandlePosition,
                        control1 = nextKnot.readHandlePosition,
                        metadata = segment.seamAllowanceKind,
                    )
                }
            },
        )
}
