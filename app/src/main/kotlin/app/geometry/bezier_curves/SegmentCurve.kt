package app.geometry.bezier_curves

import app.createPathElement
import app.fill
import app.geometry.Point
import app.geometry.Ray
import app.geometry.Subline
import app.geometry.Transformation
import app.geometry.splines.OpenSpline
import app.geometry.splines.Spline
import app.stroke
import org.w3c.dom.svg.SVGDocument
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
            toSvgPathSeg(pathElement = pathElement)
        )
    }

    fill = "none"
    stroke = strokeColor
}

private fun SegmentCurve<*>.toSvgPathSeg(
    pathElement: SVGPathElement,
): SVGPathSeg = when (this) {
    is Subline -> pathElement.createSVGPathSegLinetoAbs(
        end.x.toFloat(),
        end.y.toFloat(),
    )

    is CubicBezierCurve -> pathElement.createSVGPathSegCurvetoCubicAbs(
        end.x.toFloat(),
        end.y.toFloat(),
        control0.x.toFloat(),
        control0.y.toFloat(),
        control1.x.toFloat(),
        control1.y.toFloat(),
    )

    else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
}

private val SegmentCurve<*>.strokeColor: String
    get() = when (this) {
        is Subline -> "blue"
        is CubicBezierCurve -> "red"
        else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
    }
