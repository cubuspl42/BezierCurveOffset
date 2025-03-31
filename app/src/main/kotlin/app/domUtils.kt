package app

import org.apache.batik.anim.dom.SVGDOMImplementation
import org.w3c.dom.*
import org.w3c.dom.css.CSSStyleDeclaration
import org.w3c.dom.svg.*

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

val SVGDocument.documentSvgElement: SVGElement
    get() = documentElement as SVGElement

fun SVGDocument.createSvgElement(qualifiedName: String): Element = createElementNS(
    SVGDOMImplementation.SVG_NAMESPACE_URI,
    qualifiedName,
)

fun SVGDocument.createPathElement(): SVGPathElement = createSvgElement("path") as SVGPathElement

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
