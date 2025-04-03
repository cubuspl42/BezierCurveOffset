package app.geometry.bezier_curves

import app.SVGGElementUtils
import app.SVGPathElementUtils
import app.createPathElement
import app.fill
import app.geometry.Point
import app.geometry.Ray
import app.geometry.Subline
import app.geometry.Transformation
import app.geometry.splines.OpenSpline
import app.geometry.splines.Spline
import app.geometry.toSvgPathSegSubline
import app.stroke
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg

abstract class SegmentCurve<out CurveT : SegmentCurve<CurveT>> {
    abstract class OffsetSplineApproximationResult<out CurveT : SegmentCurve<CurveT>> {
        abstract val offsetSpline: OpenSpline<CurveT>

        abstract val globalDeviation: Double
    }

    abstract fun findOffsetSpline(
        strategy: ProperBezierCurve.OffsetStrategy,
        offset: Double,
    ): OffsetSplineApproximationResult<CurveT>?

    fun toSpline(): OpenSpline<CurveT> = OpenSpline(
        segments = listOf(segment),
        terminator = Spline.Terminator(
            endKnot = end,
        ),
    )

    abstract class Edge<out CurveT : SegmentCurve<CurveT>> {
        abstract fun bind(
            startKnot: Point,
            endKnot: Point,
        ): CurveT

        abstract fun dump(): String

        abstract fun transformVia(
            transformation: Transformation,
        ): Edge<CurveT>
    }

    val segment: Spline.Segment<CurveT>
        get() = Spline.Segment(
            startKnot = start,
            edge = edge,
        )

    abstract val start: Point

    abstract val end: Point

    abstract val edge: Edge<CurveT>

    abstract val frontRay: Ray

    abstract val backRay: Ray
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
    is Subline -> this.toSvgPathSegSubline(
        pathElement = pathElement,
    )

    is CubicBezierCurve -> this.toSvgPathSegCubic(
        pathElement = pathElement,
    )

    else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
}

private val SegmentCurve<*>.debugStrokeColor: String
    get() = when (this) {
        is Subline -> "blue"
        is CubicBezierCurve -> "red"
        else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
    }
