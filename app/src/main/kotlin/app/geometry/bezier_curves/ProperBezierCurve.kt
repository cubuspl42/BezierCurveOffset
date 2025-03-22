package app.geometry.bezier_curves

import app.algebra.bezier_formulas.*
import app.algebra.bezier_formulas.RealFunction.SamplingStrategy
import app.geometry.bezier_splines.OpenBezierSpline
import app.geometry.bezier_splines.mergeWith
import app.partitionSorted

sealed class ProperBezierCurve<CurveT : ProperBezierCurve<CurveT>> : BezierCurve<CurveT>() {
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

    override fun findOffsetSpline(
        strategy: OffsetStrategy,
        offset: Double,
    ): OpenBezierSpline {
        val initialOffsetCurveResult = approximateOffsetCurve(
            strategy = strategy,
            offset = offset,
        )

        val initialOffsetCurve = initialOffsetCurveResult.offsetCurve
        val initialError = initialOffsetCurveResult.calculateError()

        if (initialError < findOffsetErrorThreshold) {
            return initialOffsetCurve.toSpline()
        } else {
            val criticalPoints = basisFormula.findInterestingCriticalPoints().criticalPoints

            if (criticalPoints.isEmpty()) {
                val initialSplitSpline = splitAtMultiple(criticalPoints)

                return initialSplitSpline.mergeOf { splitCurve ->
                    TODO()
//                    splitCurve.findOffsetSplineOrSubdivide(
//                        strategy = strategy,
//                        offset = offset,
//                        subdivisionLevel = 0,
//                    )
                }
            } else {
                return subdivideAndFindOffsetSpline(
                    strategy = strategy,
                    offset = offset,
                    subdivisionLevel = 0,
                )
            }
        }
    }

    private fun findOffsetSplineOrSubdivide(
        strategy: OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): OpenBezierSpline {
        val offsetResult = approximateOffsetCurve(
            strategy = strategy,
            offset = offset,
        )

        val offsetCurve = offsetResult.offsetCurve
        val error = offsetResult.calculateError()

        return when {
            error < findOffsetErrorThreshold || subdivisionLevel >= findOffsetMaxSubdivisionLevel -> offsetCurve.toSpline()

            else -> subdivideAndFindOffsetSpline(
                offset = offset,
                subdivisionLevel = subdivisionLevel,
                strategy = strategy,
            )
        }
    }

    private fun approximateOffsetCurve(
        strategy: OffsetStrategy, offset: Double
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

    /**
     * Split at the given t-value, ensuring to return at least one proper curve.
     */
    fun splitAtProper(
        t: Double,
    ): Pair<ProperBezierCurve<*>, ProperBezierCurve<*>?> {
        val (leftSplitCurve, rightSplitCurve) = splitAt(t = t)
        val leftProperSplitCurve = leftSplitCurve?.asProper
        val rightProperSplitCurve = rightSplitCurve?.asProper

        return when {
            leftProperSplitCurve != null -> Pair(
                leftProperSplitCurve,
                rightProperSplitCurve,
            )

            rightProperSplitCurve != null -> Pair(
                rightProperSplitCurve,
                null,
            )

            // This is a numerical corner case, maybe even impossible. A linear
            // Bézier curve (a line segment) would need to split to two constant
            // Bézier curves (two points), or something equivalent.
            else -> Pair(this, null)
        }
    }

    final override val asProper: ProperBezierCurve<*>? = this

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

        val (leftSplitCurve, rightSplitCurve) = splitAtProper(
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

//    fun splitAt2(
//        t: Double,
//    ): Pair<ProperBezierCurve<*>, ProperBezierCurve<*>>? {
//        val (leftSplitCurve, rightSplitCurve) = splitAt(t = t)
//        val leftProperSplitCurve = leftSplitCurve.asProper ?: return null
//        val rightProperSplitCurve = rightSplitCurve.asProper ?: return null
//        return Pair(leftProperSplitCurve, rightProperSplitCurve)
//    }

    fun splitAtMidPoint() = splitAt(t = 0.5)

    private fun subdivideAndFindOffsetSpline(
        strategy: OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): OpenBezierSpline {
        val (leftSplitCurve, rightSplitCurve) = splitAtProper(t = 0.5)

        val nextSubDivisionLevel = subdivisionLevel + 1

        val firstSubSplitCurve = leftSplitCurve.findOffsetSplineOrSubdivide(
            strategy = strategy,
            offset = offset,
            subdivisionLevel = nextSubDivisionLevel,
        )

        if (rightSplitCurve == null) {
            // This is a crazy numerical corner case when a line splits at t=0.5
            // to a line and a point (or two points...)
            return firstSubSplitCurve
        }

        val secondSubSplitCurve = rightSplitCurve.findOffsetSplineOrSubdivide(
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