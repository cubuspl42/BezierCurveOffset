package app.geometry.bezier_splines

import app.geometry.Point
import app.interleave

class OpenSpline(
    /**
     * The path of links, must not be empty
     */
    override val links: List<InnerLink>,
    /**
     * The plug node that terminates the path of links
     */
    val terminalLink: TerminalLink,
) : BezierSpline<OpenSpline>() {
    companion object : Prototype<OpenSpline>() {
        fun ofEdge(
            startKnot: Point,
            edge: SplineEdge,
            endKnot: Point,
        ): OpenSpline = OpenSpline(
            links = listOf(
                InnerLink(
                    startKnot = startKnot,
                    edge = edge,
                ),
            ),
            terminalLink = TerminalLink(
                endKnot = endKnot,
            ),
        )

        fun fastenLinksSmoothly(
            prevSpline: OpenSpline,
            nextSpline: OpenSpline,
        ): Pair<InnerLink, InnerLink> {
            val prevLink = prevSpline.lastLink
            val prevEdge = prevLink.edge as BezierSplineEdge
            val prevPlug = prevSpline.terminalLink

            val nextLink = nextSpline.firstLink
            val nextEdge = nextLink.edge as BezierSplineEdge

            val fixedKnot = Point.midPoint(
                prevPlug.endKnot,
                nextLink.startKnot,
            )

            val firstControl = prevEdge.endControl
            val secondControl = nextEdge.startControl

            val (fixedFirstControl, fixedSecondControl) = Point.makeCollinear(
                a = firstControl,
                b = secondControl,
                base = fixedKnot,
            )

            return Pair(
                prevLink.copy(
                    edge = prevEdge.copy(
                        endControl = fixedFirstControl,
                    ),
                ),
                nextLink.copy(
                    startKnot = fixedKnot,
                    edge = nextEdge.copy(
                        startControl = fixedSecondControl,
                    ),
                ),
            )
        }

        fun fastenSplines(
            splines: List<OpenSpline>,
        ): List<InnerLink> = splines.interleave(
            transform = {
                it.insideLinks
            },
            separate = { prevSpline, nextSpline ->
                fastenLinksSmoothly(
                    prevSpline = prevSpline,
                    nextSpline = nextSpline,
                ).toList()
            },
        ).flatten()

        override fun merge(
            splines: List<OpenSpline>,
        ): OpenSpline {
            require(splines.isNotEmpty())

            if (splines.size == 1) {
                return splines.single()
            }

            val firstSpline = splines.first()
            val lastSpline = splines.last()

            val firstLink = firstSpline.firstLink
            val innerLinks = fastenSplines(splines = splines)
            val lastLink = lastSpline.lastLink

            return OpenSpline(
                links = listOf(firstLink) + innerLinks + lastLink,
                terminalLink = lastSpline.terminalLink,
            )
        }
    }

    init {
        require(links.isNotEmpty())
    }

    val insideLinks: List<InnerLink>
        get() = links.drop(1).dropLast(1)

    fun mergeWith(
        rightSubSplitCurve: OpenSpline,
    ): OpenSpline = OpenSpline.merge(
        splines = listOf(this, rightSubSplitCurve),
    )

    override val prototype = OpenSpline

    override val nodes: List<Link> = links + terminalLink

    override val rightEdgeNode: Link
        get() = terminalLink
}
