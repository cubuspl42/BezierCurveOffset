package app.geometry.curves

import app.SVGGElementUtils
import app.createPathElement
import app.fill
import app.geometry.Point
import app.geometry.Ray
import app.geometry.LineSegment
import app.geometry.Transformation
import app.geometry.curves.bezier.BezierCurve
import app.geometry.curves.bezier.BezierCurve.OffsetStrategy
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.curves.bezier.toDebugControlSvgPathGroupCubic
import app.geometry.curves.bezier.toSvgPathSegCubic
import app.geometry.splines.OpenSpline
import app.geometry.splines.Spline
import app.geometry.toSvgPathSegLineSegment
import app.stroke
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg

abstract class SegmentCurve<out CurveT : SegmentCurve<CurveT>> {
    abstract class OffsetSplineApproximationResult<out CurveT : SegmentCurve<CurveT>> {
        companion object {
            fun <CurveT : SegmentCurve<CurveT>> merge(
                subResults: List<OffsetSplineApproximationResult<CurveT>>,
            ): OffsetSplineApproximationResult<CurveT> {
                require(subResults.isNotEmpty())

                return object : OffsetSplineApproximationResult<CurveT>() {
                    override val offsetSpline: OpenSpline<CurveT> by lazy {
                        OpenSpline.merge(
                            splines = subResults.map { it.offsetSpline },
                        )
                    }

                    override val globalDeviation: Double by lazy {
                        subResults.maxOf { it.globalDeviation }
                    }
                }
            }
        }

        abstract val offsetSpline: OpenSpline<CurveT>

        abstract val globalDeviation: Double
    }

    abstract fun findOffsetSpline(
        strategy: BezierCurve.OffsetStrategy,
        offset: Double,
    ): OffsetSplineApproximationResult<CurveT>?

    abstract fun findOffsetSplineRecursive(
        strategy: OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
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

    val segment: Spline.Segment<CurveT>
        get() = Spline.Segment(
            startKnot = start,
            edge = edge,
        )

    abstract val start: Point

    abstract val end: Point

    abstract val edge: Edge<CurveT>

    abstract val frontRay: Ray?

    abstract val backRay: Ray?

    abstract val simplified: SegmentCurve<*>
}

fun <CurveT : SegmentCurve<CurveT>> SegmentCurve.OffsetSplineApproximationResult<CurveT>.mergeWith(
    rightResult: SegmentCurve.OffsetSplineApproximationResult<CurveT>,
): SegmentCurve.OffsetSplineApproximationResult<CurveT> = SegmentCurve.OffsetSplineApproximationResult.merge(
    subResults = listOf(this, rightResult),
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
