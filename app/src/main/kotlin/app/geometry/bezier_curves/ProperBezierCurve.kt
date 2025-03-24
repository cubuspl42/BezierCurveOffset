package app.geometry.bezier_curves

import app.algebra.bezier_binomials.*
import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.geometry.bezier_splines.OpenBezierSpline
import app.geometry.bezier_splines.mergeWith

sealed class ProperBezierCurve<CurveT : ProperBezierCurve<CurveT>> : LongitudinalBezierCurve<CurveT>() {
    abstract class OffsetCurveApproximationResult(
        val offsetCurve: BezierCurve<*>,
    ) {
        companion object {
            val approximationRatingSampleCount = 16
        }

        abstract fun calculateDeviation(): Double
    }

    sealed class OffsetStrategy {
        abstract fun approximateOffsetCurve(
            curve: ProperBezierCurve<*>,
            offset: Double,
        ): BezierCurve<*>
    }

    data object BestFitOffsetStrategy : OffsetStrategy() {
        override fun approximateOffsetCurve(
            curve: ProperBezierCurve<*>,
            offset: Double,
        ): BezierCurve<*> {
            val offsetTimedSeries = curve.findOffsetTimedSeries(offset = offset)

            // The computed timed point series could (should?) be improved using
            // the Hoschek's method. It would likely minimize the number of
            // control points needed to approximate the offset curve, but it's
            // not strictly necessary.

            return offsetTimedSeries.bestFitCurve()
        }
    }

    data object NormalOffsetStrategy : OffsetStrategy() {
        override fun approximateOffsetCurve(
            curve: ProperBezierCurve<*>,
            offset: Double,
        ): BezierCurve<*> = curve.moveInNormalDirection(
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
    ): OpenBezierSpline {
        val initialOffsetCurveResult = findApproximatedOffsetCurve(
            strategy = strategy,
            offset = offset,
        )

        val initialOffsetCurve = initialOffsetCurveResult.offsetCurve
        val initialError = initialOffsetCurveResult.calculateDeviation()

        if (initialError < findOffsetDeviationThreshold) {
            return initialOffsetCurve.toSpline()
        } else {
            val criticalPoints = basisFormula.findInterestingCriticalPoints().criticalPointsXY

            if (criticalPoints.isNotEmpty()) {
                val initialSplitSpline = splitAtMultiple(criticalPoints)

                return initialSplitSpline.reshape { splitCurve ->
                    splitCurve.findOffsetSplineRecursive(
                        strategy = strategy,
                        offset = offset,
                        subdivisionLevel = 0,
                    )
                } ?: run {
                    // A proper Bézier curve shouldn't ever split to an
                    // effectively-singularity spline, but it's not clear if
                    // this might not happen because of numeric errors. Let's
                    // return the initial offset curve then, as further
                    // splitting an effectively-singularity spline is not a
                    // good idea.
                    initialOffsetCurve.toSpline()
                }
            } else {
                return subdivideAndFindOffsetSplineRecursive(
                    strategy = strategy,
                    offset = offset,
                    subdivisionLevel = 0,
                )
            }
        }
    }

    override fun findOffsetSplineRecursive(
        strategy: OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): OpenBezierSpline {
        val offsetResult = findApproximatedOffsetCurve(
            strategy = strategy,
            offset = offset,
        )

        val offsetCurve = offsetResult.offsetCurve
        val error = offsetResult.calculateDeviation()

        return when {
            error < findOffsetDeviationThreshold || subdivisionLevel >= findOffsetMaxSubdivisionLevel -> offsetCurve.toSpline()

            else -> subdivideAndFindOffsetSplineRecursive(
                offset = offset,
                subdivisionLevel = subdivisionLevel,
                strategy = strategy,
            )
        }
    }

    // TODO: Is this always possible? What if velocity = 0 at some t?
    private fun findApproximatedOffsetCurve(
        strategy: OffsetStrategy,
        offset: Double,
    ): OffsetCurveApproximationResult {
        val approximatedOffsetCurve = strategy.approximateOffsetCurve(
            curve = this,
            offset = offset,
        )

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

                return samples.maxOf { sample ->
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

    private fun subdivideAndFindOffsetSplineRecursive(
        strategy: OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): OpenBezierSpline {
        val (leftSplitCurve, rightSplitCurve) = splitAtSafe(t = 0.5)

        val nextSubDivisionLevel = subdivisionLevel + 1

        val firstSubSplitCurve = leftSplitCurve.findOffsetSplineRecursive(
            strategy = strategy,
            offset = offset,
            subdivisionLevel = nextSubDivisionLevel,
        )

        if (rightSplitCurve == null) {
            // This is a crazy numerical corner case when a line splits at t=0.5
            // to a line and a point (or two points...)
            return firstSubSplitCurve
        }

        val secondSubSplitCurve = rightSplitCurve.findOffsetSplineRecursive(
            strategy = strategy,
            offset = offset,
            subdivisionLevel = nextSubDivisionLevel,
        )

        return firstSubSplitCurve.mergeWith(secondSubSplitCurve)
    }

    // TODO: Is this always possible numerically?
    abstract fun moveInNormalDirection(
        distance: Double,
    ): CurveT
}
