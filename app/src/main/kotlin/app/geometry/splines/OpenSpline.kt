package app.geometry.splines

import app.geometry.Point
import app.geometry.Ray
import app.geometry.curves.SegmentCurve
import app.mapFirst
import app.withPreviousOrNull

class OpenSpline<
        out CurveT : SegmentCurve<CurveT>,
        out EdgeMetadata,
        >(
    /**
     * The path of links, must not be empty
     */
    override val segments: List<Segment<CurveT, EdgeMetadata>>,
    /**
     * The plug node that terminates the path of links
     */
    val terminator: Spline.Terminator,
) : Spline<CurveT, EdgeMetadata>() {
    companion object {
        fun <CurveT : SegmentCurve<CurveT>, EdgeMetadata> merge(
            splines: List<OpenSpline<CurveT, EdgeMetadata>>,
        ): OpenSpline<CurveT, EdgeMetadata> {
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

            return OpenSpline(
                segments = segments,
                terminator = terminalNode,
            )
        }
    }

    init {
        require(segments.isNotEmpty())
    }

    val frontRay: Ray?
        get() = subCurves.first().frontRay

    val backRay: Ray?
        get() = subCurves.last().backRay

    override val nodes: List<Node> = segments + terminator

    override val rightEdgeNode: Node
        get() = terminator

}

fun <CurveT : SegmentCurve<CurveT>, EdgeMetadata> OpenSpline<CurveT, EdgeMetadata>.mergeWith(
    other: OpenSpline<CurveT, EdgeMetadata>,
): OpenSpline<CurveT, EdgeMetadata> = OpenSpline.merge(
    splines = listOf(this, other),
)

val <CurveT : SegmentCurve<CurveT>> OpenSpline<CurveT, SegmentCurve.OffsetEdgeMetadata>.globalDeviation
    get() = segments.maxOf { it.edgeMetadata.globalDeviation }
