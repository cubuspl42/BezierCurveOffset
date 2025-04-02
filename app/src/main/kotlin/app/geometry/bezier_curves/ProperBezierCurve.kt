package app.geometry.bezier_curves

import app.algebra.bezier_binomials.RealFunction
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.algebra.bezier_binomials.findInterestingCriticalPoints
import app.algebra.bezier_binomials.sample
import app.geometry.Point
import app.geometry.splines.OpenSpline

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
sealed class ProperBezierCurve<out CurveT : ProperBezierCurve<CurveT>> : LongitudinalBezierCurve<CurveT>() {
    abstract class OffsetCurveApproximationResult(
        val offsetCurve: BezierCurve<*>,
    ) {
        companion object {
            val approximationRatingSampleCount = 16
        }

        fun toOffsetSplineApproximationResult(): OffsetSplineApproximationResult =
            object : OffsetSplineApproximationResult(
                offsetSpline = offsetCurve.toSpline(),
            ) {
                override val globalDeviation: Double
                    get() = deviation
            }

        /**
         * @return The calculated deviation
         */
        abstract val deviation: Double
    }

    abstract class OffsetSplineApproximationResult(
        val offsetSpline: OpenSpline<CubicBezierCurve>,
    ) {
        companion object {
            fun precise(
                offsetCurve: BezierCurve<*>,
            ): OffsetSplineApproximationResult = object : OffsetSplineApproximationResult(
                offsetSpline = offsetCurve.toSpline(),
            ) {
                override val globalDeviation: Double = 0.0
            }

            fun merge(
                subResults: List<OffsetSplineApproximationResult>,
            ): OffsetSplineApproximationResult {
                require(subResults.isNotEmpty())

                val mergedOffsetSpline = OpenSpline.merge(
                    splines = subResults.map { it.offsetSpline },
                )

                return object : OffsetSplineApproximationResult(
                    offsetSpline = mergedOffsetSpline,
                ) {
                    override val globalDeviation: Double by lazy {
                        subResults.maxOf { it.globalDeviation }
                    }
                }
            }
        }

        fun mergeWith(
            rightResult: OffsetSplineApproximationResult,
        ): OffsetSplineApproximationResult = merge(
            subResults = listOf(this, rightResult),
        )

        /**
         * @return The calculated global deviation
         */
        abstract val globalDeviation: Double
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
            val offsetTimedSeries = curve.findOffsetTimedSeries(offset = offset) ?: return null

            // The computed timed point series could (should?) be improved using
            // the Hoschek's method. It would likely minimize the number of
            // control points needed to approximate the offset curve, but it's
            // not strictly necessary.

            return offsetTimedSeries.bestFitCurve()
        }
    }

    companion object {
        private const val findOffsetDeviationThreshold = 0.1
        private const val findOffsetMaxSubdivisionLevel = 8
    }

    final override fun findOffsetSpline(
        strategy: OffsetStrategy,
        offset: Double,
    ): OffsetSplineApproximationResult? {
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

        return when {
            initialOffsetCurveResult.deviation < findOffsetDeviationThreshold -> {
                // The first tried curve is good enough!
                initialOffsetCurveResult.toOffsetSplineApproximationResult()
            }

            else -> {
                // We weren't lucky, let's do the actual work. Splitting at the critical
                // points is a good start for subdividing, so let's try that.
                splitAtCriticalPointsAndFindOffsetSplineRecursive(
                    strategy = strategy,
                    offset = offset,
                )
            }
        }
    }

    override fun findOffsetSplineRecursive(
        strategy: OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): OffsetSplineApproximationResult? {
        val offsetCurveResult = findApproximatedOffsetCurve(
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

        fun isDeviationAcceptable() = offsetCurveResult.deviation < findOffsetDeviationThreshold
        fun wasSubdivisionLimitReached() = subdivisionLevel >= findOffsetMaxSubdivisionLevel

        return when {
            isDeviationAcceptable() || wasSubdivisionLimitReached() -> {
                // We found a curve that's good enough (or we have to assume it's
                // good enough, because we've subdivided too many times)
                offsetCurveResult.toOffsetSplineApproximationResult()
            }

            else -> subdivideAndFindOffsetSplineRecursive(
                offset = offset,
                subdivisionLevel = subdivisionLevel,
                strategy = strategy,
            ) ?: run {
                // If we couldn't find the offset curve by subdividing because
                // the results were too tiny, let's  return the best known one
                // even if it has an unacceptable deviation.
                return offsetCurveResult.toOffsetSplineApproximationResult()
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
    fun findApproximatedOffsetCurve(
        strategy: OffsetStrategy,
        offset: Double,
    ): OffsetCurveApproximationResult? {
        // TODO: Figure out the handling of tiny curves where the distance
        //   between the control points is very small, but does not trigger
        //   division by zero
//        if (start.distanceTo(end) < 1.0) {
//            throw UnsupportedOperationException()
//        }

        // TODO: Check for collinear control points up front?

        val approximatedOffsetCurve = strategy.approximateOffsetCurve(
            curve = this,
            offset = offset,
        ) ?: return null

        return object : OffsetCurveApproximationResult(
            offsetCurve = approximatedOffsetCurve,
        ) {
            override val deviation: Double by lazy {
                val offsetCurveFunction = findOffsetCurveFunction(offset = offset)

                val samples = offsetCurveFunction.sample(
                    strategy = SamplingStrategy.withSampleCount(
                        sampleCount = approximationRatingSampleCount,
                    ),
                )

                return@lazy samples.maxOfOrNull { sample: RealFunction.Sample<Point> ->
                    val t = sample.x

                    val offsetPoint = sample.value
                    val approximatedOffsetPoint = approximatedOffsetCurve.curveFunction.evaluate(t = t)

                    offsetPoint.distanceTo(approximatedOffsetPoint)
                } ?: run {
                    // It's difficult to imagine a case when all samples end up
                    // to be undefined, as theoretically _at most two of them_
                    // could be undefined (the critical points of a degenerate
                    // curve). We know that we managed to approximate the
                    // offset curve _somehow_...
                    return@lazy Double.POSITIVE_INFINITY
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
    ): OffsetSplineApproximationResult? {
        val criticalPoints = basisFormula.findInterestingCriticalPoints().criticalPointsXY

        if (criticalPoints.isNotEmpty()) {
            val initialSplitSpline = splitAtMultiple(criticalPoints) ?: run {
                // The curve was too tiny to split
                return null
            }

            val subResults = initialSplitSpline.subCurves.mapNotNull { splitCurve ->
                val splitBezierCurve = splitCurve as BezierCurve<*>

                // After splitting at the critical points, each sub-curves should
                // be theoretically non-degenerate, even if this curve is degenerate.
                // The problem of gluing at the critical point is shifted onto
                // the spline merging, but splines _have to_ support sharp corners
                // on joints. If the given split curve is non-longitudinal (is a
                // point), we know we can't generate an offset spline for it,
                // so we give up for this segment. Again, let the spline
                // merging handle that.
                splitBezierCurve.asLongitudinal?.findOffsetSplineRecursive(
                    strategy = strategy,
                    offset = offset,
                    subdivisionLevel = 0,
                )
            }

            if (subResults.isEmpty()) {
                // None of the split curve was even longitudinal, or all of the
                // longitudinal sub-curves were too tiny to construct an offset
                // spline for them
                return null
            }

            return OffsetSplineApproximationResult.merge(
                subResults = subResults
            )

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
    ): OffsetSplineApproximationResult? {
        val (leftSplitCurve, rightSplitCurve) = splitAtSafe(t = 0.5) ?: run {
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
}
