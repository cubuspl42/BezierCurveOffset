package app.geometry.splines

import app.SVGGElementUtils
import app.algebra.NumericObject
import app.geometry.BoundingBox
import app.geometry.Point
import app.geometry.curves.SegmentCurve
import app.geometry.curves.toDebugSvgPathGroup
import app.geometry.transformations.Transformation
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGGElement

/**
 * A Bézier spline, also called "poly-Bézier curve", or "composite Bézier curve"
 * (a spline formed of cubic Bézier curves)
 */
sealed class Spline<
        out CurveT : SegmentCurve<CurveT>,
        out EdgeMetadata,
        out KnotMetadata,
        > : NumericObject {
    sealed interface Node<out KnotMetadata> {
        /**
         * The "front" knot, i.e. the next knot when looked from the perspective
         * of the previous node.
         */
        val frontKnot: Point

        val frontKnotMetadata: KnotMetadata
    }

    data class Knot<out Metadata>(
        val point: Point,
        val metadata: Metadata,
    ) : NumericObject {
        fun transformVia(
            transformation: Transformation,
        ): Knot<Metadata> = Knot(
            point = point.transformVia(transformation),
            metadata = metadata,
        )

        fun <NewMetadata> mapMetadata(
            transform: (Metadata) -> NewMetadata,
        ): Knot<NewMetadata> = Knot(
            point = point,
            metadata = transform(metadata),
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            absoluteTolerance: Double,
        ): Boolean = when {
            other !is Knot<*> -> false
            !point.equalsWithTolerance(other.point, absoluteTolerance = absoluteTolerance) -> false
            else -> metadata == other.metadata
        }
    }

    data class Edge<out CurveT : SegmentCurve<CurveT>, out Metadata>(
        val curveEdge: SegmentCurve.Edge<CurveT>,
        val metadata: Metadata,
    ) : NumericObject {
        fun transformVia(
            transformation: Transformation,
        ): Edge<CurveT, Metadata> = Edge(
            curveEdge = curveEdge.transformVia(transformation),
            metadata = metadata,
        )

        fun <NewCurveT : SegmentCurve<NewCurveT>> mapCurve(
            startKnot: Point,
            endKnot: Point,
            transform: (CurveT) -> NewCurveT,
        ): Edge<NewCurveT, Metadata> = Edge(
            curveEdge = transform(
                curveEdge.bind(
                    startKnot = startKnot,
                    endKnot = endKnot,
                ),
            ).edge,
            metadata = metadata,
        )

        fun <NewMetadata> mapMetadata(
            transform: (Metadata) -> NewMetadata,
        ): Edge<CurveT, NewMetadata> = Edge(
            curveEdge = curveEdge,
            metadata = transform(metadata),
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            absoluteTolerance: Double,
        ): Boolean = when {
            other !is Edge<*, *> -> false
            !curveEdge.equalsWithTolerance(other.curveEdge, absoluteTolerance = absoluteTolerance) -> false
            else -> metadata == other.metadata
        }
    }

    sealed class Link<
            out CurveT : SegmentCurve<CurveT>,
            out EdgeMetadata,
            out KnotMetadata,
            > : NumericObject {
        abstract val startKnot: Knot<KnotMetadata>
        abstract val edge: Edge<CurveT, EdgeMetadata>
    }

    data class PartialLink<
            out CurveT : SegmentCurve<CurveT>,
            out EdgeMetadata,
            out KnotMetadata,
            >(
        override val startKnot: Knot<KnotMetadata>,
        override val edge: Edge<CurveT, EdgeMetadata>,
    ) : Link<CurveT, EdgeMetadata, KnotMetadata>() {
        fun transformVia(
            transformation: Transformation,
        ): PartialLink<CurveT, EdgeMetadata, KnotMetadata> = PartialLink(
            startKnot = startKnot.transformVia(transformation),
            edge = edge.transformVia(transformation),
        )

        fun <NewCurveT : SegmentCurve<NewCurveT>> mapCurve(
            endKnot: Point,
            transform: (CurveT) -> NewCurveT,
        ): PartialLink<NewCurveT, EdgeMetadata, KnotMetadata> = PartialLink(
            startKnot = startKnot,
            edge = edge.mapCurve(
                startKnot = startKnot.point,
                endKnot = endKnot,
                transform = transform,
            ),
        )

        fun <NewEdgeMetadata> mapEdgeMetadata(
            transform: (EdgeMetadata) -> NewEdgeMetadata,
        ): PartialLink<CurveT, NewEdgeMetadata, KnotMetadata> = PartialLink(
            startKnot = startKnot,
            edge = edge.mapMetadata(transform),
        )

        fun <NewKnotMetadata> mapKnotMetadata(
            transform: (KnotMetadata) -> NewKnotMetadata,
        ): PartialLink<CurveT, EdgeMetadata, NewKnotMetadata> = PartialLink(
            startKnot = startKnot.mapMetadata(transform),
            edge = edge,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            absoluteTolerance: Double
        ): Boolean = when {
            other !is PartialLink<*, *, *> -> false
            !startKnot.equalsWithTolerance(other.startKnot, absoluteTolerance = absoluteTolerance) -> false
            !edge.equalsWithTolerance(other.edge, absoluteTolerance = absoluteTolerance) -> false
            else -> true
        }
    }

    data class CompleteLink<
            out CurveT : SegmentCurve<CurveT>,
            out EdgeMetadata,
            out KnotMetadata,
            >(
        override val startKnot: Knot<KnotMetadata>,
        override val edge: Edge<CurveT, EdgeMetadata>,
        val endKnot: Knot<KnotMetadata>,
    ) : Link<CurveT, EdgeMetadata, KnotMetadata>() {
        val withoutEndKnot: PartialLink<CurveT, EdgeMetadata, KnotMetadata>
            get() = PartialLink(
                startKnot = startKnot,
                edge = edge,
            )

        fun <NewEdgeMetadata> mapEdgeMetadata(
            transform: (EdgeMetadata) -> NewEdgeMetadata,
        ): CompleteLink<CurveT, NewEdgeMetadata, KnotMetadata> = CompleteLink(
            startKnot = startKnot,
            edge = edge.mapMetadata(transform),
            endKnot = endKnot,
        )

        fun <NewCurveT : SegmentCurve<NewCurveT>> mapCurve(
            transform: (CurveT) -> NewCurveT,
        ): CompleteLink<NewCurveT, EdgeMetadata, KnotMetadata> = CompleteLink(
            startKnot = startKnot,
            edge = edge.mapCurve(
                startKnot = startKnot.point,
                endKnot = endKnot.point,
                transform = transform,
            ),
            endKnot = endKnot,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            absoluteTolerance: Double,
        ): Boolean = when {
            other !is CompleteLink<*, *, *> -> false
            !startKnot.equalsWithTolerance(other.startKnot, absoluteTolerance = absoluteTolerance) -> false
            !edge.equalsWithTolerance(other.edge, absoluteTolerance = absoluteTolerance) -> false
            !endKnot.equalsWithTolerance(other.endKnot, absoluteTolerance = absoluteTolerance) -> false
            else -> true
        }
    }

    data class Joint<
            out CurveT : SegmentCurve<CurveT>,
            out EdgeMetadata,
            out KnotMetadata,
            >(
        val rearEdge: Edge<CurveT, EdgeMetadata>,
        val innerKnot: Knot<KnotMetadata>,
        val frontEdge: Edge<CurveT, EdgeMetadata>,
    )

    fun findBoundingBox(): BoundingBox {
        val boundingBoxes = overlappingLinks.map { it.curve.findBoundingBox() }

        return BoundingBox.unionAll(
            boundingBoxes = boundingBoxes,
        )
    }

    /**
     * The complete links of this spline, overlapping at each knot
     */
    abstract val overlappingLinks: List<CompleteLink<CurveT, EdgeMetadata, KnotMetadata>>

    val subCurves: List<CurveT>
        get() = overlappingLinks.map { it.curve }
}

fun <CurveT : SegmentCurve<CurveT>, EdgeMetadata, KnotMetadata> Spline.PartialLink<CurveT, EdgeMetadata, KnotMetadata>.complete(
    nextLink: Spline.Link<CurveT, EdgeMetadata, KnotMetadata>,
): Spline.CompleteLink<CurveT, EdgeMetadata, KnotMetadata> = Spline.CompleteLink(
    startKnot = startKnot,
    edge = edge,
    endKnot = nextLink.startKnot,
)

val <CurveT : SegmentCurve<CurveT>, EdgeMetadata, KnotMetadata> Spline.CompleteLink<CurveT, EdgeMetadata, KnotMetadata>.curve: CurveT
    get() = edge.curveEdge.bind(
        startKnot = startKnot.point,
        endKnot = endKnot.point,
    )

fun Spline<*, *, *>.toDebugSvgPathGroup(
    document: SVGDocument,
): SVGGElement = SVGGElementUtils.of(
    document = document,
    elements = subCurves.map { subCurve ->
        subCurve.toDebugSvgPathGroup(document = document)
    },
)
