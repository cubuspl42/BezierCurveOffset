package app

import app.geometry.Point
import app.geometry.Subline
import app.geometry.Transformation
import app.geometry.bezier_curves.CubicBezierCurve
import app.geometry.bezier_curves.ProperBezierCurve
import app.geometry.bezier_curves.SegmentCurve
import app.geometry.splines.*
import app.geometry.transformation
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.w3c.dom.Element
import org.w3c.dom.svg.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.reader

val documentFactory: SAXSVGDocumentFactory = SAXSVGDocumentFactory(null)

sealed class PathSeg {
    companion object {
        fun fromSvgPathSeg(
            pathSeg: SVGPathSeg,
        ): PathSeg {
            when (pathSeg.pathSegType) {
                SVGPathSeg.PATHSEG_MOVETO_ABS -> {
                    val pathSegMovetoAbs = pathSeg as SVGPathSegMovetoAbs

                    return MoveTo(
                        finalPoint = Point.of(
                            px = pathSegMovetoAbs.x.toDouble(),
                            py = pathSegMovetoAbs.y.toDouble(),
                        )
                    )
                }

                SVGPathSeg.PATHSEG_LINETO_ABS -> {
                    val pathSegLinetoAbs = pathSeg as SVGPathSegLinetoAbs

                    return LineTo(
                        endPoint = Point.of(
                            px = pathSegLinetoAbs.x.toDouble(),
                            py = pathSegLinetoAbs.y.toDouble(),
                        )
                    )
                }

                SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS -> {
                    val pathSegCubicToAbs = pathSeg as SVGPathSegCurvetoCubicAbs

                    return CubicTo(
                        firstControl = Point.of(
                            px = pathSegCubicToAbs.x1.toDouble(),
                            py = pathSegCubicToAbs.y1.toDouble(),
                        ),
                        secondControl = Point.of(
                            px = pathSegCubicToAbs.x2.toDouble(),
                            py = pathSegCubicToAbs.y2.toDouble(),
                        ),
                        endPoint = Point.of(
                            px = pathSegCubicToAbs.x.toDouble(),
                            py = pathSegCubicToAbs.y.toDouble(),
                        ),
                    )
                }

                else -> throw UnsupportedOperationException("Unsupported path segment type: ${pathSeg.pathSegType} (${pathSeg.pathSegTypeAsLetter})")
            }
        }
    }

    sealed class CurveTo : PathSeg() {
        abstract fun toEdge(startKnot: Point): SegmentCurve.Edge<SegmentCurve<*>>

        final override val finalPoint: Point
            get() = endPoint

        abstract val endPoint: Point
    }

    data class MoveTo(
        override val finalPoint: Point,
    ) : PathSeg()

    data class LineTo(
        override val endPoint: Point,
    ) : CurveTo() {
        override fun toEdge(startKnot: Point): SegmentCurve.Edge<SegmentCurve<*>> = Subline.Edge
    }

    data class CubicTo(
        val firstControl: Point,
        val secondControl: Point,
        override val endPoint: Point,
    ) : CurveTo() {
        override fun toEdge(
            startKnot: Point,
        ): SegmentCurve.Edge<SegmentCurve<*>> {
//            require(startKnot.distanceTo(firstControl) > 0.001)

            return CubicBezierCurve.Edge(
                control0 = firstControl,
                control1 = secondControl,
            )
        }
    }

    abstract val finalPoint: Point
}

fun SVGPathElement.toClosedSpline(): ClosedSpline<*> {
    val svgPathSegs = pathSegList.asList()

    require(svgPathSegs.last().pathSegType == SVGPathSeg.PATHSEG_CLOSEPATH)

    val pathSegs = svgPathSegs.dropLast(1).map {
        PathSeg.fromSvgPathSeg(it)
    }

    val (firstPathSeg, tailPathSegs) = pathSegs.uncons()!!

    val originPathSeg = firstPathSeg as PathSeg.MoveTo
    val edgePathSegs = tailPathSegs.elementWiseAs<PathSeg.CurveTo>()

    val segments = edgePathSegs.withPrevious(
        outerLeft = originPathSeg,
    ).map { (prevPathSeg, pathSeg) ->
        val startKnot = prevPathSeg.finalPoint
        Spline.Segment(
            startKnot = startKnot,
            edge = pathSeg.toEdge(startKnot),
        )
    }

    return ClosedSpline(
        segments = segments,
    )
}

fun extractChild(
    transformation: Transformation,
    element: Element,
): ClosedSpline<*> = when (val singleChild = element.childElements.single()) {
    is SVGPathElement -> singleChild.toClosedSpline().transformVia(
        transformation = transformation
    )

    is SVGGElement -> {
        val newTransformation = singleChild.transformation.combineWith(base = transformation)

        extractChild(
            transformation = newTransformation, element = singleChild
        )
    }

    else -> throw UnsupportedOperationException("Unsupported child element: $singleChild")
}

fun extractSplineFromFile(
    filePath: Path,
): ClosedSpline<*> {
    val reader = filePath.reader()
    val uri = "file://Bezier.svg"

    val document = documentFactory.createDocument(uri, reader) as SVGDocument
    val svgElement = document.documentElement as SVGElement
    val pathElement = extractChild(
        transformation = Transformation.identity, element = svgElement
    )

    return pathElement
}

fun main() {
    val spline = extractSplineFromFile(
        filePath = Path("/Users/jakub/Temporary/Shape.svg"),
    )

    println(spline.dump())

    val contourSpline = spline.findContourSpline(
        strategy = ProperBezierCurve.BestFitOffsetStrategy,
        offset = 40.0,
    )!!.contourSpline

    val document = createSvgDocument().apply {
        val svgElement = documentSvgElement

        svgElement.appendChild(
            spline.toControlSvgPath(document = this).apply {
                fill = "none"
                stroke = "lightGray"
            },
        )

        svgElement.appendChild(
            spline.toSvgPathGroup(document = this)
        )

        svgElement.appendChild(
            contourSpline.toSvgPathGroup(document = this)
        )
    }

    document.writeToFile(
        filePath = Path("/Users/jakub/Temporary/Shape2.svg"),
    )
}
