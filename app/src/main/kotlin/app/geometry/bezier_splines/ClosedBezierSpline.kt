package app.geometry.bezier_splines

/**
 * A closed Bézier spline, i.e. such that forms a loops
 */
abstract class ClosedBezierSpline : BezierSpline() {
    final override val nodes: List<InnerNode>
        get() = innerNodes
}
