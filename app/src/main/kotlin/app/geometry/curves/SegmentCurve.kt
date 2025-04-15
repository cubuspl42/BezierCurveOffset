package app.geometry.curves

import app.SVGGElementUtils
import app.algebra.NumericObject
import app.createPathElement
import app.fill
import app.geometry.BoundingBox
import app.geometry.Curve
import app.geometry.Point
import app.geometry.Ray
import app.geometry.transformations.Transformation
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.curves.bezier.toDebugControlSvgPathGroupCubic
import app.geometry.curves.bezier.toSvgPathSegCubic
import app.geometry.splines.MonoCurveSpline
import app.geometry.splines.OpenSpline
import app.geometry.splines.Spline
import app.stroke
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg

abstract class SegmentCurve<out CurveT : SegmentCurve<CurveT>> : Curve() {
    abstract class Edge<out CurveT : SegmentCurve<CurveT>> : NumericObject {
        abstract fun bind(
            startKnot: Point,
            endKnot: Point,
        ): CurveT

        abstract fun dump(): String

        fun simplify(
            startKnot: Point,
            endKnot: Point,
        ): Edge<*> = bind(
            startKnot = startKnot,
            endKnot = endKnot,
        ).simplified.edge

        abstract fun transformVia(
            transformation: Transformation,
        ): Edge<CurveT>
    }

    abstract class OffsetEdgeMetadata {
        object Precise : OffsetEdgeMetadata() {
            override val globalDeviation: Double = 0.0
        }

        abstract val globalDeviation: Double
    }

    data class OffsetSplineParams(
        val offset: Double,
    )

    companion object {
        val segmentTRange = 0.0..1.0

        /**
         * Finds the unique intersection of two lines in 2D space.
         *
         * @return the intersection if it exists, or null if the lines are parallel
         */
        fun findIntersections(
            segmentCurve0: SegmentCurve<*>,
            segmentCurve1: SegmentCurve<*>,
        ): Set<IntersectionDetails> = when {
            segmentCurve0 is LineSegment && segmentCurve1 is LineSegment -> setOfNotNull(
                LineSegment.findIntersection(
                    lineSegment0 = segmentCurve0,
                    lineSegment1 = segmentCurve1,
                ),
            )

            segmentCurve0 is LineSegment && segmentCurve1 is CubicBezierCurve -> CubicBezierCurve.findIntersections(
                lineSegment = segmentCurve0,
                bezierCurve = segmentCurve1,
            )

            segmentCurve0 is CubicBezierCurve && segmentCurve1 is LineSegment -> CubicBezierCurve.findIntersections(
                lineSegment = segmentCurve1,
                bezierCurve = segmentCurve0,
            )

            segmentCurve0 is CubicBezierCurve && segmentCurve1 is CubicBezierCurve -> CubicBezierCurve.findIntersections(
                bezierCurve0 = segmentCurve0,
                bezierCurve1 = segmentCurve1,
            )

            // Shouldn't happen, we try to handle all combinations
            else -> throw AssertionError("Unsupported segment curve pair: $segmentCurve0, $segmentCurve1")
        }
    }

    fun findOffsetSpline(
        params: OffsetSplineParams,
    ): OpenSpline<*, OffsetEdgeMetadata, *>? = findOffsetSpline(
        offset = params.offset,
    )

    /**
     * Evaluates the curve at the given parameter t, which must be in the range [0, 1].
     */
    override fun evaluate(
        t: Double,
    ): Point {
        if (t !in segmentTRange) throw IllegalArgumentException("t must be in [0, 1], was: $t")

        return evaluateSegment(t = t)
    }

    /**
     * Evaluates the curve at the given parameter t, which is guaranteed to be in the range [0, 1].
     */
    abstract fun evaluateSegment(
        t: Double,
    ): Point

    abstract fun findOffsetSpline(
        offset: Double,
    ): OpenSpline<*, OffsetEdgeMetadata, *>?

    abstract fun findOffsetSplineRecursive(
        offset: Double,
        subdivisionLevel: Int,
    ): OpenSpline<*, OffsetEdgeMetadata, *>?

    abstract fun findBoundingBox(): BoundingBox

    abstract val start: Point

    abstract val end: Point

    abstract val edge: Edge<CurveT>

    abstract val frontRay: Ray?

    abstract val backRay: Ray?

    abstract val simplified: SegmentCurve<*>
}

fun <CurveT : SegmentCurve<CurveT>, EdgeMetadata> CurveT.toSpline(
    edgeMetadata: EdgeMetadata,
): MonoCurveSpline<CurveT, EdgeMetadata, *> = MonoCurveSpline(
    link = Spline.CompleteLink(
        startKnot = Spline.Knot(
            point = start,
            metadata = null,
        ),
        edge = Spline.Edge(
            curveEdge = edge,
            metadata = edgeMetadata,
        ),
        endKnot = Spline.Knot(
            point = end,
            metadata = null,
        ),
    ),
)

fun SegmentCurve<*>.toDebugSvgPathGroup(
    document: SVGDocument,
): SVGGElement = SVGGElementUtils.of(
    document = document,
    elements = listOfNotNull(
        toSvgPath(document = document).apply {
            fill = "none"
            stroke = debugStrokeColor
        },
        toDebugControlSvgPathGroup(document = document),
//        frontRay?.toDebugPath(document = document),
//        backRay?.toDebugPath(document = document),
    ),
)

fun SegmentCurve<*>.toSvgPath(
    document: SVGDocument,
): SVGPathElement = document.createPathElement().apply {
    val pathElement = this

    pathSegList.apply {
        appendItem(
            createSVGPathSegMovetoAbs(
                start.x.toFloat(),
                start.y.toFloat(),
            ),
        )

        appendItem(
            toSvgPathSeg(pathElement = pathElement),
        )
    }
}

private fun SegmentCurve<*>.toDebugControlSvgPathGroup(
    document: SVGDocument,
): SVGGElement? = when (this) {
    is CubicBezierCurve -> this.toDebugControlSvgPathGroupCubic(
        document = document,
    )

    else -> null
}

private fun SegmentCurve<*>.toSvgPathSeg(
    pathElement: SVGPathElement,
): SVGPathSeg = when (this) {
    is LineSegment -> this.toSvgPathSegLineSegment(
        pathElement = pathElement,
    )

    is CubicBezierCurve -> this.toSvgPathSegCubic(
        pathElement = pathElement,
    )

    else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
}

private val SegmentCurve<*>.debugStrokeColor: String
    get() = when (this) {
        is LineSegment -> "blue"
        is CubicBezierCurve -> "red"
        else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
    }
