package app

import app.geometry.transformations.MixedTransformation
import app.geometry.curves.SegmentCurve
import app.geometry.splines.*
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
): ClosedSpline<*, *> = when (val singleChild = element.childElements.single()) {
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
): ClosedSpline<*, *> {
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
    data object Small : SeamAllowanceKind {
        override val widthMm = 6.0
    }

    data object Large : SeamAllowanceKind {
        override val widthMm = 12.0
    }

    val widthMm: Double
}

fun main() {
    val patternSvg = PatternSvg.extractFromFile(
        filePath = Path("/Users/jakub/Temporary/Shape.svg"),
    )

    val patternOutline = PatternOutline.fromPatternSvg(
        patternSvg = patternSvg,
    )

//    val spline = patternSvg.splines.single().transformMetadata {
//        SeamAllowanceKind.Small
//    }

    val spline = patternOutline.closedSpline

    println(spline.dump())

    val contourSpline = spline.findContourSpline(
        offsetStrategy = object : ClosedSpline.ContourOffsetStrategy<SeamAllowanceKind>() {
            override fun determineOffsetParams(
                segmentMetadata: SeamAllowanceKind,
            ): SegmentCurve.OffsetSplineParams {
                val seamAllowanceKind = segmentMetadata
                return SegmentCurve.OffsetSplineParams(
                    offset = seamAllowanceKind.widthMm,
                )
            }
        },
    )!!

    val contourBoundingBox = contourSpline.findBoundingBox()

    println(contourBoundingBox)

    val document = createSvgDocument().apply {
        documentSvgElement.apply {
            viewBox = contourBoundingBox.toSvgViewBox()
            width = contourBoundingBox.width.toInt()
            height = contourBoundingBox.height.toInt()
        }

        documentSvgElement.appendChild(
            spline.toDebugSvgPathGroup(document = this)
        )

        documentSvgElement.appendChild(
            contourSpline.toDebugSvgPathGroup(document = this)
        )
    }

    document.writeToFile(
        filePath = Path("/Users/jakub/Temporary/Shape2.svg"),
    )
}
