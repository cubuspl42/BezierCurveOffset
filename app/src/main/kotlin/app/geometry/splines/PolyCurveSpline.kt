package app.geometry.splines

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance
import app.geometry.curves.SegmentCurve
import app.withNext

data class PolyCurveSpline<
        out CurveT : SegmentCurve<CurveT>,
        out EdgeMetadata,
        out KnotMetadata,
        >(
    private val firstLink: PartialLink<CurveT, EdgeMetadata, KnotMetadata>,
    private val innerLinks: List<PartialLink<CurveT, EdgeMetadata, KnotMetadata>>,
    override val lastLink: CompleteLink<CurveT, EdgeMetadata, KnotMetadata>,
) : OpenSpline<CurveT, EdgeMetadata, KnotMetadata>() {
    override val leadingLinks: List<PartialLink<CurveT, EdgeMetadata, KnotMetadata>>
        get() = listOf(firstLink) + innerLinks

    private val secondLink: Link<CurveT, EdgeMetadata, KnotMetadata>
        get() = innerLinks.firstOrNull() ?: lastLink

    override val withoutLastKnot: List<PartialLink<CurveT, EdgeMetadata, KnotMetadata>>
        get() = leadingLinks + lastLink.withoutEndKnot

    override val overlappingLinks: List<CompleteLink<CurveT, EdgeMetadata, KnotMetadata>>
        get() = listOf(
            firstLink.complete(nextLink = secondLink),
        ) + innerLinks.withNext(outerRight = lastLink).map { (innerLink, nextLink) ->
            innerLink.complete(nextLink = nextLink)
        } + lastLink

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double
    ): Boolean = when {
        other !is PolyCurveSpline<*, *, *> -> false

        !firstLink.equalsWithTolerance(
            other.firstLink,
            absoluteTolerance = absoluteTolerance,
        ) -> false

        !innerLinks.equalsWithTolerance(
            other.innerLinks,
            absoluteTolerance = absoluteTolerance,
        ) -> false

        !lastLink.equalsWithTolerance(
            other.lastLink,
            absoluteTolerance = absoluteTolerance,
        ) -> false

        else -> true
    }
}
