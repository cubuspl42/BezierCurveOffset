package app.geometry.bezier_curves

import app.*
import app.arithmetic.bezier_formulas.*
import app.arithmetic.bezier_formulas.RealFunction.SamplingStrategy
import app.geometry.Point
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.Path2D
import kotlin.math.roundToInt

abstract class BezierCurve {
    companion object {
        fun bind(
            pointFunction: TimeFunction<Point>,
            vectorFunction: TimeFunction<Vector>,
        ): TimeFunction<BoundVector> = TimeFunction.map2(
            functionA = pointFunction,
            functionB = vectorFunction,
        ) { point, vector ->
            vector.bind(point)
        }
    }

    val pathFunction: TimeFunction<Point> by lazy {
        TimeFunction.wrap(basisFormula).map { it.toPoint() }
    }

    val tangentFunction: TimeFunction<Vector> by lazy {
        TimeFunction.wrap(basisFormula.findDerivative())
    }

    val boundTangentFunction by lazy {
        bind(
            pointFunction = pathFunction,
            vectorFunction = tangentFunction,
        )
    }

    val normalFunction: TimeFunction<Vector> by lazy {
        tangentFunction.map { it.perpendicular }
    }

    val boundNormalFunction by lazy {
        bind(
            pointFunction = pathFunction,
            vectorFunction = normalFunction,
        )
    }

    abstract val start: Point
    abstract val end: Point

    abstract val basisFormula: BezierFormula<Vector>

    fun draw(
        graphics2D: Graphics2D,
        innerColor: Color,
        outerColor: Color,
        outerSamplingStrategy: SamplingStrategy,
    ) {
        val outerPath = basisFormula.toPath2D(
            samplingStrategy = outerSamplingStrategy,
        )

        graphics2D.stroke = BasicStroke(1.0f)
        graphics2D.color = outerColor
        graphics2D.draw(outerPath)

        val innerPath = toPath2D()

        graphics2D.stroke = BasicStroke(2.0f)
        graphics2D.color = innerColor
        graphics2D.draw(innerPath)

        graphics2D.color = Color.PINK

        graphics2D.fillCircle(
            center = start,
            radius = 6.0,
        )
        graphics2D.fillCircle(
            center = end,
            radius = 6.0,
        )

        val criticalPointSet = basisFormula.findCriticalPoints()

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

    abstract fun toPath2D(): Path2D
}

fun Graphics2D.drawCircle(
    center: Point,
    radius: Double,
) {
    val diameter = (2 * radius).roundToInt()
    val x = center.x - radius
    val y = center.y - radius
    drawOval(x.roundToInt(), y.roundToInt(), diameter, diameter)
}

fun Graphics2D.fillCircle(
    center: Point,
    radius: Double,
) {
    val diameter = (2 * radius).roundToInt()
    val x = center.x - radius
    val y = center.y - radius
    fillOval(x.roundToInt(), y.roundToInt(), diameter, diameter)
}
