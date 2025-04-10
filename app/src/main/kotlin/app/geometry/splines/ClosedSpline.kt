package app.geometry.splines

import app.dump
import app.geometry.BoundingBox
import app.geometry.curves.SegmentCurve
import app.geometry.transformations.Transformation
import app.withNext
import app.withNextCyclic

class ClosedSpline<
        out CurveT : SegmentCurve<CurveT>,
        out EdgeMetadata,
        >(
    /**
     * The cyclic chain of links, must not be empty
     */
    override val segments: List<Segment<CurveT, EdgeMetadata>>,
) : Spline<CurveT, EdgeMetadata>() {
    sealed class ContourEdgeMetadata {
        data object Corner : ContourEdgeMetadata() {
            override val offsetDeviation = null
        }

        data class Side(
            val offsetMetadata: SegmentCurve.OffsetEdgeMetadata,
        ) : ContourEdgeMetadata() {
            override val offsetDeviation: Double
                get() = offsetMetadata.globalDeviation
        }

        abstract val offsetDeviation: Double?
    }

    abstract class ContourOffsetStrategy<in EdgeMetadata> {
        data class Constant(
            val offset: Double,
        ) : ContourOffsetStrategy<Any?>() {
            override fun determineOffsetParams(
                edgeMetadata: Any?,
            ): SegmentCurve.OffsetSplineParams = SegmentCurve.OffsetSplineParams(
                offset = offset,
            )
        }

        abstract fun determineOffsetParams(
            edgeMetadata: EdgeMetadata,
        ): SegmentCurve.OffsetSplineParams
    }

    companion object {
        fun interconnect(
            splines: List<OpenSpline<*, SegmentCurve.OffsetEdgeMetadata>>,
        ): ClosedSpline<*, ContourEdgeMetadata> {
            require(splines.isNotEmpty())

            val segments = splines.withNextCyclic().flatMap { (spline, nextSpline) ->
                spline.segments.map { segment ->
                    segment.mapMetadata { offsetEdgeMetadata ->
                        ContourEdgeMetadata.Side(offsetMetadata = offsetEdgeMetadata)
                    }
                } + listOfNotNull(
                    Segment.lineSegment(
                        startKnot = spline.terminator.endKnot,
                        metadata = ContourEdgeMetadata.Corner,
                    ),
                    spline.backRay!!.intersect(nextSpline.frontRay!!)?.let { intersectionPoint ->
                        Segment.lineSegment(
                            startKnot = intersectionPoint,
                            metadata = ContourEdgeMetadata.Corner,
                        )
                    },
                )
            }

            return ClosedSpline(
                segments = segments,
            )
        }
    }

    init {
        require(segments.isNotEmpty())
    }

    override val nodes: List<Node> = segments

    override val rightEdgeNode: Node
        get() = segments.first()

    fun findBoundingBox(): BoundingBox = BoundingBox.unionAll(
        subCurves.map {
            it.findBoundingBox()
        },
    )

    fun transformVia(
        transformation: Transformation,
    ) = ClosedSpline(
        segments = segments.map { it.transformVia(transformation) },
    )

    fun findContourSpline(
        offset: Double,
    ): ClosedSpline<*, ContourEdgeMetadata>? = findContourSpline(
        offsetStrategy = ContourOffsetStrategy.Constant(
            offset = offset,
        ),
    )

    /**
     * Find the contour of this spline.
     *
     * @return The best found contour spline, or null if this spline is too tiny
     * to construct its contour
     */
    fun findContourSpline(
        offsetStrategy: ContourOffsetStrategy<EdgeMetadata>,
    ): ClosedSpline<*, ContourEdgeMetadata>? {
        val offsetSplines = subSegments.mapNotNull { subSegment ->
            val subCurve = subSegment.segmentCurve
            val edgeMetadata = subSegment.edgeMetadata

            subCurve.findOffsetSpline(
                params = offsetStrategy.determineOffsetParams(
                    edgeMetadata = edgeMetadata,
                ),
            )
        }

        if (offsetSplines.isEmpty()) {
            // TODO: Return a circle?
            return null
        }

        return ClosedSpline.interconnect(
            offsetSplines,
        )
    }

    val simplified: ClosedSpline<*, EdgeMetadata>
        get() {
            val segments = segments.withNext(
                outerRight = rightEdgeNode,
            ).map { (segment, nextNode) ->
                segment.simplify(
                    endKnot = nextNode.frontKnot,
                )
            }

            return ClosedSpline<SegmentCurve<*>, EdgeMetadata>(
                segments = segments,
            )
        }

    fun dump() = """
        ClosedSpline(
            segments = ${segments.dump()},
        )
    """.trimIndent()
}

val ClosedSpline<*, ClosedSpline.ContourEdgeMetadata>.globalOffsetDeviation
    get() = segments.mapNotNull { it.edgeMetadata.offsetDeviation }.max()
