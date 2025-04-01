package app.geometry.bezier_splines

import app.appendAllItems
import app.createPathElement
import app.geometry.Point
import app.geometry.Subline
import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.CubicBezierCurve
import app.geometry.bezier_curves.SegmentCurve
import app.geometry.cubicTo
import app.geometry.lineTo
import app.geometry.moveTo
import app.mapWithNext
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D

/**
 * A Bézier spline, also called "poly-Bézier curve", or "composite Bézier curve"
 * (a spline formed of cubic Bézier curves)
 */
sealed class Spline {
    sealed interface Node {
        /**
         * The "front" knot, i.e. the next knot when looked from the perspective
         * of the previous node.
         */
        val frontKnot: Point
    }

    data class Segment(
        val startKnot: Point,
        val edge: SegmentCurve.Edge,
    ) : Node {
        companion object {
            fun bezier(
                startKnot: Point,
                control0: Point,
                control1: Point,
            ): Segment = Segment(
                startKnot = startKnot,
                edge = BezierCurve.Edge(
                    control0 = control0,
                    control1 = control1,
                ),
            )

            fun subline(
                startKnot: Point,
            ): Segment = Segment(
                startKnot = startKnot,
                edge = Subline.Edge,
            )
        }

        override val frontKnot: Point
            get() = startKnot
    }

    data class Terminator(
        val endKnot: Point,
    ) : Node {
        override val frontKnot: Point
            get() = endKnot
    }

    val firstSegment: Segment
        get() = segments.first()

    val lastSegment: Segment
        get() = segments.last()

    /**
     * Splines always have at least one node
     */
    abstract val nodes: Iterable<Node>

    abstract val segments: Iterable<Segment>

    abstract val rightEdgeNode: Node

    val subCurves: List<SegmentCurve> by lazy {
        segments.mapWithNext(rightEdge = rightEdgeNode) { segment, nextNode ->
            val startKnot = segment.startKnot
            val endKnot = nextNode.frontKnot
            val edge = segment.edge

            edge.bind(
                startKnot = startKnot,
                endKnot = endKnot,
            )
        }
    }
}

fun Spline.toSvgPath(
    document: SVGDocument,
): SVGPathElement {
    val spline = this

    return document.createPathElement().apply {
        val start = subCurves.first().start

        pathSegList.appendItem(
            createSVGPathSegMovetoAbs(
                start.x.toFloat(),
                start.y.toFloat(),
            ),
        )

        subCurves.forEach { subCurve ->
            pathSegList.appendItem(
                subCurve.toSvgPathSeg(
                    pathElement = this,
                ),
            )
        }

        if (spline is ClosedSpline) {
            pathSegList.appendItem(
                createSVGPathSegClosePath(),
            )
        }
    }
}

fun Spline.toControlSvgPath(
    document: SVGDocument,
): SVGPathElement {
    val spline = this

    return document.createPathElement().apply {
        val startKnot = firstSegment.startKnot

        pathSegList.appendItem(
            createSVGPathSegMovetoAbs(
                startKnot.x.toFloat(),
                startKnot.y.toFloat(),
            ),
        )

        subCurves.forEach { subCurve ->
            pathSegList.appendAllItems(
                subCurve.toControlSvgPathSegs(
                    pathElement = this,
                )
            )
        }

        if (spline is ClosedSpline) {
            pathSegList.appendItem(
                createSVGPathSegClosePath(),
            )
        }
    }
}

private fun SegmentCurve.toSvgPathSeg(
    pathElement: SVGPathElement,
): SVGPathSeg = when (this) {
    is CubicBezierCurve -> pathElement.createSVGPathSegCurvetoCubicAbs(
        end.x.toFloat(),
        end.y.toFloat(),
        control0.x.toFloat(),
        control0.y.toFloat(),
        control1.x.toFloat(),
        control1.y.toFloat(),
    )

    else -> throw UnsupportedOperationException()
}

private fun SegmentCurve.toControlSvgPathSegs(
    pathElement: SVGPathElement,
): List<SVGPathSeg> = when (this) {
    is CubicBezierCurve -> listOf(
        pathElement.createSVGPathSegLinetoAbs(
            control0.x.toFloat(),
            control0.y.toFloat(),
        ),
        pathElement.createSVGPathSegLinetoAbs(
            control1.x.toFloat(),
            control1.y.toFloat(),
        ),
        pathElement.createSVGPathSegLinetoAbs(
            end.x.toFloat(),
            end.y.toFloat(),
        ),
    )

    else -> throw UnsupportedOperationException()
}

fun OpenSpline.toControlPathOpen(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstSegment.startKnot)

    subCurves.forEach { subCurve ->
        pathToControl(subCurve)
    }
}

fun OpenSpline.toPathOpen(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstSegment.startKnot)

    subCurves.forEach { subCurve ->
        pathTo(subCurve)
    }
}

fun ClosedSpline.toControlPathClosed(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstSegment.startKnot)

    subCurves.forEach { subCurve ->
        pathToControl(subCurve)
    }

    closePath()
}

fun ClosedSpline.toPathClosed(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstSegment.startKnot)

    subCurves.forEach { subCurve ->
        pathTo(subCurve)
    }

    closePath()
}

fun Path2D.pathToControl(curve: SegmentCurve) {
    when (curve) {
        is CubicBezierCurve -> {
            lineTo(p = curve.control0)
            lineTo(p = curve.control1)
            lineTo(p = curve.end)
        }
    }
}

fun Path2D.pathTo(curve: SegmentCurve) {
    when (curve) {
        is CubicBezierCurve -> {
            cubicTo(p1 = curve.control0, p2 = curve.control1, p3 = curve.end)
        }
    }
}

fun Spline.toControlPath(): Path2D.Double = when (this) {
    is ClosedSpline -> toControlPathClosed()
    is OpenSpline -> toControlPathOpen()
}

fun Spline.toPath(): Path2D.Double = when (this) {
    is ClosedSpline -> toPathClosed()
    is OpenSpline -> toPathOpen()
}

fun Spline.drawSpline(
    graphics2D: Graphics2D,
    color: Color = Color.BLACK,
) {
    graphics2D.color = Color.LIGHT_GRAY
    graphics2D.draw(toControlPath())

    graphics2D.color = color
    graphics2D.draw(toPath())
}
