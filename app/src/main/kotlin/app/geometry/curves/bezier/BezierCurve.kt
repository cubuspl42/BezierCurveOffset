package app.geometry.curves.bezier

import app.algebra.bezier_binomials.DifferentiableBezierBinomial
import app.algebra.bezier_binomials.RealFunction
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.algebra.bezier_binomials.findFaster
import app.algebra.bezier_binomials.findInterestingCriticalPoints
import app.algebra.bezier_binomials.sample
import app.algebra.linear.Vector2
import app.geometry.Direction
import app.geometry.Point
import app.geometry.Ray
import app.geometry.TimedPointSeries
import app.geometry.curves.SegmentCurve
import app.geometry.splines.OpenSpline
import app.geometry.splines.mergeWith
import app.partitionSorted

/**
 * A Bézier curve
 */
sealed class BezierCurve : SegmentCurve<CubicBezierCurve>() {
    abstract class OffsetCurveApproximationResult(
        val offsetCurve: BezierCurve,
    ) {
        companion object {
            val approximationRatingSampleCount = 16
        }

        fun toOffsetSplineApproximationResult(): OpenSpline<CubicBezierCurve, OffsetEdgeMetadata> =
            offsetCurve.toSpline(
                edgeMetadata = object : OffsetEdgeMetadata() {
                    override val globalDeviation: Double
                        get() = deviation
                },
            )

        /**
         * @return The calculated deviation
         */
        abstract val deviation: Double
    }

    companion object {
        fun bindRay(
            pointFunction: TimeFunction<Point>,
            vectorFunction: TimeFunction<Direction?>,
        ): TimeFunction<Ray?> = TimeFunction.map2(
            functionA = pointFunction,
            functionB = vectorFunction,
        ) { point, direction ->
            direction?.let {
                Ray.inDirection(
                    point = point,
                    direction = it,
                )
            }
        }

        private const val findOffsetDeviationThreshold = 0.1
        private const val findOffsetMaxSubdivisionLevel = 8
    }

    val curveFunction: TimeFunction<Point> by lazy {
        basisFormula.findFaster().map { it.toPoint() }
    }

    fun findOffsetCurveFunction(
        offset: Double,
    ): TimeFunction<Point?> = normalRayFunction.map { normalRay ->
        normalRay?.startingPoint?.translateInDirection(
            direction = normalRay.direction,
            distance = offset,
        )
    }

    /**
     * The tangent direction function of the curve, based on the curve's
     * velocity. If this curve is degenerate, it might "slow down" at some point
     * to zero, so the tangent direction is non-existent (null). Theoretically,
     * for longitudinal (non-point) curves (even the otherwise degenerate ones),
     * the tangent should always be defined for t=0 and t=1, but even that is
     * difficult to guarantee from the numerical perspective.
     */
    val tangentFunction: TimeFunction<Direction?> by lazy {
        TimeFunction.wrap(basisFormula.findDerivative()).map {
            Direction.of(it)
        }
    }

    val tangentRayFunction: TimeFunction<Ray?> by lazy {
        bindRay(
            pointFunction = curveFunction,
            vectorFunction = tangentFunction,
        )
    }

    /**
     * The normal direction of the curve, i.e. the direction perpendicular to
     * the tangent direction.
     */
    val normalFunction: TimeFunction<Direction?> by lazy {
        tangentFunction.map {
            it?.perpendicular
        }
    }

    val normalRayFunction: TimeFunction<Ray?> by lazy {
        bindRay(
            pointFunction = curveFunction,
            vectorFunction = normalFunction,
        )
    }

    fun findOffsetTimedSeries(
        offset: Double,
    ): TimedPointSeries? {
        val offsetCurveFunction = findOffsetCurveFunction(offset = offset)

        return TimedPointSeries.sample(
            curveFunction = offsetCurveFunction,
            sampleCount = 6,
        )
    }

