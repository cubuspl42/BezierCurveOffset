package app

import app.geometry.Point
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve
import app.geometry.curves.bezier.CubicBezierCurve
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
import org.w3c.dom.svg.SVGPathSegLinetoAbs
import org.w3c.dom.svg.SVGPathSegMovetoAbs

sealed class WrappedSvgPathSeg {
    companion object {
        fun fromSvgPathSeg(
            pathSeg: SVGPathSeg,
        ): WrappedSvgPathSeg {
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

    sealed class CurveTo : WrappedSvgPathSeg() {
        abstract fun toEdge(startKnot: Point): SegmentCurve.Edge<SegmentCurve<*>>

        final override val finalPoint: Point
            get() = endPoint

        abstract val endPoint: Point
    }

    data class MoveTo(
        override val finalPoint: Point,
    ) : WrappedSvgPathSeg()

    data class LineTo(
        override val endPoint: Point,
    ) : CurveTo() {
        override fun toEdge(startKnot: Point): SegmentCurve.Edge<SegmentCurve<*>> = LineSegment.Edge
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
