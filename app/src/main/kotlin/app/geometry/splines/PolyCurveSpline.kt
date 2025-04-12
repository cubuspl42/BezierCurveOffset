package app.geometry.splines

import app.geometry.curves.SegmentCurve

class PolyCurveSpline<
        out CurveT : SegmentCurve<CurveT>,
        out EdgeMetadata,
        out KnotMetadata,
        >(
    /**
     * The path of segments, must have at least one element
     */
    override val innerSegments: List<Segment<CurveT, EdgeMetadata, KnotMetadata>>,
    /**
     * The node that terminates the path of links
     */
    override val terminator: Spline.Terminator<KnotMetadata>,
) : OpenSpline<CurveT, EdgeMetadata, KnotMetadata>() {
    init {
        require(innerSegments.size >= 2) {
            "PolyCurveSpline must have at least two segments"
        }
    }
}
