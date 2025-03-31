package app

import org.w3c.dom.DOMException
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegList

fun NodeList.asList(): List<Node> = object : AbstractList<Node>() {
    override val size: Int
        get() = length

    override fun get(
        index: Int,
    ): Node = item(index) ?: throw IndexOutOfBoundsException()
}

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

val Element.childElements: List<Element>
    get() = this.childNodes.asList().filterIsInstance<Element>()
