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
        ): PatternOutline {
            val intermediateSegments = IntermediateSegment.fromMarkedSpline(
                markedSpline = markedSpline,
            )

            return PatternOutline(
                segments = intermediateSegments.withNextCyclic().map { (segment, nextSegment) ->
                    segment.parametrize(
                        nextSegment = nextSegment,
                        outlineParams = params,
                    )
                },
            )
        }
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
            companion object {
                fun fromChunk(
                    chunk: ClosedSpline.KnotChunk<*, *, *>,
                ): HandleRod? {
                    val prevBezierEdge = chunk.prevEdge as? CubicBezierCurve.Edge
                    val knot = chunk.knot
                    val nextBezierEdge = chunk.nextEdge as? CubicBezierCurve.Edge

                    val rearControl = prevBezierEdge?.control1 ?: return null
                    val frontControl = nextBezierEdge?.control0 ?: return null

                    return HandleRod(
                        rearHandlePosition = rearControl,
                        frontStringLength = knot.distanceTo(frontControl),
                    )
                }
            }

            fun determineFrontHandlePosition(
                knotPosition: Point,
            ): Point {
                val direction = rearHandlePosition.directionTo(knotPosition)!!
                return knotPosition.translateInDirection(
                    direction = direction,
                    distance = frontStringLength,
                )
            }

            init {
//                require(frontStringLength > 0.0)
            }
        }

        init {
            require(
                handleRod?.let { it.rearHandlePosition != knotPosition } ?: true,
            )
        }

        override val rearHandlePosition: Point?
            get() = handleRod?.rearHandlePosition

        override val frontHandlePosition: Point?
            get() = handleRod?.determineFrontHandlePosition(knotPosition = knotPosition)
    }

    data class IntermediateSegment(
        val knotName: String,
        val originKnot: OuterKnot,
        val innerKnots: List<InnerKnot>,
    ) {
        companion object {
            fun fromMarkedSpline(
                markedSpline: ClosedSpline<*, *, Marker?>,
            ): List<IntermediateSegment> {
                val knotChunkGroups = markedSpline.knotChunks.shiftWhile {
                    it.knotMetadata == null
                }.splitBy {
                    it.knotMetadata != null
                }

                return knotChunkGroups.map { knotChunkGroup ->
                    val (firstChunk, otherChunks) = knotChunkGroup.uncons() ?: throw IllegalArgumentException()
                    val firstKnotMarker = firstChunk.knotMetadata ?: throw IllegalArgumentException()

                    val originRearEdge = firstChunk.prevEdge as? CubicBezierCurve.Edge
                    val originFrontEdge = firstChunk.nextEdge as? CubicBezierCurve.Edge

                    IntermediateSegment(
                        knotName = firstKnotMarker.name,
                        originKnot = OuterKnot(
                            rearHandlePosition = originRearEdge?.control1,
                            knotPosition = firstChunk.knot,
                            frontHandlePosition = originFrontEdge?.control0,
                        ),
                        innerKnots = otherChunks.map { chunk ->
                            InnerKnot(
                                knotPosition = chunk.knot,
                                handleRod = InnerKnot.HandleRod.fromChunk(chunk = chunk),
                            )
                        },
                    )
                }
            }
        }

        fun parametrize(
            nextSegment: IntermediateSegment,
            outlineParams: PatternOutlineParams,
        ): Segment {
            val segmentParams = outlineParams.getSegmentParams(
                edgeHandle = PatternOutlineParams.EdgeHandle(
                    firstKnotName = knotName,
                    secondKnotName = nextSegment.knotName,
                ),
            )

            return Segment(
                originKnot = originKnot,
                innerKnots = innerKnots,
                seamAllowanceKind = segmentParams?.seamAllowanceKind ?: SeamAllowanceKind.Standard,
            )
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
