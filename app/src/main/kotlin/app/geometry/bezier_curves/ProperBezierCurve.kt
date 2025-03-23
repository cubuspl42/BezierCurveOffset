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

        abstract fun calculateError(): Double
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
            return offsetTimedSeries.bestFitCurve()
        }
    }

    data object NormalOffsetStrategy : OffsetStrategy() {
        override fun approximateOffsetCurve(
            curve: ProperBezierCurve<*>,
            offset: Double,
        ): BezierCurve<*> {
            return curve.moveInNormalDirection(
                distance = offset,
            )


        }
    }

    companion object {
        private const val findOffsetErrorThreshold = 0.0001
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
        val initialError = initialOffsetCurveResult.calculateError()

        if (initialError < findOffsetErrorThreshold) {
            return initialOffsetCurve.toSpline()
        } else {
            val criticalPoints = basisFormula.findInterestingCriticalPoints().criticalPointsXY

            if (criticalPoints.isNotEmpty() && false) { // FIXME
                val initialSplitSpline = splitAtMultiple(criticalPoints)

                return initialSplitSpline.mergeOf { splitCurve ->
                    // FIXME
                    (splitCurve as LongitudinalBezierCurve<*>).findOffsetSplineRecursive(
                        strategy = strategy,
                        offset = offset,
                        subdivisionLevel = 0,
                    )
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
        val error = offsetResult.calculateError()

        return when {
            error < findOffsetErrorThreshold || subdivisionLevel >= findOffsetMaxSubdivisionLevel -> offsetCurve.toSpline()

            else -> subdivideAndFindOffsetSplineRecursive(
                offset = offset,
                subdivisionLevel = subdivisionLevel,
                strategy = strategy,
            )
        }
    }

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
            override fun calculateError(): Double {
                val offsetCurveFunction = findOffsetCurveFunction(offset = offset)

                val samples = offsetCurveFunction.sample(
                    strategy = SamplingStrategy.withSampleCount(
                        sampleCount = approximationRatingSampleCount,
                    ),
                )

                return samples.map { sample ->
                    val t = sample.x

                    val offsetPoint = sample.value
                    val approximatedOffsetPoint = approximatedOffsetCurve.curveFunction.evaluate(t = t)

                    offsetPoint.distanceSquaredTo(approximatedOffsetPoint)
                }.average()
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

    abstract fun moveInNormalDirection(
        distance: Double,
    ): CurveT
}
