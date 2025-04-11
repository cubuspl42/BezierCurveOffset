package app.geometry.curves

import app.SVGGElementUtils
import app.algebra.NumericObject
import app.createPathElement
import app.fill
import app.geometry.BoundingBox
import app.geometry.Point
import app.geometry.Ray
import app.geometry.transformations.Transformation
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.curves.bezier.toDebugControlSvgPathGroupCubic
import app.geometry.curves.bezier.toSvgPathSegCubic
import app.geometry.splines.OpenSpline
import app.geometry.splines.Spline
import app.stroke
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg

abstract class SegmentCurve<out CurveT : SegmentCurve<CurveT>> {
    abstract class OffsetEdgeMetadata {
        object Precise : OffsetEdgeMetadata() {
            override val globalDeviation: Double = 0.0
        }

        abstract val globalDeviation: Double
    }

    data class OffsetSplineParams(
        val offset: Double,
    )

    fun findOffsetSpline(
        params: OffsetSplineParams,
    ): OpenSpline<*, OffsetEdgeMetadata, *>? = findOffsetSpline(
        offset = params.offset,
    )

    abstract fun findOffsetSpline(
        offset: Double,
    ): OpenSpline<*, OffsetEdgeMetadata, *>?

    abstract fun findOffsetSplineRecursive(
        offset: Double,
        subdivisionLevel: Int,
    ): OpenSpline<*, OffsetEdgeMetadata, *>?

    fun <EdgeMetadata> toSpline(
        edgeMetadata: EdgeMetadata,
    ): OpenSpline<CurveT, EdgeMetadata, *> = OpenSpline(
        segments = listOf(
            toSegment(edgeMetadata = edgeMetadata),
        ),
        terminator = Spline.Terminator(
            endKnot = end,
        ),
    )

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

    fun <EdgeMetadata> toSegment(
        edgeMetadata: EdgeMetadata,
    ): Spline.Segment<CurveT, EdgeMetadata, *> = Spline.Segment(
        startKnot = start,
        edge = edge,
        edgeMetadata = edgeMetadata,
        knotMetadata = null,
    )

    abstract fun findBoundingBox(): BoundingBox

    abstract val start: Point

    abstract val end: Point

    abstract val edge: Edge<CurveT>

    abstract val frontRay: Ray?

    abstract val backRay: Ray?

    abstract val simplified: SegmentCurve<*>
}

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
