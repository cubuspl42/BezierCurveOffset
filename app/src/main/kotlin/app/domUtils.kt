package app

import org.apache.batik.anim.dom.SVGDOMImplementation
import org.apache.batik.anim.dom.SVGGraphicsElement
import org.w3c.dom.*
import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.svg.*
import java.nio.file.Path
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

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

var SVGElement.width: Int
    get() = getAttribute("width").toInt()
    set(value) {
        setAttribute("width", value.toString())
    }

var SVGElement.height: Int
    get() = getAttribute("height").toInt()
    set(value) {
        setAttribute("height", value.toString())
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

val SVGDocument.documentSvgElement: SVGElement
    get() = documentElement as SVGElement

fun SVGDocument.createSvgElement(qualifiedName: String): Element = createElementNS(
    SVGDOMImplementation.SVG_NAMESPACE_URI,
    qualifiedName,
)

fun SVGDocument.createPathElement(): SVGPathElement = createSvgElement("path") as SVGPathElement

fun SVGDocument.createGElement(): SVGGElement = createSvgElement("g") as SVGGElement

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
