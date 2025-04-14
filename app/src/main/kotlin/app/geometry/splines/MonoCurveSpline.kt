package app.geometry.splines

import app.algebra.NumericObject
import app.geometry.curves.SegmentCurve

class MonoCurveSpline<
        out CurveT : SegmentCurve<CurveT>,
        out EdgeMetadata,
        out KnotMetadata,
        >(
    /**
     * The single complete link this spline consists of
     */
    private val link: CompleteLink<CurveT, EdgeMetadata, KnotMetadata>,
) : OpenSpline<CurveT, EdgeMetadata, KnotMetadata>() {
    override val leadingLinks: List<PartialLink<CurveT, EdgeMetadata, KnotMetadata>>
        get() = emptyList()

    override val lastLink: CompleteLink<CurveT, EdgeMetadata, KnotMetadata>
        get() = link

    override val withoutLastKnot: List<PartialLink<CurveT, EdgeMetadata, KnotMetadata>>
        get() = listOf(link.withoutEndKnot)

    override val overlappingLinks: List<CompleteLink<CurveT, EdgeMetadata, KnotMetadata>>
        get() = listOf(link)

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double,
    ): Boolean = when {
        other !is MonoCurveSpline<*, *, *> -> false

        !link.equalsWithTolerance(
            other.link,
            absoluteTolerance = absoluteTolerance,
        ) -> false

        else -> true
    }
}
