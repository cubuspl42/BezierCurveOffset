package app.geometry.splines

import app.WrappedSvgPathSeg
import app.asList
import app.dump
import app.elementWiseAs
import app.geometry.BoundingBox
import app.geometry.curves.SegmentCurve
import app.geometry.transformations.Transformation
import app.uncons
import app.withNext
import app.withNextCyclic
import app.withPrevious
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg

class ClosedSpline<
        out CurveT : SegmentCurve<CurveT>,
        out SegmentMetadata,
        >(
    /**
     * The cyclic chain of links, must not be empty
     */
    override val segments: List<Segment<CurveT, SegmentMetadata>>,
) : Spline<CurveT, SegmentMetadata>() {
    sealed class ContourSegmentMetadata {
        data object Corner : ContourSegmentMetadata() {
            override val offsetDeviation = null
        }

        data class Side(
            val offsetMetadata: SegmentCurve.OffsetSegmentMetadata,
        ) : ContourSegmentMetadata() {
            override val offsetDeviation: Double
                get() = offsetMetadata.globalDeviation
        }

        abstract val offsetDeviation: Double?
    }

    abstract class ContourOffsetStrategy<in SegmentMetadata> {
        data class Constant(
            val offset: Double,
        ) : ContourOffsetStrategy<Any?>() {
            override fun determineOffsetParams(
                segmentMetadata: Any?,
            ): SegmentCurve.OffsetSplineParams = SegmentCurve.OffsetSplineParams(
                offset = offset,
            )
        }

        abstract fun determineOffsetParams(
            segmentMetadata: SegmentMetadata,
        ): SegmentCurve.OffsetSplineParams
    }

    companion object {
        fun interconnect(
            splines: List<OpenSpline<*, SegmentCurve.OffsetSegmentMetadata>>,
        ): ClosedSpline<*, ContourSegmentMetadata> {
            require(splines.isNotEmpty())

            val segments = splines.withNextCyclic().flatMap { (spline, nextSpline) ->
                spline.segments.map { segment ->
                    segment.mapMetadata { offsetSegmentMetadata ->
                        ContourSegmentMetadata.Side(offsetMetadata = offsetSegmentMetadata)
                    }
                } + listOfNotNull(
                    Segment.lineSegment(
                        startKnot = spline.terminator.endKnot,
                        metadata = ContourSegmentMetadata.Corner,
                    ),
                    spline.backRay!!.intersect(nextSpline.frontRay!!)?.let { intersectionPoint ->
                        Segment.lineSegment(
                            startKnot = intersectionPoint,
                            metadata = ContourSegmentMetadata.Corner,
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
    ): ClosedSpline<*, ContourSegmentMetadata>? = findContourSpline(
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
        offsetStrategy: ContourOffsetStrategy<SegmentMetadata>,
    ): ClosedSpline<*, ContourSegmentMetadata>? {
        val offsetSplines = subSegments.mapNotNull { subSegment ->
            val subCurve = subSegment.segmentCurve
            val segmentMetadata = subSegment.segmentMetadata

            subCurve.findOffsetSpline(
                params = offsetStrategy.determineOffsetParams(
                    segmentMetadata = segmentMetadata,
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

    fun <NewMetadata> transformMetadata(
        transform: (Spline.Segment<CurveT, SegmentMetadata>) -> NewMetadata,
    ): ClosedSpline<CurveT, NewMetadata> = ClosedSpline(
        segments = segments.map { segment ->
            segment.replaceMetadata(
                newMetadata = transform(segment)
            )
        },
    )

    val simplified: ClosedSpline<*, SegmentMetadata>
        get() {
            val segments = segments.withNext(
                outerRight = rightEdgeNode,
            ).map { (segment, nextNode) ->
                segment.simplify(
                    endKnot = nextNode.frontKnot,
                )
            }

            return ClosedSpline<SegmentCurve<*>, SegmentMetadata>(
                segments = segments,
            )
        }

    fun dump() = """
        ClosedSpline(
            segments = ${segments.dump()},
        )
    """.trimIndent()
}

val ClosedSpline<*, ClosedSpline.ContourSegmentMetadata>.globalOffsetDeviation
    get() = segments.mapNotNull { it.metadata.offsetDeviation }.max()

fun SVGPathElement.toClosedSpline(): ClosedSpline<*, *> {
    val svgPathSegs = pathSegList.asList()

    require(svgPathSegs.last().pathSegType == SVGPathSeg.PATHSEG_CLOSEPATH)

    val pathSegs = svgPathSegs.dropLast(1).map {
        WrappedSvgPathSeg.fromSvgPathSeg(it)
    }

    val (firstPathSeg, tailPathSegs) = pathSegs.uncons()!!

    val originPathSeg = firstPathSeg as WrappedSvgPathSeg.MoveTo
    val edgePathSegs = tailPathSegs.elementWiseAs<WrappedSvgPathSeg.CurveTo>()

    val segments = edgePathSegs.withPrevious(
        outerLeft = originPathSeg,
    ).map { (prevPathSeg, pathSeg) ->
        val startKnot = prevPathSeg.finalPoint
        Spline.Segment(
            startKnot = startKnot,
            edge = pathSeg.toEdge(startKnot),
            metadata = null,
        )
    }

    return ClosedSpline(
        segments = segments,
    )
}
