package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_formulas.*
import app.geometry.*
import app.geometry.bezier_splines.*
import app.partitionSorted
import java.awt.geom.Path2D

data class CubicBezierCurve(
    override val start: Point,
    val control0: Point,
    val control1: Point,
    override val end: Point,
) : BezierCurve() {
    companion object {
        private const val bestFitErrorThreshold = 0.001
        private const val bestFitMaxSubdivisionLevel = 4
    }

    override fun isSingularity(): Boolean = setOf(start, control0, control1, end).size == 1

    override val basisFormula = CubicBezierFormula(
        vectorSpace = Vector.VectorVectorSpace,
        weight0 = start.toVector(),
        weight1 = control0.toVector(),
        weight2 = control1.toVector(),
        weight3 = end.toVector(),
    )

    fun mapPointWise(
        transform: (Point) -> Point,
    ): CubicBezierCurve = CubicBezierCurve(
        start = transform(start),
        control0 = transform(control0),
        control1 = transform(control1),
        end = transform(end),
    )

    override fun moveAwayPointWise(
        origin: Point,
        distance: Double,
    ): CubicBezierCurve = mapPointWise {
        it.moveAway(
            origin = origin,
            distance = distance,
        )
    }

    override fun moveInDirectionPointWise(
        direction: Direction,
        distance: Double,
    ): CubicBezierCurve = mapPointWise {
        it.moveInDirection(
            direction = direction,
            distance = distance,
        )
    }

    override fun splitAt(
        t: Double,
    ): BiCubicBezierCurve {
        val skeleton0 = basisFormula.findSkeletonCubic(t = t)
        val skeleton1 = skeleton0.findSkeletonQuadratic(t = t)
        val midPoint = skeleton1.evaluateLinear(t = t).toPoint()

        return BiCubicBezierCurve(
            startNode = CubicBezierSpline.InnerNode.start(
                point = start,
                control1 = skeleton0.point0,
            ),
            midNode = CubicBezierSpline.InnerNode(
                backwardControl = skeleton1.point0,
                point = midPoint,
                forwardControl = skeleton1.point1,
            ),
            endNode = CubicBezierSpline.InnerNode.end(
                control0 = skeleton0.point2,
                point = end,
            ),
        )
    }

    fun splitAtMidPoint(): BiCubicBezierCurve = splitAt(t = 0.5)

    fun splitAtCriticalPoints(): CubicBezierSpline {
        val criticalPoints = basisFormula.findInterestingCriticalPoints().criticalPoints

        val splitSpline = splitAtMultiple(
            tValues = criticalPoints,
        )

        return splitSpline
    }

    fun splitAtMultiple(
        tValues: Set<Double>,
    ): CubicBezierSpline {
        if (tValues.isEmpty()) {
            return this.toSpline()
        }

        val tValuesSorted = tValues.sorted()

        val spline = splitAtMultipleSorted(
            tValuesSorted = tValuesSorted,
        )

        return spline
    }

    private fun toSpline(): CubicBezierSpline = MonoCubicBezierCurve(
        curve = this,
    )

    private fun splitAtMultipleSorted(
        tValuesSorted: List<Double>,
    ): CubicBezierSpline {
        val partitioningResult = tValuesSorted.partitionSorted() ?: return this.toSpline()

        val leftTValues = partitioningResult.leftPart
        val medianTValue = partitioningResult.medianValue
        val rightTValues = partitioningResult.rightPart

        val splitBezierCurve = splitAt(t = medianTValue)
        val leftSplitCurve = splitBezierCurve.firstSubCurve
        val rightSplitCurve = splitBezierCurve.secondSubCurve

        val leftCorrectedTValues = leftTValues.map { it / medianTValue }
        val rightCorrectedTValues = rightTValues.map { (it - medianTValue) / (1.0 - medianTValue) }

        val leftSubSplitCurve = leftSplitCurve.splitAtMultipleSorted(
            tValuesSorted = leftCorrectedTValues,
        )

        val rightSubSplitCurveOrNull = rightSplitCurve.splitAtMultipleSorted(
            tValuesSorted = rightCorrectedTValues,
        )

        val joinedSpline = CubicBezierSpline.join(
            splines = listOfNotNull(
                leftSubSplitCurve,
                rightSubSplitCurveOrNull,
            ),
        )

        return joinedSpline
    }

    fun findOffsetSplineBestFit(
        offset: Double,
    ): CubicBezierSpline {
        val initialOffsetCurveBestFitResult = findOffsetCurveBestFit(
            offset = offset,
        )

        val initialOffsetCurve = initialOffsetCurveBestFitResult.offsetCurve
        val initialError = initialOffsetCurveBestFitResult.calculateError()

        if (initialError < bestFitErrorThreshold) {
            return initialOffsetCurve.toSpline()
        } else {
            val criticalPoints = basisFormula.findInterestingCriticalPoints().criticalPoints

            if (criticalPoints.isEmpty()) {
                val initialSplitSpline = splitAtMultiple(criticalPoints)

                return initialSplitSpline.joinOf { splitCurve ->
                    splitCurve.findOffsetSplineBestFitOrSubdivide(
                        offset = offset,
                        subdivisionLevel = 0,
                    )
                }
            } else {
                return subdivideAndFindOffsetSplineBestFit(
                    offset = offset,
                    subdivisionLevel = 0,
                )
            }
        }
    }

    private fun findOffsetSplineBestFitOrSubdivide(
        offset: Double,
        subdivisionLevel: Int,
    ): CubicBezierSpline {
        val offsetCurveBestFitResult = findOffsetCurveBestFit(
            offset = offset,
        )

        val offsetCurve = offsetCurveBestFitResult.offsetCurve
        val error = offsetCurveBestFitResult.calculateError()

        return when {
            error < bestFitErrorThreshold || subdivisionLevel >= bestFitMaxSubdivisionLevel -> offsetCurve.toSpline()

            else -> subdivideAndFindOffsetSplineBestFit(
                offset = offset,
                subdivisionLevel = subdivisionLevel,
            )
        }
    }

    private fun subdivideAndFindOffsetSplineBestFit(
        offset: Double,
        subdivisionLevel: Int,
    ): CubicBezierSpline {
        val splitBiBezierCurve = splitAtMidPoint()
        val leftSplitCurve = splitBiBezierCurve.firstSubCurve
        val rightSplitCurve = splitBiBezierCurve.secondSubCurve

        val nextSubDivisionLevel = subdivisionLevel + 1

        val leftSubSplitCurve = leftSplitCurve.findOffsetSplineBestFitOrSubdivide(
            offset = offset,
            subdivisionLevel = nextSubDivisionLevel,
        )

        val rightSubSplitCurve = rightSplitCurve.findOffsetSplineBestFitOrSubdivide(
            offset = offset,
            subdivisionLevel = nextSubDivisionLevel,
        )

        return leftSubSplitCurve.joinWith(rightSubSplitCurve)
    }


    override fun toPath2D(): Path2D.Double = Path2D.Double().apply {
        moveTo(start)
        cubicTo(control0, control1, end)
    }

    fun translate(
        translation: Translation,
    ): CubicBezierCurve = mapPointWise {
        it.translate(translation = translation)
    }
}
