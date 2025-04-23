package app.geometry

import app.SVGGElementUtils
import app.SvgViewBox
import app.WrappedSvgPathSeg
import app.asList
import app.createPathElement
import app.fill
import app.geometry.curves.LineSegment
import app.geometry.curves.SegmentCurve
import app.geometry.curves.bezier.CubicBezierCurve
import app.geometry.splines.ClosedSpline
import app.geometry.splines.Spline
import app.geometry.transformations.MixedTransformation
import app.get
import app.stroke
import app.utils.iterable.elementWiseAs
import app.utils.iterable.uncons
import app.utils.iterable.untrail
import app.utils.iterable.withPrevious
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGMatrix
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
import org.w3c.dom.svg.SVGPathSegLinetoAbs

val svgDomImplementation: SVGDOMImplementation = SVGDOMImplementation.getDOMImplementation() as SVGDOMImplementation

val documentFactory: SAXSVGDocumentFactory = SAXSVGDocumentFactory(null)

fun BoundingBox.toSvgViewBox(): SvgViewBox = SvgViewBox(
    xMin = xMin,
    yMin = yMin,
    width = width,
    height = height,
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

private val SegmentCurve<*>.debugStrokeColor: String
    get() = when (this) {
        is LineSegment -> "blue"
        is CubicBezierCurve -> "red"
        else -> throw UnsupportedOperationException("Unsupported segment curve: $this")
    }


fun Ray.toDebugPath(
    document: SVGDocument,
): SVGPathElement = LineSegment.of(
    start = startingPoint,
    end = startingPoint.translateInDirection(
        direction = direction,
        distance = 100.0,
    ),
).toSvgPath(
    document = document,
).apply {
    fill = "none"
    stroke = "orange"
}

fun CubicBezierCurve.toDebugControlSvgPathGroupCubic(
    document: SVGDocument,
): SVGGElement = SVGGElementUtils.of(
    document = document,
    elements = listOf(
        lineSegment0.toSvgPath(
            document = document,
        ).apply {
            fill = "none"
            stroke = "darkGray"
        },
//        lineSegment1.toSvgPath(
//            document = document,
//        ).apply {
//            fill = "none"
//            stroke = "lightGray"
//        },
        lineSegment2.toSvgPath(
            document = document,
        ).apply {
            fill = "none"
            stroke = "darkGray"
        },
    ),
)

fun CubicBezierCurve.toSvgPathSegCubic(
    pathElement: SVGPathElement,
): SVGPathSegCurvetoCubicAbs = pathElement.createSVGPathSegCurvetoCubicAbs(
    end.x.toFloat(),
    end.y.toFloat(),
    control0.x.toFloat(),
    control0.y.toFloat(),
    control1.x.toFloat(),
    control1.y.toFloat(),
)

fun SVGPathElement.toClosedSpline(): ClosedSpline<*, *, *> {
    val (leadingSvgPathSegs, lastSvgPathSeg) = pathSegList.asList().untrail()!!

    require(lastSvgPathSeg.pathSegType == SVGPathSeg.PATHSEG_CLOSEPATH)

    val pathSegs = leadingSvgPathSegs.map {
        WrappedSvgPathSeg.fromSvgPathSeg(it)
    }

    val (firstPathSeg, trailingPathSegs) = pathSegs.uncons()!!

    val originPathSeg = firstPathSeg as WrappedSvgPathSeg.MoveTo
    val edgePathSegs = trailingPathSegs.elementWiseAs<WrappedSvgPathSeg.CurveTo>()

    val cyclicLinks = edgePathSegs.withPrevious(
        outerLeft = originPathSeg,
    ).map { (prevPathSeg, pathSeg) ->
        val startKnot = prevPathSeg.finalPoint

        Spline.PartialLink(
            startKnot = Spline.Knot(
                point = startKnot,
                metadata = null,
            ),
            edge = pathSeg.toSplineEdge(startKnot),
        )
    }

    return ClosedSpline(
        cyclicLinks = cyclicLinks,
    )
}

fun Spline<*, *, *>.toDebugSvgPathGroup(
    document: SVGDocument,
): SVGGElement = SVGGElementUtils.of(
    document = document,
    elements = subCurves.map { subCurve ->
        subCurve.toDebugSvgPathGroup(document = document)
    },
)

val SVGGElement.transformation: MixedTransformation
    get() = transform.baseVal[0].matrix.toTransformation()

fun SVGMatrix.toTransformation(): MixedTransformation = MixedTransformation.of(
    a = a.toDouble(),
    b = b.toDouble(),
    c = c.toDouble(),
    d = d.toDouble(),
    e = e.toDouble(),
    f = f.toDouble(),
)

fun SegmentCurve<*>.toSvgPathSeg(
    pathElement: SVGPathElement,
): SVGPathSeg = when (this) {
    is LineSegment -> pathElement.createSVGPathSegLinetoAbs(
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

fun LineSegment.toSvgPathSegLineSegment(
    pathElement: SVGPathElement,
): SVGPathSegLinetoAbs = pathElement.createSVGPathSegLinetoAbs(
    end.x.toFloat(),
    end.y.toFloat(),
)
