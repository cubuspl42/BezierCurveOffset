package app.geometry.bezier_curves

import app.geometry.bezier_curves.ProperBezierCurve.OffsetStrategy
import app.geometry.bezier_splines.OpenBezierSpline
import app.geometry.cubicTo
import app.geometry.moveTo
import app.partitionSorted
import java.awt.geom.Path2D

/**
 * A Bézier curve of order >= 1, i.e. a curve of non-zero length (not a point).
 */
sealed class LongitudinalBezierCurve<CurveT : LongitudinalBezierCurve<CurveT>> : BezierCurve<CurveT>() {
    // TODO: Move here?
    abstract override fun splitAt(
        t: Double,
    ): Pair<BezierCurve<*>, BezierCurve<*>>

    /**
     * Split at the given t-value, ensuring to return at least one safe curve
     * (a curve that is actually longitudinal)
     */
    fun splitAtSafe(
        t: Double,
    ): Pair<LongitudinalBezierCurve<*>, LongitudinalBezierCurve<*>?> {
        val (leftSplitCurve, rightSplitCurve) = splitAt(t = t)
        val leftSafeSplitCurve = leftSplitCurve.asLongitudinal
        val rightSafeSplitCurve = rightSplitCurve.asLongitudinal

        return when {
            leftSafeSplitCurve != null -> Pair(
                leftSafeSplitCurve,
                rightSafeSplitCurve,
            )

            rightSafeSplitCurve != null -> Pair(
                rightSafeSplitCurve,
                null,
            )

            // This is a numerical corner case, maybe even impossible. A
            // longitudinal Bézier curve would need to split to two point
            // Bézier curves, which in proper math is not possible
            else -> Pair(this, null)
        }
    }

    final override val asLongitudinal: LongitudinalBezierCurve<*>
        get() = this

    // TODO: Move here?
    final override fun splitAtMultiple(
        tValues: Set<Double>,
    ): OpenBezierSpline {
        if (tValues.isEmpty()) {
            return this.toSpline()
        }

        val tValuesSorted = tValues.sorted()

        val spline = splitAtMultipleSorted(
            tValuesSorted = tValuesSorted,
        )

        return spline
    }

    private fun splitAtMultipleSorted(
        tValuesSorted: List<Double>,
    ): OpenBezierSpline {
        val partitioningResult =
            tValuesSorted.partitionSorted() ?: return this.toSpline() // We're done, no more places to split

        val leftTValues = partitioningResult.leftPart
        val medianTValue = partitioningResult.medianValue
        val rightTValues = partitioningResult.rightPart

        val leftCorrectedTValues = leftTValues.map { it / medianTValue }
        val rightCorrectedTValues = rightTValues.map { (it - medianTValue) / (1.0 - medianTValue) }

        val (leftSplitCurve, rightSplitCurve) = splitAtSafe(
            t = medianTValue,
        )

        val leftSubSplitCurve = leftSplitCurve.splitAtMultipleSorted(
            tValuesSorted = leftCorrectedTValues,
        )

        val rightSubSplitCurveOrNull = rightSplitCurve?.splitAtMultipleSorted(
            tValuesSorted = rightCorrectedTValues,
        )

        return when {
            else -> OpenBezierSpline.merge(
                splines = listOfNotNull(
                    leftSubSplitCurve,
                    rightSubSplitCurveOrNull,
                ),
            )
        }
    }

    fun splitAtMidPoint() = splitAt(t = 0.5)

    abstract fun toPath2D(): Path2D.Double

    /**
     * Find the offset spline recursively, assuming this curve is theoretically
     * non-degenerate.
     *
     * @return The best found offset spline, or null which would indicate that
     * this curve numerically appeared degenerate and some normal directions were
     * missing.
     */
    abstract fun findOffsetSplineRecursive(
        strategy: OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): OpenBezierSpline?
}
