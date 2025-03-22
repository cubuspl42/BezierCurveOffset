package app.geometry.bezier_curves

import app.algebra.Vector
import app.algebra.bezier_formulas.*
import app.algebra.bezier_formulas.RealFunction.SamplingStrategy
import app.fillCircle
import app.geometry.*
import app.geometry.bezier_splines.*
import app.partitionSorted
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D

/**
 * A cubic Bézier curve
 */
data class CubicBezierCurve(
    val start: Point,
    val control0: Point,
    val control1: Point,
    val end: Point,
) {
    abstract class OffsetCurveApproximationResult(
        val offsetCurve: CubicBezierCurve,
    ) {
        companion object {
            val approximationRatingSampleCount = 16
        }

        abstract fun calculateError(): Double
    }

    sealed class OffsetStrategy {
        abstract fun approximateOffsetCurve(
            curve: CubicBezierCurve,
            offset: Double,
        ): CubicBezierCurve
    }

    data object BestFitOffsetStrategy : OffsetStrategy() {
        override fun approximateOffsetCurve(
            curve: CubicBezierCurve,
            offset: Double,
        ): CubicBezierCurve {
            val offsetTimedSeries = curve.findOffsetTimedSeries(offset = offset)
            return offsetTimedSeries.bestFitCurve()
        }
    }

    data object NormalOffsetStrategy : OffsetStrategy() {
        override fun approximateOffsetCurve(
            curve: CubicBezierCurve,
            offset: Double,
        ): CubicBezierCurve {
            val startNormalRay = curve.normalRayFunction.startValue
            val startNormalLine = startNormalRay.containingLine

            val endNormalRay = curve.normalRayFunction.endValue
            val endNormalLine = endNormalRay.containingLine

            val normalIntersectionPoint =
                startNormalLine.findIntersectionPoint(endNormalLine) ?: return curve.moveInDirectionPointWise(
                    // If there's no intersection point, the start and end vectors are parallel. We could choose either.
                    direction = startNormalRay.direction,
                    distance = offset,
                )

            return curve.moveAwayPointWise(
                origin = normalIntersectionPoint,
                distance = offset,
            )
        }
    }

    companion object {
        private const val findOffsetErrorThreshold = 0.0001
        private const val findOffsetMaxSubdivisionLevel = 8

        fun interConnect(
            prevNode: BezierSpline.InnerNode,
            nextNode: BezierSpline.InnerNode,
        ): CubicBezierCurve = CubicBezierCurve(
            start = prevNode.point,
            control0 = prevNode.forwardControl,
            control1 = nextNode.backwardControl,
            end = nextNode.point,
        )

        fun interConnectAll(
            innerNodes: List<BezierSpline.InnerNode>,
        ): List<CubicBezierCurve> = innerNodes.zipWithNext { prevNode, nextNode ->
            CubicBezierCurve.interConnect(
                prevNode = prevNode,
                nextNode = nextNode,
            )
        }

        fun bindRay(
            pointFunction: TimeFunction<Point>,
            vectorFunction: TimeFunction<Direction>,
        ): TimeFunction<Ray> = TimeFunction.map2(
            functionA = pointFunction,
            functionB = vectorFunction,
        ) { point, direction ->
            Ray.inDirection(
                point = point,
                direction = direction,
            )
        }
    }

    val curveFunction: TimeFunction<Point> by lazy {
        basisFormula.findFaster().map { it.toPoint() }
    }

    fun findOffsetCurveFunction(
        offset: Double,
    ): TimeFunction<Point> = normalRayFunction.map { normalRay ->
        normalRay.startingPoint.moveInDirection(
            direction = normalRay.direction,
            distance = offset,
        )
    }

    val tangentFunction: TimeFunction<Direction> by lazy {
        TimeFunction.wrap(basisFormula.findDerivative()).map {
            // TODO: This might actually be zero
            Direction(d = it)
        }
    }

    val tangentRayFunction: TimeFunction<Ray> by lazy {
        bindRay(
            pointFunction = curveFunction,
            vectorFunction = tangentFunction,
        )
    }

    val normalFunction: TimeFunction<Direction> by lazy {
        tangentFunction.map { it.perpendicular }
    }

    val normalRayFunction by lazy {
        bindRay(
            pointFunction = curveFunction,
            vectorFunction = normalFunction,
        )
    }

    fun findBoundingBox(): BoundingBox {
        val startPoint = curveFunction.startValue
        val endPoint = curveFunction.endValue

        val inRangeCriticalPointSet = basisFormula.findInterestingCriticalPoints()

        val criticalXValues = inRangeCriticalPointSet.criticalPointsX.map { t -> curveFunction.evaluate(t).x }
        val potentialXExtrema = criticalXValues + startPoint.x + endPoint.x
        val xMin = potentialXExtrema.min()
        val xMax = potentialXExtrema.max()

        val criticalYValues = inRangeCriticalPointSet.criticalPointsY.map { t -> curveFunction.evaluate(t).y }
        val potentialYExtrema = criticalYValues + startPoint.y + endPoint.y
        val yMin = potentialYExtrema.min()
        val yMax = potentialYExtrema.max()

        return BoundingBox.fromExtrema(
            xMin = xMin,
            xMax = xMax,
            yMin = yMin,
            yMax = yMax,
        )
    }

    fun findOffsetTimedSeries(
        offset: Double,
    ): TimedPointSeries {
        val offsetCurveFunction = findOffsetCurveFunction(offset = offset)

        return TimedPointSeries.sample(
            curveFunction = offsetCurveFunction,
            sampleCount = 6,
        )
    }

    fun draw(
        graphics2D: Graphics2D,
        innerColor: Color = Color.BLACK,
        outerColor: Color = Color.LIGHT_GRAY,
        outerSamplingStrategy: SamplingStrategy,
    ) {
        val outerPath = basisFormula.toPath2D(
            samplingStrategy = outerSamplingStrategy,
        )

        graphics2D.stroke = BasicStroke(1.0f)
        graphics2D.color = outerColor
        graphics2D.draw(outerPath)

        val innerPath = basisFormula.findFaster().toPath2D(
            samplingStrategy = outerSamplingStrategy.copy(
                x0 = 0.0,
                x1 = 1.0,
            ),
        )

        graphics2D.stroke = BasicStroke(2.0f)
        graphics2D.color = innerColor
        graphics2D.draw(innerPath)

        graphics2D.stroke = BasicStroke(0.5f)
        graphics2D.color = Color.LIGHT_GRAY

        basisFormula.segments.forEach { segment ->
            segment.draw(graphics2D = graphics2D)
        }

        graphics2D.color = Color.PINK

        graphics2D.fillCircle(
            center = start,
            radius = 6.0,
        )
        graphics2D.fillCircle(
            center = end,
            radius = 6.0,
        )

        val criticalPointSet = basisFormula.findAllCriticalPoints()

        fun drawCriticalPoints(
            criticalPoints: Set<Double>,
            color: Color,
        ) {
            criticalPoints.forEach { extremityT ->
                val extremityPoint = basisFormula.evaluate(t = extremityT).toPoint()

                graphics2D.color = color
                graphics2D.fillCircle(
                    center = extremityPoint,
                    radius = 4.0,
                )
            }
        }

        drawCriticalPoints(
            criticalPoints = criticalPointSet.criticalPointsX,
            color = Color.RED,
        )

        drawCriticalPoints(
            criticalPoints = criticalPointSet.criticalPointsY,
            color = Color.GREEN,
        )
    }

    fun isSingularity(): Boolean = setOf(start, control0, control1, end).size == 1

    val basisFormula = CubicBezierFormula(
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

    fun moveAwayPointWise(
        origin: Point,
        distance: Double,
    ): CubicBezierCurve = mapPointWise {
        it.moveAway(
            origin = origin,
            distance = distance,
        )
    }

    fun moveInDirectionPointWise(
        direction: Direction,
        distance: Double,
    ): CubicBezierCurve = mapPointWise {
        it.moveInDirection(
            direction = direction,
            distance = distance,
        )
    }

    fun splitAt(
        t: Double,
    ): BiBezierCurve {
        val skeleton0 = basisFormula.findSkeletonCubic(t = t)
        val skeleton1 = skeleton0.findSkeletonQuadratic(t = t)
        val midPoint = skeleton1.evaluateLinear(t = t).toPoint()

        return BiBezierCurve(
            startNode = BezierSpline.InnerNode.start(
                point = start,
                control1 = skeleton0.point0,
            ),
            midNode = BezierSpline.InnerNode(
                backwardControl = skeleton1.point0,
                point = midPoint,
                forwardControl = skeleton1.point1,
            ),
            endNode = BezierSpline.InnerNode.end(
                control0 = skeleton0.point2,
                point = end,
            ),
        )
    }

    fun splitAtMidPoint(): BiBezierCurve = splitAt(t = 0.5)

    fun splitAtCriticalPoints(): OpenBezierSpline {
        val criticalPoints = basisFormula.findInterestingCriticalPoints().criticalPoints

        val splitSpline = splitAtMultiple(
            tValues = criticalPoints,
        )

        return splitSpline
    }

    fun splitAtMultiple(
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

    private fun toSpline(): OpenBezierSpline = MonoBezierCurve(
        curve = this,
    )

    private fun splitAtMultipleSorted(
        tValuesSorted: List<Double>,
    ): OpenBezierSpline {
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

        val mergedSpline = OpenBezierSpline.merge(
            splines = listOfNotNull(
                leftSubSplitCurve,
                rightSubSplitCurveOrNull,
            ),
        )

        return mergedSpline
    }

    fun findOffsetSpline(
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
                    splitCurve.findOffsetSplineOrSubdivide(
                        strategy = strategy,
                        offset = offset,
                        subdivisionLevel = 0,
                    )
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
        strategy: OffsetStrategy,
        offset: Double
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

    private fun subdivideAndFindOffsetSpline(
        strategy: OffsetStrategy,
        offset: Double,
        subdivisionLevel: Int,
    ): OpenBezierSpline {
        val splitBiBezierCurve = splitAtMidPoint()
        val leftSplitCurve = splitBiBezierCurve.firstSubCurve
        val rightSplitCurve = splitBiBezierCurve.secondSubCurve

        val nextSubDivisionLevel = subdivisionLevel + 1

        val leftSubSplitCurve = leftSplitCurve.findOffsetSplineOrSubdivide(
            strategy = strategy,
            offset = offset,
            subdivisionLevel = nextSubDivisionLevel,
        )

        val rightSubSplitCurve = rightSplitCurve.findOffsetSplineOrSubdivide(
            strategy = strategy,
            offset = offset,
            subdivisionLevel = nextSubDivisionLevel,
        )

        return leftSubSplitCurve.mergeWith(rightSubSplitCurve)
    }


    fun toPath2D(): Path2D.Double = Path2D.Double().apply {
        moveTo(start)
        cubicTo(control0, control1, end)
    }

    fun translate(
        translation: Translation,
    ): CubicBezierCurve = mapPointWise {
        it.translate(translation = translation)
    }
}
