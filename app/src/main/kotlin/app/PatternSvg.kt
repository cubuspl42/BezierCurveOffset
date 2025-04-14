package app

import app.geometry.Point
import app.geometry.splines.ClosedSpline
import app.geometry.splines.toClosedSpline
import app.geometry.transformations.Scaling
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

object PatternSvg {
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

    private const val inchToMmFactor = 25.4
    private const val density = 300.0
    private const val ptToMmFactor = inchToMmFactor / density
    const val mmToPtFactor = density / inchToMmFactor

    private fun visitElement(
        transformation: Transformation,
        element: Element,
        splines: MutableSet<ClosedSpline<*, *, *>>,
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
    ): ClosedSpline<*, *, Marker?> {
        val reader = filePath.reader()
        val uri = "file://Pattern.svg"

        val document = documentFactory.createDocument(uri, reader) as SVGDocument
        val svgElement = document.documentElement as SVGElement

        val splines = mutableSetOf<ClosedSpline<*, *, *>>()
        val markers = mutableSetOf<Marker>()

        svgElement.childElements.forEach {
            visitElement(
                transformation = Scaling(
                    factor = ptToMmFactor,
                ),
                element = it,
                splines = splines,
                markers = markers,
            )
        }

        val spline = splines.single()

        fun getClosestMarker(
            position: Point,
            maxDistance: Double,
        ): Marker? = markers.minByOrNull { marker ->
            marker.position.distanceTo(position)
        }?.takeIf { closestMarker ->
            closestMarker.position.distanceTo(position) < maxDistance
        }

        val markedSpline = spline.transformKnotMetadata { knot ->
            getClosestMarker(
                position = knot.point,
                maxDistance = 10.0,
            )
        }

        val nameGrouping = markedSpline.cyclicLinks.groupingBy { it.startKnot.metadata?.name }

        val countByName = nameGrouping.eachCount()

        countByName.forEach { (nameOrNull, count) ->
            val name = nameOrNull ?: return@forEach

            if (count > 1) {
                throw IllegalArgumentException("Multiple segments tagged '$name'")
            }
        }

        return markedSpline
    }
}
