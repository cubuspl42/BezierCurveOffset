package app.geometry.bezier_curves

import app.algebra.bezier_binomials.*
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.geometry.Point
import app.geometry.bezier_splines.OpenBezierSpline
import app.geometry.bezier_splines.mergeWith

sealed class ProperBezierCurve<CurveT : ProperBezierCurve<CurveT>> : LongitudinalBezierCurve<CurveT>() {
    abstract class OffsetCurveApproximationResult(
        val offsetCurve: BezierCurve<*>,
    ) {
        companion object {
            val approximationRatingSampleCount = 16
        }

        /**
         * @return The calculated deviation, or null if the deviation couldn't
         * be calculated because of samples ended up being undefined
         */
        abstract fun calculateDeviation(): Double?
    }

    sealed class OffsetStrategy {
        /**
         * Approximate the offset curve of the given curve using this strategy
         *
         * @return The approximated offset curve, or null if the approximation
         * was using this strategy wasn't possible. If the [curve] is
         * non-degenerate, it theoretically should always be possible to
         * generate the offset curve.
         */
        abstract fun approximateOffsetCurve(
            curve: ProperBezierCurve<*>,
            offset: Double,
        ): BezierCurve<*>?
    }

    data object BestFitOffsetStrategy : OffsetStrategy() {
        override fun approximateOffsetCurve(
            curve: ProperBezierCurve<*>,
            offset: Double,
        ): BezierCurve<*>? {
            val offsetTimedSeries = curve.findOffsetTimedSeries(offset = offset)

            // The computed timed point series could (should?) be improved using
            // the Hoschek's method. It would likely minimize the number of
            // control points needed to approximate the offset curve, but it's
            // not strictly necessary.

            return offsetTimedSeries?.bestFitCurve()
        }
    }

    data object NormalOffsetStrategy : OffsetStrategy() {
        override fun approximateOffsetCurve(
            curve: ProperBezierCurve<*>,
            offset: Double,
        ): BezierCurve<*>? = curve.moveInNormalDirection(
            distance = offset,
        )
    }

    companion object {
        private const val findOffsetDeviationThreshold = 0.01
        private const val findOffsetMaxSubdivisionLevel = 8
    }

    final override fun findOffsetSpline(
        strategy: OffsetStrategy,
        offset: Double,
    ): OpenBezierSpline? {
        val initialOffsetCurveResult = findApproximatedOffsetCurve(
            strategy = strategy,
            offset = offset,
        ) ?: return run {
            // One of the reasons we couldn't find a single initial offset curve
            // could be that this curve is actually a degenerate curve. After
            // splitting at the critical points (which a degenerate curve should
            // theoretically always have), all sub-curves should theoretically be
            // non-degenerate.
            splitAtCriticalPointsAndFindOffsetSplineRecursive(
                strategy = strategy,
                offset = offset,
            )
        }

        val initialOffsetCurve = initialOffsetCurveResult.offsetCurve
        val initialDeviation = initialOffsetCurveResult.calculateDeviation() ?: run {
            // This case would indicate a situation when the curve is (or appeared
            // to be) degenerate during deviation calculation, though the offset
            // approximation strategy happily generated an approximated offset
            // curve. Maybe this is possible in the craziest of the corner cases,
            // but it doesn't seem to be reasonable to even consider trying
            // splitting at the critical points. Just give up.
            return null
        }

        return when {
            initialDeviation < findOffsetDeviationThreshold -> initialOffsetCurve.toSpline()

            else -> splitAtCriticalPointsAndFindOffsetSplineRecursive(
                strategy = strategy,
                offset = offset,
            )
        }
    }

    override fun findOffsetSplineRecursive(
        strategy: OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): OpenBezierSpline? {
        val offsetResult = findApproximatedOffsetCurve(
            strategy = strategy,
            offset = offset,
        ) ?: return null

        val offsetCurve = offsetResult.offsetCurve
        val deviation = offsetResult.calculateDeviation() ?: return null

        return when {
            deviation < findOffsetDeviationThreshold || subdivisionLevel >= findOffsetMaxSubdivisionLevel -> offsetCurve.toSpline()

            else -> subdivideAndFindOffsetSplineRecursive(
                offset = offset,
                subdivisionLevel = subdivisionLevel,
                strategy = strategy,
            )
        }
    }

