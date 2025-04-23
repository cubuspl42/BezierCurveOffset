package app

import app.geometry.Point
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.w3c.dom.DOMException
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.css.CSSPrimitiveValue
import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.css.RGBColor
import org.w3c.dom.svg.SVGCircleElement
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
import org.w3c.dom.svg.SVGPathSegLinetoAbs
import org.w3c.dom.svg.SVGPathSegList
import org.w3c.dom.svg.SVGPathSegMovetoAbs
import org.w3c.dom.svg.SVGRectElement
import org.w3c.dom.svg.SVGTransform
import org.w3c.dom.svg.SVGTransformList
import java.awt.Color
import java.nio.file.Path
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.math.roundToInt

fun NodeList.asList(): List<Node> = object : AbstractList<Node>() {
    override val size: Int
        get() = length

    override fun get(
        index: Int,
    ): Node = item(index) ?: throw IndexOutOfBoundsException()
}

fun createSvgDocument(): SVGDocument {
    val impl = SVGDOMImplementation.getDOMImplementation()
    val svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
    return impl.createDocument(svgNS, "svg", null) as SVGDocument
}

var SVGElement.width: String
    get() = getAttribute("width")
    set(value) {
        setAttribute("width", value)
    }

var SVGElement.height: String
    get() = getAttribute("height")
    set(value) {
        setAttribute("height", value)
    }


/**
 * The SVG presentational fill attribute and the CSS fill property can be used
 * with the following SVG elements:
 *
 *     <circle>
 *     <ellipse>
 *     <path>
 *     <polygon>
 *     <polyline>
 *     <rect>
 *     <text>
 *     <textPath>
 *     <tref>
 *     <tspan>
 */
var SVGElement.fill: String
    get() = getAttribute("fill")
    set(value) {
        setAttribute("fill", value)
    }

/**
 * You can use this attribute with the following SVG elements:
 *
 *     <circle>
 *     <ellipse>
 *     <line>
 *     <path>
 *     <polygon>
 *     <polyline>
 *     <rect>
 *     <text>
 *     <textPath>
 *     <tref>
 *     <tspan>
 */
var SVGElement.stroke: String
    get() = getAttribute("stroke")
    set(value) {
        setAttribute("stroke", value)
    }

var SVGElement.viewBox: SvgViewBox
    get() = SvgViewBox.fromSvgString(getAttribute("viewBox"))
    set(value) {
        setAttribute("viewBox", value.toSvgString())
    }

data class SvgViewBox(
    val xMin: Double,
    val yMin: Double,
    val width: Double,
    val height: Double,
) {
    companion object {
        fun fromSvgString(
            viewBox: String,
        ): SvgViewBox {
            val parts = viewBox.split(" ")
            require(parts.size == 4) { "Invalid viewBox format" }
            return SvgViewBox(
                xMin = parts[0].toDouble(),
                yMin = parts[1].toDouble(),
                width = parts[2].toDouble(),
                height = parts[3].toDouble(),
            )
        }
    }

    fun toSvgString(): String = "$xMin $yMin $width $height"
}

val SVGDocument.documentSvgElement: SVGElement
    get() = documentElement as SVGElement


fun SVGDocument.createSvgElement(qualifiedName: String): Element = createElementNS(
    SVGDOMImplementation.SVG_NAMESPACE_URI,
    qualifiedName,
)

fun SVGDocument.createPathElement(): SVGPathElement = createSvgElement("path") as SVGPathElement

fun SVGDocument.createGElement(): SVGGElement = createSvgElement("g") as SVGGElement

fun SVGDocument.createRectElement(): SVGRectElement = createSvgElement("rect") as SVGRectElement

fun SVGDocument.createCircleElement(): SVGCircleElement = createSvgElement("circle") as SVGCircleElement

fun SVGPathSegList.asList(): List<SVGPathSeg> = object : AbstractList<SVGPathSeg>() {
    override val size: Int
        get() = numberOfItems

    override fun get(
        index: Int,
    ): SVGPathSeg {
        try {
            return getItem(index)
        } catch (e: DOMException) {
            throw IndexOutOfBoundsException()
        }
    }
}

fun SVGPathSegList.appendAllItems(
    items: Iterable<SVGPathSeg>,
) {
    items.forEach { appendItem(it) }
}

val Element.childElements: List<Element>
    get() = this.childNodes.asList().filterIsInstance<Element>()

fun CSSStyleDeclaration.setProperty(propertyName: String, value: String) {
    setProperty(propertyName, value, "")
}

fun Document.writeToFile(filePath: Path) {
    val transformer = TransformerFactory.newInstance().newTransformer()

    val source = DOMSource(this)
    val result = StreamResult(filePath.toFile())

    transformer.transform(source, result);
}

operator fun SVGTransformList.get(index: Int): SVGTransform = getItem(index)

object SVGGElementUtils {
    fun of(
        document: SVGDocument,
        elements: List<Element>,
    ): SVGGElement = document.createGElement().apply {
        elements.forEach { appendChild(it) }
    }
}

object SVGPathElementUtils {
    fun build(
        document: SVGDocument,
        buildPathSegs: SVGPathElement.() -> List<SVGPathSeg>,
    ): SVGPathElement = document.createPathElement().apply {
        pathSegList.appendAllItems(this.buildPathSegs())
    }
}

val SVGPathSeg.asSVGPathSegMovetoAbs: SVGPathSegMovetoAbs?
    get() = when {
        pathSegType == SVGPathSeg.PATHSEG_MOVETO_ABS -> this as SVGPathSegMovetoAbs
        else -> null
    }

val SVGPathSeg.asSVGPathSegLinetoAbs: SVGPathSegLinetoAbs?
    get() = when {
        pathSegType == SVGPathSeg.PATHSEG_LINETO_ABS -> this as SVGPathSegLinetoAbs
        else -> null
    }

val SVGPathSeg.asSVGPathSegCurvetoCubicAbs: SVGPathSegCurvetoCubicAbs?
    get() = when {
        pathSegType == SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS -> this as SVGPathSegCurvetoCubicAbs
        else -> null
    }

val SVGPathSegMovetoAbs.p: Point
    get() = Point.of(
        x.toDouble(),
        y.toDouble(),
    )

val SVGPathSegLinetoAbs.p: Point
    get() = Point.of(
        x.toDouble(),
        y.toDouble(),
    )

val SVGPathSegCurvetoCubicAbs.p: Point
    get() = Point.of(
        x.toDouble(),
        y.toDouble(),
    )

val SVGPathSegCurvetoCubicAbs.p1: Point
    get() = Point.of(
        x1.toDouble(),
        y1.toDouble(),
    )

val SVGPathSegCurvetoCubicAbs.p2: Point
    get() = Point.of(
        x2.toDouble(),
        y2.toDouble(),
    )

fun CSSPrimitiveValue.getIntNumberValue(): Int = getFloatValue(CSSPrimitiveValue.CSS_NUMBER).roundToInt()

val RGBColor.color: Color
    get() = Color(
        red.getIntNumberValue(),
        green.getIntNumberValue(),
        blue.getIntNumberValue(),
    )
