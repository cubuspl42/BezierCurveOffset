package app.geometry.splines

import app.WrappedSvgPathSeg
import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.equalsWithTolerance
import app.asList
import app.utils.elementWiseAs
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve
import app.geometry.transformations.Transformation
import app.utils.uncons
import app.utils.untrail
import app.utils.withNextCyclic
import app.utils.withPrevious
import app.utils.withPreviousCyclic
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg

data class ClosedSpline<
        out CurveT : SegmentCurve<CurveT>,
        out EdgeMetadata,
        out KnotMetadata,
        >(
    /**
     * The cyclic chain of partial links, must not be empty
     */
    val cyclicLinks: List<PartialLink<CurveT, EdgeMetadata, KnotMetadata>>,
) : Spline<CurveT, EdgeMetadata, KnotMetadata>() {
    sealed class ContourEdgeMetadata {
        data object CornerEdge : ContourEdgeMetadata() {
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

    sealed class ContourKnotMetadata {
        data object Corner : ContourKnotMetadata()
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
            splines: List<OpenSpline<*, SegmentCurve.OffsetEdgeMetadata, *>>,
        ): ClosedSpline<*, ContourEdgeMetadata, *> {
            require(splines.isNotEmpty())

            val links = splines.withNextCyclic().flatMap { (spline, nextSpline) ->
                spline.withoutLastKnot.map { link ->
                    link.mapEdgeMetadata { offsetMetadata ->
                        ContourEdgeMetadata.Side(offsetMetadata = offsetMetadata)
                    }
                } + joinSplines(
                    prevSpline = spline,
                    nextSpline = nextSpline,
                )
            }

            return ClosedSpline(
                cyclicLinks = links,
            )
        }

        private fun joinSplines(
            prevSpline: OpenSpline<*, SegmentCurve.OffsetEdgeMetadata, *>,
            nextSpline: OpenSpline<*, SegmentCurve.OffsetEdgeMetadata, *>,
        ): List<PartialLink<LineSegment, ContourEdgeMetadata, *>> {
            val rayIntersectionOrNull = prevSpline.backRay!!.findIntersection(nextSpline.frontRay!!)

            return listOfNotNull(
                PartialLink(
                    startKnot = prevSpline.lastLink.endKnot,
                    edge = Edge(
                        curveEdge = LineSegment.Edge,
                        metadata = ContourEdgeMetadata.CornerEdge,
                    ),
                ),
                rayIntersectionOrNull?.let { rayIntersection ->
                    PartialLink(
                        startKnot = Knot(
                            point = rayIntersection,
                            metadata = ContourKnotMetadata.Corner,
                        ),
                        edge = Edge(
                            curveEdge = LineSegment.Edge,
                            metadata = ContourEdgeMetadata.CornerEdge,
                        ),
                    )
                },
            )
        }
    }

    init {
        require(cyclicLinks.isNotEmpty())
    }

    fun transformVia(
        transformation: Transformation,
    ) = ClosedSpline(
        cyclicLinks = cyclicLinks.map { it.transformVia(transformation) },
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
        val offsetSplines = findOffsetSplines(
            offsetStrategy = offsetStrategy,
        )

        if (offsetSplines.isEmpty()) {
            // TODO: Return a circle?
            return null
        }

        return ClosedSpline.interconnect(
            offsetSplines,
        )
    }

    private fun findOffsetSplines(
        offsetStrategy: ContourOffsetStrategy<EdgeMetadata>,
    ): List<OpenSpline<*, SegmentCurve.OffsetEdgeMetadata, *>> = overlappingLinks.mapNotNull { link ->
        val subCurve = link.curve
        val edgeMetadata = link.edge.metadata

        subCurve.findOffsetSpline(
            params = offsetStrategy.determineOffsetParams(
                edgeMetadata = edgeMetadata,
            ),
        )
    }

    fun <NewKnotMetadata> transformKnotMetadata(
        transform: (Knot<KnotMetadata>) -> NewKnotMetadata,
    ): ClosedSpline<CurveT, EdgeMetadata, NewKnotMetadata> = ClosedSpline(
        cyclicLinks.map { partialLink ->
            partialLink.mapKnotMetadata {
                transform(partialLink.startKnot)
            }
        },
    )

    override val overlappingLinks: List<CompleteLink<CurveT, EdgeMetadata, KnotMetadata>>
        get() = cyclicLinks.withNextCyclic().map { (link, nextLink) ->
            link.complete(nextLink = nextLink)
        }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is ClosedSpline<*, *, *> -> false

        !this.cyclicLinks.equalsWithTolerance(
            other.cyclicLinks,
            tolerance = tolerance,
        ) -> false

        else -> true
    }

    /**
     * Map the sub-curves of this spline to a new type of curve.
     *
     * @param transform A function that transforms the sub-curve to a new
     * sub-curve, preserving the start and the end knot.
     */
    private fun <NewCurveT : SegmentCurve<NewCurveT>> mapSubCurves(
        transform: (CurveT) -> NewCurveT,
    ): ClosedSpline<NewCurveT, EdgeMetadata, KnotMetadata> = ClosedSpline(
        cyclicLinks = overlappingLinks.map { it.mapCurve(transform).withoutEndKnot },
    )

    val simplified: ClosedSpline<*, EdgeMetadata, KnotMetadata>
        get() = mapSubCurves { it.simplified }

    val joints: List<Joint<CurveT, EdgeMetadata, KnotMetadata>>
        get() = cyclicLinks.withPreviousCyclic().map { (prevLink, link) ->
            Joint(
                rearEdge = prevLink.edge,
                innerKnot = link.startKnot,
                frontEdge = link.edge,
            )
        }
}

val ClosedSpline<*, ClosedSpline.ContourEdgeMetadata, *>.globalOffsetDeviation
    get() = cyclicLinks.mapNotNull { it.edge.metadata.offsetDeviation }.max()

fun SVGPathElement.toClosedSpline(): ClosedSpline<*, *, *> {
    val (leadingSvgPathSegs, lastSvgPathSeg) = pathSegList.asList().untrail()!!

    require(lastSvgPathSeg.pathSegType == SVGPathSeg.PATHSEG_CLOSEPATH)

    val pathSegs = leadingSvgPathSegs.map {
        WrappedSvgPathSeg.fromSvgPathSeg(it)
    }

    val (firstPathSeg, trailingPathSegs) = pathSegs.uncons()!!

    val originPathSeg = firstPathSeg as WrappedSvgPathSeg.MoveTo
    val edgePathSegs = trailingPathSegs.elementWiseAs<WrappedSvgPathSeg.CurveTo>()

    val cyclicLinks = edgePathSegs.withPrevious(
        outerLeft = originPathSeg,
    ).map { (prevPathSeg, pathSeg) ->
        val startKnot = prevPathSeg.finalPoint

        Spline.PartialLink(
            startKnot = Spline.Knot(
                point = startKnot,
                metadata = null,
            ),
            edge = pathSeg.toSplineEdge(startKnot),
        )
    }

    return ClosedSpline(
        cyclicLinks = cyclicLinks,
    )
}
