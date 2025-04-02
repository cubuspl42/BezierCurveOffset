package app

import app.geometry.Point
import app.geometry.bezier_curves.ProperBezierCurve
import app.geometry.splines.*
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

                    return MoveTo(
                        finalPoint = Point.of(
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
                        ), secondControl = Point.of(
                            px = pathSegCubicToAbs.x2.toDouble(),
                            py = pathSegCubicToAbs.y2.toDouble(),
                        ), finalPoint = Point.of(
                            px = pathSegCubicToAbs.x.toDouble(),
                            py = pathSegCubicToAbs.y.toDouble(),
                        )
                    )
                }

                else -> throw UnsupportedOperationException("Unsupported path segment type: ${pathSeg.pathSegType} (${pathSeg.pathSegTypeAsLetter})")
            }
        }
    }

    data class MoveTo(
        override val finalPoint: Point,
    ) : PathSeg()

    data class CubicTo(
        val firstControl: Point,
        val secondControl: Point,
        override val finalPoint: Point,
    ) : PathSeg()

    abstract val finalPoint: Point
}

fun SVGPathElement.toSpline(): ClosedSpline<*> {
    val svgPathSegs = pathSegList.asList()

    require(svgPathSegs.last().pathSegType == SVGPathSeg.PATHSEG_CLOSEPATH)

    val pathSegs = svgPathSegs.dropLast(1).map {
        PathSeg.fromSvgPathSeg(it)
    }

    val (firstPathSeg, tailPathSegs) = pathSegs.uncons()!!

    val originPathSeg = firstPathSeg as PathSeg.MoveTo
    val edgePathSegs = tailPathSegs.filterIsInstance<PathSeg.CubicTo>()

    val segments = edgePathSegs.withPrevious(
        outerLeft = originPathSeg,
    ).map { (prevPathSeg, pathSeg) ->
        Spline.Segment.bezier(
            startKnot = prevPathSeg.finalPoint,
            control0 = pathSeg.firstControl,
            control1 = pathSeg.secondControl,
        )
    }

    return ClosedSpline(
        segments = segments,
    )
}

fun extractChild(
    element: Element,
): SVGPathElement = when (val singleChild = element.childElements.single()) {
    is SVGPathElement -> singleChild
    is SVGGElement -> extractChild(singleChild)
    else -> throw UnsupportedOperationException("Unsupported child element: $singleChild")
}

fun extractSplineFromFile(
    filePath: Path,
): ClosedSpline<*> {
    val reader = filePath.reader()
    val uri = "file://Bezier.svg"

    val document = documentFactory.createDocument(uri, reader) as SVGDocument
    val svgElement = document.documentElement as SVGElement
    val pathElement = extractChild(svgElement)

    return pathElement.toSpline()
}

fun main() {
    val spline = extractSplineFromFile(
        filePath = Path("/Users/jakub/Temporary/Shape.svg"),
    )

    println(spline.dump())

    val contourSpline = spline.findContourSpline(
        strategy = ProperBezierCurve.BestFitOffsetStrategy,
        offset = 10.0,
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
            spline.toSvgPath(document = this).apply {
                fill = "none"
                stroke = "red"
            },
        )

        svgElement.appendChild(
            contourSpline.toSvgPath(document = this).apply {
                fill = "none"
                stroke = "lightBlue"
            },
        )
    }

    document.writeToFile(
        filePath = Path("/Users/jakub/Temporary/Shape2.svg"),
    )
}
