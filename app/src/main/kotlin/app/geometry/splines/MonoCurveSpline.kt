package app.geometry.splines

import app.geometry.curves.SegmentCurve

class MonoCurveSpline<
        out CurveT : SegmentCurve<CurveT>,
        out EdgeMetadata,
        out KnotMetadata,
        >(
    /**
     * The single curve this spline consists of
     */
    private val curve: CurveT,
    /**
     * The metadata of the start knot
     */
    private val startKnotMetadata: KnotMetadata,
    /**
     * The metadata of the single edge
     */
    private val edgeMetadata: EdgeMetadata,
    /**
     * The metadata of the end knot
     */
    private val endKnotMetadata: KnotMetadata,
) : OpenSpline<CurveT, EdgeMetadata, KnotMetadata>() {
    override val subCurves: List<CurveT>
        get() = listOf(curve)

    override val innerSegments: List<Segment<CurveT, EdgeMetadata, KnotMetadata>> = listOf(
        Segment(
            startKnot = curve.start,
            startKnotMetadata = startKnotMetadata,
            edge = curve.edge,
            edgeMetadata = edgeMetadata,
        )
    )

    override val terminator: Terminator<KnotMetadata> = Terminator(
        endKnot = curve.end,
        endKnotMetadata = endKnotMetadata,
    )
}
