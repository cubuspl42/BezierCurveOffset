package app.geometry.bezier_curves

import app.geometry.bezier_curves.ProperBezierCurve.OffsetStrategy
import app.geometry.splines.OpenSpline
import app.partitionSorted
import java.awt.geom.Path2D

/**
 * A Bézier curve of order >= 1, i.e. a curve of non-zero length (not a point).
 */
sealed class LongitudinalBezierCurve : BezierCurve() {
    final override val asLongitudinal: LongitudinalBezierCurve
        get() = this


    abstract fun toPath2D(): Path2D.Double

    /**
     * Find the offset spline recursively, assuming this curve is theoretically
     * non-degenerate.
     *
     * @return The best found offset spline, or null if this curve is too tiny
     * to construct its offset spline
     */
    abstract fun findOffsetSplineRecursive(
        strategy: OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): ProperBezierCurve.BezierOffsetSplineApproximationResult?
}