    /**
     * Approximate the offset curve of this curve using the given strategy
     *
     * @return The approximated offset curve, or null if the approximation
     * using the given strategy wasn't possible. If this curve is non-degenerate,
     * it theoretically should always be possible to generate the offset curve.
     */
    private fun findApproximatedOffsetCurve(
        strategy: OffsetStrategy,
        offset: Double,
    ): OffsetCurveApproximationResult? {
        val approximatedOffsetCurve = strategy.approximateOffsetCurve(
            curve = this,
            offset = offset,
        ) ?: return null

        return object : OffsetCurveApproximationResult(
            offsetCurve = approximatedOffsetCurve,
        ) {
            override fun calculateDeviation(): Double? {
                val offsetCurveFunction = findOffsetCurveFunction(offset = offset)

                val samples = offsetCurveFunction.sample(
                    strategy = SamplingStrategy.withSampleCount(
                        sampleCount = approximationRatingSampleCount,
                    ),
                )

                // It's difficult to imagine a case when all samples end up to be
                // undefined, but even theoretically _some_ of them might be (in
                // the case of a degenerate curve).
                return samples.maxOfOrNull { sample: RealFunction.Sample<Point> ->
                    val t = sample.x

                    val offsetPoint = sample.value
                    val approximatedOffsetPoint = approximatedOffsetCurve.curveFunction.evaluate(t = t)

                    offsetPoint.distanceTo(approximatedOffsetPoint)
                }
            }
        }
    }

    final override val asProper: ProperBezierCurve<*>
        get() = this

    /**
     * Split this curve at its critical points and the offset spline recursively
     * by joining the offset splines of the sub-cures.
     *
     * @return The best found offset spline, or null which would indicate that
     * one of the sub-curves numerically appeared degenerate
     */
    private fun splitAtCriticalPointsAndFindOffsetSplineRecursive(
        strategy: OffsetStrategy,
        offset: Double,
    ): OpenBezierSpline? {
        val criticalPoints = basisFormula.findInterestingCriticalPoints().criticalPointsXY

        if (criticalPoints.isNotEmpty()) {
            // After splitting at the critical points, the sub-curves should be
            // theoretically non-degenerate, even if theis curve is degenerate
            val initialSplitSpline = splitAtMultiple(criticalPoints)

            return initialSplitSpline.reshape { splitCurve ->
                splitCurve.findOffsetSplineRecursive(
                    strategy = strategy,
                    offset = offset,
                    subdivisionLevel = 0,
                )
            }
        } else {
            // If this curve has no critical points, it theoretically shouldn't
            // be degenerate
            return subdivideAndFindOffsetSplineRecursive(
                strategy = strategy,
                offset = offset,
                subdivisionLevel = 0,
            )
        }
    }

    /**
     * Subdivide this curve and the offset spline recursively by joining the
     * offset splines of the subdivided curves, assuming this curve is
     * theoretically non-degenerate.
     *
     * @return The best found offset spline, or null which would indicate that
     * one of the sub-curves numerically appeared degenerate
     */
    private fun subdivideAndFindOffsetSplineRecursive(
        strategy: OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): OpenBezierSpline? {
        val (leftSplitCurve, rightSplitCurve) = splitAtSafe(t = 0.5)

        val nextSubDivisionLevel = subdivisionLevel + 1

        // As this curve is theoretically non-degenerate, each sub-curve of this
        // curve should also be theoretically non-degenerate
        val firstSubSplitCurve = leftSplitCurve.findOffsetSplineRecursive(
            strategy = strategy,
            offset = offset,
            subdivisionLevel = nextSubDivisionLevel,
        ) ?: return null

        if (rightSplitCurve == null) {
            // This is a crazy numerical corner case when a line splits at t=0.5
            // to a line and a point (or two points...)
            return firstSubSplitCurve
        }

        val secondSubSplitCurve = rightSplitCurve.findOffsetSplineRecursive(
            strategy = strategy,
            offset = offset,
            subdivisionLevel = nextSubDivisionLevel,
        ) ?: return null

        return firstSubSplitCurve.mergeWith(secondSubSplitCurve)
    }

    /**
     * Move the curve point-wise in the normal direction
     *
     * @return The moved curve, or null if one of the required normal directions
     * was missing
     */
    abstract fun moveInNormalDirection(
        distance: Double,
    ): CurveT?
}
