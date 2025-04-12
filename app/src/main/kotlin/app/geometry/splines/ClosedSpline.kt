package app.geometry.splines

import app.WrappedSvgPathSeg
import app.asList
import app.dump
import app.elementWiseAs
import app.geometry.BoundingBox
import app.geometry.Point
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
        out EdgeMetadata,
        out KnotMetadata,
        >(
    /**
     * The cyclic chain of links, must not be empty
     */
    override val segments: List<Segment<CurveT, EdgeMetadata, KnotMetadata>>,
) : Spline<CurveT, EdgeMetadata, KnotMetadata>() {
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

    data class KnotChunk<
            out CurveT : SegmentCurve<CurveT>,
            out EdgeMetadata,
            out KnotMetadata,
            >(
        val prevEdgeMetadata: EdgeMetadata,
        val prevEdge: SegmentCurve.Edge<CurveT>,
        val knotMetadata: KnotMetadata,
        val knot: Point,
        val nextEdgeMetadata: EdgeMetadata,
        val nextEdge: SegmentCurve.Edge<CurveT>,
    )

    companion object {
        fun interconnect(
            splines: List<OpenSpline<*, SegmentCurve.OffsetEdgeMetadata, *>>,
        ): ClosedSpline<*, ContourEdgeMetadata, *> {
            require(splines.isNotEmpty())

            val segments = splines.withNextCyclic().flatMap { (spline, nextSpline) ->
                spline.segments.map { segment ->
                    segment.mapEdgeMetadata { offsetEdgeMetadata ->
                        ContourEdgeMetadata.Side(offsetMetadata = offsetEdgeMetadata)
                    }
                } + listOfNotNull(
                    Segment.lineSegment(
                        startKnot = spline.terminator.endKnot,
                        edgeMetadata = ContourEdgeMetadata.Corner,
                        knotMetadata = null,
                    ),
                    spline.backRay!!.findIntersection(nextSpline.frontRay!!)?.let { intersectionPoint ->
                        Segment.lineSegment(
                            startKnot = intersectionPoint,
                            edgeMetadata = ContourEdgeMetadata.Corner,
                            knotMetadata = null,
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

    override val nodes: List<Node<KnotMetadata>> = segments

    override val rightEdgeNode: Node<KnotMetadata>
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
    ): ClosedSpline<*, ContourEdgeMetadata, *>? = findContourSpline(
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
    ): ClosedSpline<*, ContourEdgeMetadata, *>? {
        val offsetSplines = edgeChunks.mapNotNull { subSegment ->
            val subCurve = subSegment.edgeCurve
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

    fun <NewKnotMetadata> transformKnotMetadata(
        transform: (Spline.Segment<CurveT, EdgeMetadata, KnotMetadata>) -> NewKnotMetadata,
    ): ClosedSpline<CurveT, EdgeMetadata, NewKnotMetadata> = ClosedSpline(
        segments = segments.map { segment ->
            segment.replaceKnotMetadata(
                newKnotMetadata = transform(segment)
            )
        },
    )

    val simplified: ClosedSpline<*, EdgeMetadata, KnotMetadata>
        get() {
            val segments = segments.withNext(
                outerRight = rightEdgeNode,
            ).map { (segment, nextNode) ->
                segment.simplify(
                    endKnot = nextNode.frontKnot,
                )
            }

            return ClosedSpline<SegmentCurve<*>, EdgeMetadata, KnotMetadata>(
                segments = segments,
            )
        }

    val knotChunks: List<KnotChunk<CurveT, EdgeMetadata, KnotMetadata>> =
        segments.withPrevious(outerLeft = lastSegment).map { (prevSegment, segment) ->
            KnotChunk(
                prevEdgeMetadata = prevSegment.edgeMetadata,
                prevEdge = prevSegment.edge,
                knotMetadata = segment.startKnotMetadata,
                knot = segment.startKnot,
                nextEdgeMetadata = segment.edgeMetadata,
                nextEdge = segment.edge,
            )
        }

    fun dump() = """
        ClosedSpline(
            segments = ${segments.dump()},
        )
    """.trimIndent()
}

val ClosedSpline<*, ClosedSpline.ContourEdgeMetadata, *>.globalOffsetDeviation
    get() = segments.mapNotNull { it.edgeMetadata.offsetDeviation }.max()

fun SVGPathElement.toClosedSpline(): ClosedSpline<*, *, *> {
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
            edgeMetadata = null,
            startKnotMetadata = null,
        )
    }

    return ClosedSpline(
        segments = segments,
    )
}
