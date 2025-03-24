package app.geometry.bezier_curves

import app.geometry.bezier_curves.ProperBezierCurve.OffsetStrategy
import app.geometry.bezier_splines.OpenBezierSpline
import app.partitionSorted
import java.awt.geom.Path2D

/**
 * A Bézier curve of order >= 1, i.e. a curve of non-zero length (not a point).
 */
sealed class LongitudinalBezierCurve<CurveT : LongitudinalBezierCurve<CurveT>> : BezierCurve<CurveT>() {
    abstract fun splitAt(
        t: Double,
    ): Pair<BezierCurve<*>, BezierCurve<*>>

    /**
     * Split this Bézier curve to two curves at the given t-value.
     *
     * Theoretically, a longitudinal Bézier curve should always split to two
     * longitudinal Bézier curves. In a numerical corner case, one of the
     * sub-curves (or both of them) could be a point.
     *
     * @return The two longitudinal sub-curves split at [t], or null if [t] was
     * too close to the edge of the domain (0 or 1)
     */
    fun splitAtSafe(
        t: Double,
    ): Pair<LongitudinalBezierCurve<*>, LongitudinalBezierCurve<*>>? {
        val (leftSplitCurve, rightSplitCurve) = splitAt(t = t)
        val leftSafeSplitCurve = leftSplitCurve.asLongitudinal ?: return null
        val rightSafeSplitCurve = rightSplitCurve.asLongitudinal ?: return null

        return Pair(
            leftSafeSplitCurve,
            rightSafeSplitCurve,
        )
    }

    final override val asLongitudinal: LongitudinalBezierCurve<*>
        get() = this


    /**
     * @param tValues - a set of t-values to split at
     *
     * @return A spline consisting of curves resulting from splitting the curve
     * at the given t-values, or null if the curve was too tiny to split
     */
    fun splitAtMultiple(
        tValues: Set<Double>,
    ): OpenBezierSpline? {
        if (tValues.isEmpty()) {
            return this.toSpline()
        }

        val tValuesSorted = tValues.sorted()

        val spline = splitAtMultipleSorted(
            tValuesSorted = tValuesSorted,
        )

        return spline
    }

    /**
     * @param tValuesSorted - a sorted list of t-values to split at
     *
     * @return A spline consisting of curves resulting from splitting the curve
     * at the given t-values, or null if the curve was too tiny to split
     */
    private fun splitAtMultipleSorted(
        tValuesSorted: List<Double>,
    ): OpenBezierSpline? {
        val partitioningResult =
            tValuesSorted.partitionSorted() ?: return this.toSpline() // We're done, no more places to split

        val leftTValues = partitioningResult.leftPart
        val medianTValue = partitioningResult.medianValue
        val rightTValues = partitioningResult.rightPart

        val (leftSplitCurve, rightSplitCurve) = splitAtSafe(
            t = medianTValue,
        ) ?: return when {
            // We couldn't split at the median t-value, but it's possible that
            // one of the sides contains "reasonable" t-values, though. As the
            // median t-value was extremely close to 0 or 1, we don't even try
            // to correct them.
            medianTValue < 0.5 -> splitAtMultipleSorted(
                tValuesSorted = leftTValues,
            )

            else -> splitAtMultipleSorted(
                tValuesSorted = rightTValues,
            )
        }

        val leftCorrectedTValues = leftTValues.map { it / medianTValue }
        val rightCorrectedTValues = rightTValues.map { (it - medianTValue) / (1.0 - medianTValue) }

        val leftSubSplitCurveOrNull = leftSplitCurve.splitAtMultipleSorted(
            tValuesSorted = leftCorrectedTValues,
        )

        val rightSubSplitCurveOrNull = rightSplitCurve.splitAtMultipleSorted(
            tValuesSorted = rightCorrectedTValues,
        )

        val subSplines = listOfNotNull(
            leftSubSplitCurveOrNull,
            rightSubSplitCurveOrNull,
        )

        if (subSplines.isEmpty()) {
            return null
        }

        return OpenBezierSpline.merge(
            splines = subSplines,
        )
    }

    fun splitAtMidPoint() = splitAt(t = 0.5)

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
    ): OpenBezierSpline?
}
