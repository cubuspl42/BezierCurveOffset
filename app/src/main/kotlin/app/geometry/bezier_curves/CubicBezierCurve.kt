package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_formulas.*
import app.geometry.*
import app.partitionSorted
import java.awt.geom.Path2D

data class CubicBezierCurve(
    override val start: Point,
    val control0: Point,
    val control1: Point,
    override val end: Point,
) : BezierCurve(), CubicBezierSpline {
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

    override fun split(
        t: Double,
    ): Pair<CubicBezierCurve, CubicBezierCurve?> {
        if (isSingularity()) {
            return Pair(this, null)
        }

        val skeleton0 = basisFormula.findSkeletonCubic(t = t)
        val skeleton1 = skeleton0.findSkeletonQuadratic(t = t)
        val midPoint = skeleton1.evaluateLinear(t = t).toPoint()

        val firstCurve = CubicBezierCurve(
            start = start,
            control0 = skeleton0.point0,
            control1 = skeleton1.point0,
            end = midPoint,
        )

        if (firstCurve.isSingularity()) {
            return Pair(this, null)
        }

        val secondCurve = CubicBezierCurve(
            start = midPoint,
            control0 = skeleton1.point1,
            control1 = skeleton0.point2,
            end = end,
        )

        return Pair(
            firstCurve,
            secondCurve.takeIf { !it.isSingularity() },
        )
    }

    fun splitAtCriticalPoints(): CubicBezierSpline? {
        val criticalPoints = basisFormula.findInterestingCriticalPoints().criticalPoints

        if (criticalPoints.isEmpty()) {
            return null
        }

        val splitSpline = splitAt(
            tValues = criticalPoints,
        )

        return splitSpline
    }

    fun splitAt(
        tValues: Set<Double>,
    ): CubicBezierSpline {
        val tValuesSorted = tValues.sorted()

        val spline = splitAtRecursive(
            tValuesSorted = tValuesSorted,
        )

        return spline
    }

    fun splitAtRecursive(
        tValuesSorted: List<Double>,
    ): CubicBezierSpline {
        val partitioningResult = tValuesSorted.partitionSorted() ?: return this

        val leftTValues = partitioningResult.leftPart
        val medianTValue = partitioningResult.medianValue
        val rightTValues = partitioningResult.rightPart

        val (leftSplitCurve, rightSplitCurveOrNull) = split(t = medianTValue)

        val leftCorrectedTValues = leftTValues.map { it / medianTValue }
        val rightCorrectedTValues = rightTValues.map { (it - medianTValue) / (1.0 - medianTValue) }

        val leftSubSplitCurve = leftSplitCurve.splitAtRecursive(tValuesSorted = leftCorrectedTValues)
        val rightSubSplitCurveOrNull = rightSplitCurveOrNull?.splitAtRecursive(tValuesSorted = rightCorrectedTValues)

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
    ): CubicBezierSpline = findOffsetSplineBestFitRecursive(
        offset = offset,
        level = 0,
    )

    private fun findOffsetSplineBestFitRecursive(
        offset: Double,
        level: Int,
    ): CubicBezierSpline {
        val maxLevel = 8
        val errorThreshold = 0.0001

        val offsetCurveBestFitResult = findOffsetCurveBestFit(
            offset = offset,
        )

        val offsetCurve = offsetCurveBestFitResult.offsetCurve
        val error = offsetCurveBestFitResult.calculateError()

        // TODO: First split at extrema, then at the midpoint (also, check the second derivative?)
        return when {
            error < errorThreshold || level >= maxLevel -> offsetCurve

            else -> {
                val splitSpline = splitAtCriticalPoints() ?: return offsetCurve

                splitSpline.joinOf { splitCurve ->
                    splitCurve.findOffsetSplineBestFitRecursive(
                        offset = offset,
                        level = level + 1,
                    )
                }
            }
        }
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

    override val nodes: List<CompositeCubicBezierCurve.Node> = listOf(
        CompositeCubicBezierCurve.Node(
            control0 = start,
            point = start,
            control1 = control0,
        ),
        CompositeCubicBezierCurve.Node(
            control0 = control1,
            point = end,
            control1 = end,
        ),
    )

    override val subCurves: List<CubicBezierCurve> = listOf(this)
}
