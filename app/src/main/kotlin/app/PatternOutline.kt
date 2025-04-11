package app

import app.PatternSvg.Marker
import app.geometry.Point
import app.geometry.curves.LineSegment
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.splines.ClosedSpline
import app.geometry.splines.Spline

data class PatternOutline(
    val segments: List<Segment>,
) {
    data class PatternOutlineParams(
        val segmentParamsByEdgeHandle: Map<EdgeHandle, SegmentParams>,
    ) {
        data class EdgeHandle(
            val firstKnotName: String,
            val secondKnotName: String,
        )

        data class SegmentParams(
            val seamAllowanceKind: SeamAllowanceKind,
        )

        fun getSegmentParams(
            edgeHandle: EdgeHandle,
        ): SegmentParams? = segmentParamsByEdgeHandle[edgeHandle]
    }

    companion object {
        fun fromMarkedSpline(
            markedSpline: ClosedSpline<*, *, Marker?>,
            params: PatternOutlineParams,
        ): PatternOutline = PatternOutline(
            segments = markedSpline.segments.withPreviousCyclic().map { (prevSegment, segment) ->
                val prevBezierEdge = prevSegment.edge as? CubicBezierCurve.Edge
                val bezierEdge = segment.edge as? CubicBezierCurve.Edge

                PatternOutline.Segment(
                    originKnot = OuterKnot(
                        rearHandlePosition = prevBezierEdge?.control1,
                        knotPosition = segment.startKnot,
                        frontHandlePosition = bezierEdge?.control1,
                    ),
                    innerKnots = emptyList(),
                    seamAllowanceKind = SeamAllowanceKind.Standard,
                )
            },
        )
    }

    sealed class Knot {
        abstract val rearHandlePosition: Point?
        abstract val knotPosition: Point
        abstract val frontHandlePosition: Point?
    }

    data class OuterKnot(
        override val rearHandlePosition: Point?,
        override val knotPosition: Point,
        override val frontHandlePosition: Point?,
    ) : Knot()

    data class InnerKnot(
        override val knotPosition: Point,
        val handleRod: HandleRod?,
    ) : Knot() {
        data class HandleRod(
            val rearHandlePosition: Point,
            val frontStringLength: Double,
        ) {
            fun determineFrontHandlePosition(
                knotPosition: Point,
            ): Point = knotPosition.translateVia(
                rearHandlePosition.translationTo(knotPosition).extend(frontStringLength),
            )

            init {
                require(frontStringLength > 0.0)
            }
        }

        override val rearHandlePosition: Point?
            get() = handleRod?.rearHandlePosition

        override val frontHandlePosition: Point?
            get() = handleRod?.determineFrontHandlePosition(knotPosition = knotPosition)
    }

    data class Segment(
        val originKnot: OuterKnot,
        val innerKnots: List<InnerKnot>,
        val seamAllowanceKind: SeamAllowanceKind,
    ) {
        val knots: List<Knot>
            get() = listOf(originKnot) + innerKnots
    }

    val closedSpline: ClosedSpline<*, SeamAllowanceKind, *>
        get() = ClosedSpline(
            segments = segments.withNextCyclic().flatMap { (segment, nextSegment) ->
                segment.knots.withNext(
                    outerRight = nextSegment.originKnot,
                ).map { (knot, nextKnot) ->
                    val startKnot = knot.knotPosition
                    val endKnot = nextKnot.knotPosition


                    val firstControl = knot.frontHandlePosition
                    val secondControl = nextKnot.rearHandlePosition

                    Spline.Segment(
                        startKnot = startKnot,
                        edge = when {
                            firstControl == null && secondControl == null -> LineSegment.Edge
                            else -> CubicBezierCurve.Edge(
                                control0 = firstControl ?: startKnot,
                                control1 = secondControl ?: endKnot,
                            )
                        },

                        edgeMetadata = segment.seamAllowanceKind,
                        startKnotMetadata = null,
                    )
                }
            },
        )
}
