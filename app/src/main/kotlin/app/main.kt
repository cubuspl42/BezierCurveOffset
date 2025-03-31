package app

import app.geometry.Point
import app.geometry.bezier_splines.BezierSpline
import app.geometry.bezier_splines.BezierSplineEdge
import app.geometry.bezier_splines.ClosedSpline
import app.geometry.bezier_splines.drawSpline
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGOMPathElement
import org.jfree.svg.SVGGraphics2D
import org.jfree.svg.SVGUtils
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
import org.w3c.dom.svg.SVGPathSegMovetoAbs
import java.awt.BasicStroke
import java.awt.Color
import java.io.File
import java.io.IOException
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
                        finalPoint = Point.of(
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

fun SVGOMPathElement.toSpline(): ClosedSpline {
    val svgPathSegs = pathSegList.asList()

    require(svgPathSegs.last().pathSegType == SVGPathSeg.PATHSEG_CLOSEPATH)

    val pathSegs = svgPathSegs.dropLast(1).map {
        PathSeg.fromSvgPathSeg(it)
    }

    val (firstPathSeg, tailPathSegs) = pathSegs.uncons()!!


    val originPathSeg = firstPathSeg as PathSeg.MoveTo
    val edgePathSegs = tailPathSegs.elementWiseAs<PathSeg.CubicTo>()

    val links = edgePathSegs.withPrevious(
        outerLeft = originPathSeg,
    ).map { (prevPathSeg, pathSeg) ->
        BezierSpline.InnerLink(
            startKnot = prevPathSeg.finalPoint,
            edge = BezierSplineEdge(
                startControl = pathSeg.firstControl,
                endControl = pathSeg.secondControl,
            )
        )
    }

    return ClosedSpline(
        links = links,
    )
}

fun main() {
    try {
        val reader = Path("/Users/jakub/Temporary/Shape.svg").reader()
        val uri = "file://Bezier.svg"

        val document = documentFactory.createDocument(uri, reader)
        val svgElement = document.documentElement as SVGElement

        val pathElement = svgElement.childElements.single() as SVGOMPathElement

        val spline = pathElement.toSpline()

        val width = 100
        val height = width
        val svgGraphics2D = SVGGraphics2D(width.toDouble(), height.toDouble())

        svgGraphics2D.stroke = BasicStroke(1.0f)

        spline.drawSpline(
            graphics2D = svgGraphics2D,
            color = Color.RED,
        )

        val file = File("/Users/jakub/Temporary/Shape2.svg")
        SVGUtils.writeToSVG(file, svgGraphics2D.svgElement)
    } catch (ex: IOException) {
        println(ex)
    }
}
