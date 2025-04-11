package app

import app.PatternOutline.PatternOutlineParams
import app.PatternOutline.PatternOutlineParams.EdgeHandle
import app.PatternOutline.PatternOutlineParams.SegmentParams
import app.PatternSvg.mmToPtFactor
import app.geometry.transformations.MixedTransformation
import app.geometry.curves.SegmentCurve
import app.geometry.splines.*
import app.geometry.transformations.Scaling
import app.geometry.transformations.transformation
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.w3c.dom.Element
import org.w3c.dom.svg.*
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.reader

val documentFactory: SAXSVGDocumentFactory = SAXSVGDocumentFactory(null)

fun extractSplineFromElement(
    transformation: MixedTransformation,
    element: Element,
): ClosedSpline<*, *, *> = when (val singleChild = element.childElements.single()) {
    is SVGPathElement -> singleChild.toClosedSpline().transformVia(
        transformation = transformation
    )

    is SVGGElement -> {
        val newTransformation = singleChild.transformation.applyOver(base = transformation)

        extractSplineFromElement(
            transformation = newTransformation, element = singleChild
        )
    }

    else -> throw UnsupportedOperationException("Unsupported child element: $singleChild")
}

fun extractSplineFromFile(
    filePath: Path,
): ClosedSpline<*, *, *> {
    val reader = filePath.reader()
    val uri = "file://Bezier.svg"

    val document = documentFactory.createDocument(uri, reader) as SVGDocument
    val svgElement = document.documentElement as SVGElement
    val pathElement = extractSplineFromElement(
        transformation = MixedTransformation.identity, element = svgElement
    )

    return pathElement
}

sealed interface SeamAllowanceKind {
    data object None : SeamAllowanceKind {
        override val widthMm = 0.0
    }

    data object Standard : SeamAllowanceKind {
        override val widthMm = 6.0
    }

    data object Tunnel : SeamAllowanceKind {
        override val widthMm = 9.0
    }

    data object Edging : SeamAllowanceKind {
        override val widthMm = 12.0
    }

    val widthMm: Double
}

fun main() {
    val markedSpline = PatternSvg.extractFromFile(
        filePath = Path("/Users/jakub/Temporary/Shape.svg"),
    )

    val patternOutline = PatternOutline.fromMarkedSpline(
        markedSpline = markedSpline,
        params = PatternOutlineParams(
            segmentParamsByEdgeHandle = mapOf(
                EdgeHandle(
                    firstKnotName = "B",
                    secondKnotName = "C",
                ) to SegmentParams(
                    seamAllowanceKind = SeamAllowanceKind.Edging,
                ),
                EdgeHandle(
                    firstKnotName = "C",
                    secondKnotName = "D",
                ) to SegmentParams(
                    seamAllowanceKind = SeamAllowanceKind.None,
                ),

                EdgeHandle(
                    firstKnotName = "D",
                    secondKnotName = "E",
                ) to SegmentParams(
                    seamAllowanceKind = SeamAllowanceKind.Edging,
                ),
                EdgeHandle(
                    firstKnotName = "E",
                    secondKnotName = "F",
                ) to SegmentParams(
                    seamAllowanceKind = SeamAllowanceKind.Tunnel,
                ),
            ),
        ),
    )

    val spline = patternOutline.closedSpline

    println(spline.dump())

    val contourSpline = spline.findContourSpline(
        offsetStrategy = object : ClosedSpline.ContourOffsetStrategy<SeamAllowanceKind>() {
            override fun determineOffsetParams(
                edgeMetadata: SeamAllowanceKind,
            ): SegmentCurve.OffsetSplineParams {
                val seamAllowanceKind = edgeMetadata
                return SegmentCurve.OffsetSplineParams(
                    offset = seamAllowanceKind.widthMm,
                )
            }
        },
    )!!

    exportSplinesPreview(
        spline = spline,
        contourSpline = contourSpline,
    )
}

fun exportSplinesPreview(
    spline: ClosedSpline<*, *, *>,
    contourSpline: ClosedSpline<*, *, *>,
) {
    val contourBoundingBox = contourSpline.findBoundingBox()

    println(contourBoundingBox)

    val widthMm = 297.0
    val heightMm = 210.0

    val exportTransform = Scaling(
        factor = mmToPtFactor,
    )

    val document = createSvgDocument().apply {
        documentSvgElement.apply {
            viewBox = SvgViewBox(
                xMin = 0.0,
                yMin = 0.0,
                width = widthMm * mmToPtFactor,
                height = heightMm * mmToPtFactor,
            )
            width = "${widthMm}mm"
            height = "${heightMm}mm"
        }

        documentSvgElement.appendChild(
            spline.transformVia(exportTransform).toDebugSvgPathGroup(document = this)
        )

        documentSvgElement.appendChild(
            contourSpline.transformVia(exportTransform).toDebugSvgPathGroup(document = this)
        )
    }

    document.writeToFile(
        filePath = Path("/Users/jakub/Temporary/Shape2.svg"),
    )
}
