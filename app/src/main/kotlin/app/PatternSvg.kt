package app

import app.geometry.Point
import app.geometry.splines.ClosedSpline
import app.geometry.splines.toClosedSpline
import app.geometry.transformations.MixedTransformation
import app.geometry.transformations.Transformation
import app.geometry.transformations.transformation
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGTextElement
import java.nio.file.Path
import kotlin.io.path.reader

data class PatternSvg(
    val splines: Set<ClosedSpline<*, *>>,
    val markers: Set<Marker>,
) {
    data class Marker(
        val position: Point,
        val name: String,
    ) {
        companion object {
            fun fromTextElement(
                transformation: Transformation,
                textElement: SVGTextElement,
            ): Marker {
                val position = Point.of(
                    px = textElement.x.baseVal.getItem(0).value.toDouble(),
                    py = textElement.y.baseVal.getItem(0).value.toDouble(),
                ).transformVia(transformation)

                val name = textElement.textContent

                return Marker(
                    position = position,
                    name = name,
                )
            }
        }
    }

    companion object {
        fun visitElement(
            transformation: MixedTransformation,
            element: Element,
            splines: MutableSet<ClosedSpline<*, *>>,
            markers: MutableSet<Marker>,
        ) {
            when (element) {
                is SVGPathElement -> {
                    splines.add(
                        element.toClosedSpline().transformVia(
                            transformation = transformation
                        ),
                    )
                }

                is SVGTextElement -> {
                    markers.add(
                        Marker.fromTextElement(
                            transformation = transformation,
                            textElement = element,
                        ),
                    )
                }

                is SVGGElement -> {
                    val newTransformation = element.transformation.applyOver(base = transformation)

                    element.childElements.forEach {
                        visitElement(
                            transformation = newTransformation,
                            element = it,
                            splines = splines,
                            markers = markers,
                        )
                    }
                }

                else -> throw UnsupportedOperationException("Unsupported element: $element")
            }
        }

        fun extractFromFile(
            filePath: Path,
        ): PatternSvg {
            val reader = filePath.reader()
            val uri = "file://Pattern.svg"

            val document = documentFactory.createDocument(uri, reader) as SVGDocument
            val svgElement = document.documentElement as SVGElement

            val splines = mutableSetOf<ClosedSpline<*, *>>()
            val markers = mutableSetOf<Marker>()

            svgElement.childElements.forEach {
                visitElement(
                    transformation = MixedTransformation.identity,
                    element = it,
                    splines = splines,
                    markers = markers,
                )
            }

            return PatternSvg(
                splines = splines,
                markers = markers,
            )
        }
    }

    fun getClosestMarker(
        position: Point,
        maxDistance: Double,
    ): Marker? = markers.minByOrNull { marker ->
        marker.position.distanceTo(position)
    }?.takeIf { closestMarker ->
        closestMarker.position.distanceTo(position) < maxDistance
    }
}