    final override fun findOffsetSpline(
        offset: Double,
    ): OpenSpline<CubicBezierCurve, OffsetEdgeMetadata>? {
        val initialOffsetCurveResult = findApproximatedOffsetCurve(
            offset = offset,
        ) ?: run {
            // If we couldn't find a single initial offset curve, it means that
            // this curve is degenerate. Splitting at the critical points should
            // fix this.
            return splitAtCriticalPointsAndFindOffsetSplineRecursive(
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
                    offset = offset,
                )
            }
        }
    }

    override fun findOffsetSplineRecursive(
        offset: Double,
        subdivisionLevel: Int,
    ): OpenSpline<CubicBezierCurve, OffsetEdgeMetadata>? {
        val offsetCurveResult = findApproximatedOffsetCurve(
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
        offset: Double,
    ): OffsetCurveApproximationResult? {
        // TODO: Figure out the handling of tiny curves where the distance
        //   between the control points is very small, but does not trigger
        //   division by zero
//        if (start.distanceTo(end) < 1.0) {
//            throw UnsupportedOperationException()
//        }

        // TODO: Check for collinear control points up front?

        val offsetTimedSeries = findOffsetTimedSeries(offset = offset) ?: return null

        // The computed timed point series could (should?) be improved using
        // the Hoschek's method. It would likely minimize the number of
        // control points needed to approximate the offset curve, but it's
        // not strictly necessary.

        val approximatedOffsetCurve = offsetTimedSeries.bestFitCurve() ?: return null

        return object : OffsetCurveApproximationResult(
            offsetCurve = approximatedOffsetCurve,
        ) {
            override val deviation: Double by lazy {
                val offsetCurveFunction = findOffsetCurveFunction(offset = offset)

                val samples = offsetCurveFunction.sample(
                    strategy = SamplingStrategy(
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

    /**
     * Split this curve at its critical points and the offset spline recursively
     * by joining the offset splines of the sub-cures.
     *
     * @return The best found offset spline, or null if this curve is too tiny
     * to construct its offset spline
     */
    private fun splitAtCriticalPointsAndFindOffsetSplineRecursive(
        offset: Double,
    ): OpenSpline<CubicBezierCurve, OffsetEdgeMetadata>? {
        val criticalPoints = basisFormula.findInterestingCriticalPoints().criticalPointsXY

        if (criticalPoints.isNotEmpty()) {
            val initialSplitSubCurves = splitAtMultiple(criticalPoints) ?: run {
                // The curve was too tiny to split
                return null
            }

            val subSplines = initialSplitSubCurves.mapNotNull { splitCurve ->
                // After splitting at the critical points, each sub-curves should
                // be theoretically non-degenerate, even if this curve is degenerate.
                // The problem of gluing at the critical point is shifted onto
                // the spline merging, but splines _have to_ support sharp corners
                // on joints. If the given split curve is non-longitudinal (is a
                // point), we know we can't generate an offset spline for it,
                // so we give up for this segment. Again, let the spline
                // merging handle that.
                splitCurve.findOffsetSplineRecursive(
                    offset = offset,
                    subdivisionLevel = 0,
                )
            }

            if (subSplines.isEmpty()) {
                // None of the split curve was even longitudinal, or all of the
                // longitudinal sub-curves were too tiny to construct an offset
                // spline for them
                return null
            }

            return OpenSpline.merge(
                subSplines
            )

        } else {
            // If this curve has no critical points, it theoretically shouldn't
            // be degenerate
            return subdivideAndFindOffsetSplineRecursive(
                offset = offset,
                subdivisionLevel = 0,
            )
        }
    }

    /**
     * @param tValues - a set of t-values to split at
     *
     * @return A spline consisting of curves resulting from splitting the curve
     * at the given t-values, or null if the curve was too tiny to split
     */
    fun splitAtMultiple(
        tValues: Set<Double>,
    ): List<BezierCurve>? {
        if (tValues.isEmpty()) {
            return listOf(this)
        }

        val tValuesSorted = tValues.sorted()

        return splitAtMultipleSorted(
            tValuesSorted = tValuesSorted,
        )
    }

    /**
     * @param tValuesSorted - a sorted list of t-values to split at
     *
     * @return A spline consisting of curves resulting from splitting the curve
     * at the given t-values, or null if the curve was too tiny to split
     */
    fun splitAtMultipleSorted(
        tValuesSorted: List<Double>,
    ): List<BezierCurve>? {
        val partitioningResult =
            tValuesSorted.partitionSorted() ?: return listOf(this) // We're done, no more places to split

        val leftTValues = partitioningResult.leftPart
        val medianTValue = partitioningResult.medianValue
        val rightTValues = partitioningResult.rightPart

        val (leftSplitCurve, rightSplitCurve) = splitAt(
            t = medianTValue,
        )

        val leftCorrectedTValues = leftTValues.map { it / medianTValue }
        val rightCorrectedTValues = rightTValues.map { (it - medianTValue) / (1.0 - medianTValue) }

        val leftSubSplitCurves = leftSplitCurve.splitAtMultipleSorted(
            tValuesSorted = leftCorrectedTValues,
        ) ?: emptyList()

        val rightSubSplitCurves = rightSplitCurve.splitAtMultipleSorted(
            tValuesSorted = rightCorrectedTValues,
        ) ?: emptyList()

        val subCurves = leftSubSplitCurves + rightSubSplitCurves

        if (subCurves.isEmpty()) {
            return null
        }

        return subCurves
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
        offset: Double,
        subdivisionLevel: Int,
    ): OpenSpline<CubicBezierCurve, OffsetEdgeMetadata>? {
        val (leftSplitCurve, rightSplitCurve) = splitAt(t = 0.5) ?: run {
            // If the t-value 0.5 is too close to 0 or 1 to even split the curve,
            // this curve is just too tiny to generate the offset spline for it
            return null
        }

        val nextSubDivisionLevel = subdivisionLevel + 1

        // As this curve is theoretically non-degenerate, each sub-curve of this
        // curve should also be theoretically non-degenerate
        val firstSubSplitCurve = leftSplitCurve.findOffsetSplineRecursive(
            offset = offset,
            subdivisionLevel = nextSubDivisionLevel,
        ) ?: return null

        val secondSubSplitCurve = rightSplitCurve.findOffsetSplineRecursive(
            offset = offset,
            subdivisionLevel = nextSubDivisionLevel,
        ) ?: return null

        return firstSubSplitCurve.mergeWith(secondSubSplitCurve)
    }

    abstract val basisFormula: DifferentiableBezierBinomial<Vector2<*>>

    abstract fun splitAt(
        t: Double,
    ): Pair<BezierCurve, BezierCurve>
}
