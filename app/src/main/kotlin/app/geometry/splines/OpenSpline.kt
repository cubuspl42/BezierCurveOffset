package app.geometry.splines

import app.geometry.Point
import app.geometry.Ray
import app.geometry.curves.SegmentCurve
import app.mapFirst
import app.withPreviousOrNull

abstract class OpenSpline<
        out CurveT : SegmentCurve<CurveT>,
        out EdgeMetadata,
        out KnotMetadata,
        > : Spline<CurveT, EdgeMetadata, KnotMetadata>() {
    companion object {
        fun <CurveT : SegmentCurve<CurveT>, EdgeMetadata, KnotMetadata> of(
            segments: List<Segment<CurveT, EdgeMetadata, KnotMetadata>>,
            terminator: Terminator<KnotMetadata>,
        ): OpenSpline<CurveT, EdgeMetadata, KnotMetadata> {
            require(segments.isNotEmpty())

            return PolyCurveSpline(
                innerSegments = segments,
                terminator = terminator,
            )
        }

        fun <CurveT : SegmentCurve<CurveT>, EdgeMetadata, KnotMetadata> merge(
            splines: List<OpenSpline<CurveT, EdgeMetadata, KnotMetadata>>,
        ): OpenSpline<CurveT, EdgeMetadata, KnotMetadata> {
            require(splines.isNotEmpty())

            if (splines.size == 1) {
                return splines.single()
            }

            val segments = splines.withPreviousOrNull().flatMap { (prevSpline, spline) ->
                when {
                    prevSpline != null -> spline.segments.mapFirst { firstNode ->
                        val prevSplineEndEndKnot = prevSpline.terminator.endKnot
                        val splineStartKnot = firstNode.startKnot

                        firstNode.copy(
                            startKnot = Point.midPoint(prevSplineEndEndKnot, splineStartKnot),
                        )
                    }

                    else -> spline.segments
                }
            }

            val lastSpline = splines.last()
            val terminalNode = lastSpline.terminator

            return PolyCurveSpline(
                innerSegments = segments,
                terminator = terminalNode,
            )
        }
    }

    final override val segments: List<Segment<CurveT, EdgeMetadata, KnotMetadata>>
        get() = innerSegments

    abstract val innerSegments: List<Segment<CurveT, EdgeMetadata, KnotMetadata>>

    /**
     * The plug node that terminates the path of links
     */
    abstract val terminator: Spline.Terminator<KnotMetadata>

    val frontRay: Ray?
        get() = subCurves.first().frontRay

    val backRay: Ray?
        get() = subCurves.last().backRay

    final override val nodes: List<Node<KnotMetadata>> by lazy {
        segments + terminator
    }

    override val rightEdgeNode: Node<KnotMetadata>
        get() = terminator
}

fun <CurveT : SegmentCurve<CurveT>, EdgeMetadata, KnotMetadata> OpenSpline<CurveT, EdgeMetadata, KnotMetadata>.mergeWith(
    other: OpenSpline<CurveT, EdgeMetadata, KnotMetadata>,
): OpenSpline<CurveT, EdgeMetadata, KnotMetadata> = OpenSpline.merge(
    splines = listOf(this, other),
)

val <CurveT : SegmentCurve<CurveT>> OpenSpline<CurveT, SegmentCurve.OffsetEdgeMetadata, *>.globalDeviation
    get() = segments.maxOf { it.edgeMetadata.globalDeviation }
