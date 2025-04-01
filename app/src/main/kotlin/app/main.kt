package app

import app.geometry.Point
import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.CubicBezierCurve
import app.geometry.bezier_curves.ProperBezierCurve
import app.geometry.bezier_splines.*
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
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

fun SVGPathElement.toSpline(): ClosedSpline {
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
        Spline.Segment(
            startKnot = prevPathSeg.finalPoint,
            edge = BezierCurve.Edge(
                startControl = pathSeg.firstControl,
                endControl = pathSeg.secondControl,
            )
        )
    }

    return ClosedSpline(
        segments = links,
    )
}

fun extractSplineFromFile(
    filePath: Path,
): ClosedSpline {
    val reader = filePath.reader()
    val uri = "file://Bezier.svg"

    val document = documentFactory.createDocument(uri, reader) as SVGDocument
    val svgElement = document.documentElement as SVGElement

    val pathElement = svgElement.childElements.single() as SVGPathElement

    return pathElement.toSpline()
}

fun main() {
    val spline = extractSplineFromFile(
        filePath = Path("/Users/jakub/Temporary/Shape.svg"),
    )

    val pickedSubCurve = spline.subCurves[3] as CubicBezierCurve

    val pickedOffsetSpline = pickedSubCurve.findOffsetSpline(
        strategy = ProperBezierCurve.BestFitOffsetStrategy,
        offset = 10.0,
    )!!.offsetSpline

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
                stroke = "orange"
            },
        )

        svgElement.appendChild(
            pickedOffsetSpline.toSvgPath(document = this).apply {
                fill = "none"
                stroke = "blue"
            },
        )
    }

    document.writeToFile(
        filePath = Path("/Users/jakub/Temporary/Shape2.svg"),
    )
}
