package app.geometry.bezier_splines

import app.*
import app.geometry.*
import app.geometry.bezier_curves.*
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
sealed class BezierSpline<SplineT : BezierSpline<SplineT>> {
    abstract class Prototype<SplineT : BezierSpline<SplineT>> {
        abstract fun merge(
            splines: List<OpenSpline>,
        ): SplineT
    }

    sealed interface Link {
        val knot: Point
    }

    data class InnerLink(
        val startKnot: Point,
        val edge: SplineEdge,
    ) : Link {
        companion object {
            fun bezier(
                startKnot: Point,
                control0: Point,
                control1: Point,
            ): InnerLink = InnerLink(
                startKnot = startKnot,
                edge = BezierSplineEdge(
                    startControl = control0,
                    endControl = control1,
                ),
            )
        }

        override val knot: Point
            get() = startKnot
    }

    data class TerminalLink(
        val endKnot: Point,
    ) : Link {
        override val knot: Point
            get() = endKnot
    }

    /**
     * @return A merged spline, or null if no sub-curves could be constructed
     */
    fun mergeOfNonNullOrNull(
        transformCurve: (Curve) -> OpenSpline?,
    ): SplineT? {
        val transformedSplines = subCurves.mapNotNull(transformCurve)

        return when {
            transformedSplines.isEmpty() -> null

            else -> prototype.merge(
                splines = transformedSplines,
            )
        }
    }

    val firstLink: InnerLink
        get() = links.first()

    val lastLink: InnerLink
        get() = links.last()

    abstract val prototype: Prototype<SplineT>

    /**
     * Splines always have at least one node
     */
    abstract val nodes: Iterable<Link>

    abstract val links: Iterable<InnerLink>

    abstract val rightEdgeNode: Link

    /**
     * Splines always have at least one knot
     */
    val knots: Set<Point> by lazy {
        nodes.map { it.knot }.toSet()
    }

    val subCurves: List<Curve> by lazy {
        links.mapWithNext(rightEdge = rightEdgeNode) { innerLink, nextLink ->
            val startKnot = innerLink.startKnot
            val endKnot = nextLink.knot
            val edge = innerLink.edge

            edge.bind(
                startKnot = startKnot,
                endKnot = endKnot,
            )
        }
    }
}


fun BezierSpline<*>.toSvgPath(
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

fun BezierSpline<*>.toControlSvgPath(
    document: SVGDocument,
): SVGPathElement {
    val spline = this

    return document.createPathElement().apply {
        val startKnot = firstLink.startKnot

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

private fun Curve.toSvgPathSeg(
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

private fun Curve.toControlSvgPathSegs(
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
    moveTo(firstLink.startKnot)

    subCurves.forEach { subCurve ->
        pathToControl(subCurve)
    }
}

fun OpenSpline.toPathOpen(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstLink.startKnot)

    subCurves.forEach { subCurve ->
        pathTo(subCurve)
    }
}

fun ClosedSpline.toControlPathClosed(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstLink.startKnot)

    subCurves.forEach { subCurve ->
        pathToControl(subCurve)
    }

    closePath()
}

fun ClosedSpline.toPathClosed(): Path2D.Double = Path2D.Double().apply {
    moveTo(firstLink.startKnot)

    subCurves.forEach { subCurve ->
        pathTo(subCurve)
    }

    closePath()
}

fun Path2D.pathToControl(curve: Curve) {
    when (curve) {
        is CubicBezierCurve -> {
            lineTo(p = curve.control0)
            lineTo(p = curve.control1)
            lineTo(p = curve.end)
        }
    }
}

fun Path2D.pathTo(curve: Curve) {
    when (curve) {
        is CubicBezierCurve -> {
            cubicTo(p1 = curve.control0, p2 = curve.control1, p3 = curve.end)
        }
    }
}

fun BezierSpline<*>.toControlPath(): Path2D.Double = when (this) {
    is ClosedSpline -> toControlPathClosed()
    is OpenSpline -> toControlPathOpen()
}

fun BezierSpline<*>.toPath(): Path2D.Double = when (this) {
    is ClosedSpline -> toPathClosed()
    is OpenSpline -> toPathOpen()
}

fun BezierSpline<*>.drawSpline(
    graphics2D: Graphics2D,
    color: Color = Color.BLACK,
) {
    graphics2D.color = Color.LIGHT_GRAY
    graphics2D.draw(toControlPath())

    graphics2D.color = color
    graphics2D.draw(toPath())
}
