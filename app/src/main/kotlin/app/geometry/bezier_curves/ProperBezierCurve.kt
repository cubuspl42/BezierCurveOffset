package app.geometry.bezier_curves

import app.algebra.bezier_binomials.*
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.geometry.Point
import app.geometry.bezier_splines.OpenBezierSpline
import app.geometry.bezier_splines.mergeWith

/**
 * A best-effort non-degenerate Bézier of order >= 2, where all the control
 * points are different from each other and from the start/end points.
 *
 * This model allows a specific case that could be considered a degenerate curve,
 * i.e. when the start point, the end point and all the control points are
 * different, but collinear. Mathematically, this is a line segment, but lowering
 * such a curve to a linear Bézier curve is non-trivial. At the tip(s), such a
 * curve has its velocity equal to zero, which causes unfortunate corner cases.
 */
sealed class ProperBezierCurve<CurveT : ProperBezierCurve<CurveT>> : LongitudinalBezierCurve<CurveT>() {
    abstract class OffsetCurveApproximationResult(
        val offsetCurve: BezierCurve<*>,
    ) {
        companion object {
            val approximationRatingSampleCount = 16
        }

        /**
         * @return The calculated deviation
         */
        abstract fun calculateDeviation(): Double
    }

    sealed class OffsetStrategy {
        /**
         * Approximate the offset curve of the given curve using this strategy
         *
         * @return The approximated offset curve, or null if approximating a
         * continuous curve using this strategy wasn't possible because of the
         * missing normal directions. If the [curve] is non-degenerate, it
         * theoretically should always be possible to approximate a continuous
         * offset curve.
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
        ) ?: run {
            // If we couldn't find a single initial offset curve, it means that
            // this curve is degenerate. Splitting at the critical points should
            // fix this.
            return splitAtCriticalPointsAndFindOffsetSplineRecursive(
                strategy = strategy,
                offset = offset,
            )
        }

        val initialOffsetCurve = initialOffsetCurveResult.offsetCurve
        val initialDeviation = initialOffsetCurveResult.calculateDeviation()

        return when {
            // The first tried curve is good enough!
            initialDeviation < findOffsetDeviationThreshold -> initialOffsetCurve.toSpline()

            // We weren't lucky, let's do the actual work. Splitting at the critical
            // points is a good start for subdividing, so let's try that.
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
        ) ?: run {
            // If we couldn't construct a continuous offset curve approximation
            // (this curve appeared degenerate, though theoretically it isn't),
            // we just give up, as there's no reason to think that subdividing
            // will help. It's either impossible, or is an extremal corner case
            // that could occur only for an extremely tiny curve
            return null
        }

        val offsetCurve = offsetResult.offsetCurve
        val deviation = offsetResult.calculateDeviation()

        return when {
            deviation < findOffsetDeviationThreshold || subdivisionLevel >= findOffsetMaxSubdivisionLevel -> offsetCurve.toSpline()

            else -> subdivideAndFindOffsetSplineRecursive(
                offset = offset,
                subdivisionLevel = subdivisionLevel,
                strategy = strategy,
            ) ?: run {
                // If we couldn't find the offset curve by subdividing because
                // the results were too tiny, let's  return the best known one
                // even if it has an unacceptable deviation.
                return offsetCurve.toSpline()
            }
        }
    }

    /**
     * Approximate the offset curve of this curve using the given strategy
     *
     * @return The approximated offset curve, or null if approximating a
     * continuous curve using the given strategy wasn't possible because of missing
     * normal directions. If this curve is non-degenerate it theoretically should
     * always be possible to generate a continuous offset curve.
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
            override fun calculateDeviation(): Double {
                val offsetCurveFunction = findOffsetCurveFunction(offset = offset)

                val samples = offsetCurveFunction.sample(
                    strategy = SamplingStrategy.withSampleCount(
                        sampleCount = approximationRatingSampleCount,
                    ),
                )

                return samples.maxOfOrNull { sample: RealFunction.Sample<Point> ->
                    val t = sample.x

                    val offsetPoint = sample.value
                    val approximatedOffsetPoint = approximatedOffsetCurve.curveFunction.evaluate(t = t)

                    offsetPoint.distanceTo(approximatedOffsetPoint)
                } ?: run {
                    // It's difficult to imagine a case when all samples end up
                    // to be undefined, as theoretically _at most one_ of them
                    // could be undefined (the critical points of a degenerate
                    // curve). As we know that we managed to approximate the
                    // offset curve _somehow_, let's consider that curve good
                    // enough and say that it has no deviation. It's obviously
                    // not true, but no answer is good in this case, which might
                    // even be numerically impossible.

                    // TODO: Figure out if the best-fit strategy shouldn't check for collinear control points
                    return 0.0
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
     * @return The best found offset spline, or null if this curve is too tiny
     * to construct its offset spline
     */
    private fun splitAtCriticalPointsAndFindOffsetSplineRecursive(
        strategy: OffsetStrategy,
        offset: Double,
    ): OpenBezierSpline? {
        val criticalPoints = basisFormula.findInterestingCriticalPoints().criticalPointsXY

        if (criticalPoints.isNotEmpty()) {
            val initialSplitSpline = splitAtMultiple(criticalPoints) ?: run {
                // The curve was too tiny to split
                return null
            }

            val mergedSpline = initialSplitSpline.mergeOfNonNullOrNull { splitCurve: BezierCurve<*> ->
                // After splitting at the critical points, each sub-curves should
                // be theoretically non-degenerate, even if this curve is degenerate.
                // The problem of gluing at the critical point is shifted onto
                // the spline merging, but splines _have to_ support sharp corners
                // on joints. If the given split curve is non-longitudinal (is a
                // point), we know we can't generate an offset spline for it,
                // so we give up for this segment. Again, let the spline
                // merging handle that.
                splitCurve.asLongitudinal?.findOffsetSplineRecursive(
                    strategy = strategy,
                    offset = offset,
                    subdivisionLevel = 0,
                )
            }

            // If the merged spline is null, it means that none of the split curve
            // was even longitudinal, or all of the longitudinal sub-curves were
            // too tiny to construct an offset spline for them
            return mergedSpline
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
     * @return The best found offset spline, or null if this curve is too tiny
     * to construct its offset spline
     */
    private fun subdivideAndFindOffsetSplineRecursive(
        strategy: OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): OpenBezierSpline? {
        val (leftSplitCurve, rightSplitCurve) = splitAtSafe(t = 0.5) ?:  run {
            // If the t-value 0.5 is too close to 0 or 1 to even split the curve,
            // this curve is just too tiny to generate the offset spline for it
            return null
        }

        val nextSubDivisionLevel = subdivisionLevel + 1

        // As this curve is theoretically non-degenerate, each sub-curve of this
        // curve should also be theoretically non-degenerate
        val firstSubSplitCurve = leftSplitCurve.findOffsetSplineRecursive(
            strategy = strategy,
            offset = offset,
            subdivisionLevel = nextSubDivisionLevel,
        ) ?: return null

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
