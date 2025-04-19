package app.geometry.splines

import app.geometry.Ray
import app.geometry.curves.SegmentCurve
import app.utils.uncons
import app.utils.untrail

abstract class OpenSpline<
        out CurveT : SegmentCurve<CurveT>,
        out EdgeMetadata,
        out KnotMetadata,
        > : Spline<CurveT, EdgeMetadata, KnotMetadata>() {
    companion object {
        fun <CurveT : SegmentCurve<CurveT>, EdgeMetadata, KnotMetadata> of(
            leadingLinks: List<PartialLink<CurveT, EdgeMetadata, KnotMetadata>>,
            lastLink: CompleteLink<CurveT, EdgeMetadata, KnotMetadata>,
        ): OpenSpline<CurveT, EdgeMetadata, KnotMetadata> {
            val (firstLink, innerLinks) = leadingLinks.uncons() ?: return MonoCurveSpline(
                link = lastLink,
            )

            return PolyCurveSpline(
                firstLink = firstLink,
                innerLinks = innerLinks,
                lastLink = lastLink,
            )
        }

        /**
         * Merge a list of splines into a single spline, assuming the end of
         * each spline is at the same point as the start of the next spline.
         */
        fun <CurveT : SegmentCurve<CurveT>, EdgeMetadata, KnotMetadata> merge(
            splines: List<OpenSpline<CurveT, EdgeMetadata, KnotMetadata>>,
        ): OpenSpline<CurveT, EdgeMetadata, KnotMetadata> {
            val (leadingSplines, lastSpline) = splines.untrail()
                ?: throw IllegalArgumentException("Cannot merge empty list of splines")

            val leadingLinks = leadingSplines.flatMap { spline ->
                spline.withoutLastKnot
            } + lastSpline.leadingLinks

            return OpenSpline.of(
                leadingLinks = leadingLinks,
                lastLink = lastSpline.lastLink,
            )
        }
    }

    abstract val leadingLinks: List<PartialLink<CurveT, EdgeMetadata, KnotMetadata>>

    internal abstract val lastLink: CompleteLink<CurveT, EdgeMetadata, KnotMetadata>

    /**
     * Cut the end tip of the last link (the complete link)
     */
    abstract val withoutLastKnot: List<PartialLink<CurveT, EdgeMetadata, KnotMetadata>>

    val frontRay: Ray?
        get() = subCurves.first().frontRay

    val backRay: Ray?
        get() = subCurves.last().backRay
}

fun <CurveT : SegmentCurve<CurveT>, EdgeMetadata, KnotMetadata> OpenSpline<CurveT, EdgeMetadata, KnotMetadata>.mergeWith(
    other: OpenSpline<CurveT, EdgeMetadata, KnotMetadata>,
): OpenSpline<CurveT, EdgeMetadata, KnotMetadata> = OpenSpline.merge(
    splines = listOf(this, other),
)

val <CurveT : SegmentCurve<CurveT>> OpenSpline<CurveT, SegmentCurve.OffsetEdgeMetadata, *>.globalDeviation
    get() = overlappingLinks.maxOf { it.edge.metadata.globalDeviation }
