package app

import java.awt.geom.Path2D
import java.nio.file.Path

abstract class BezierCurve {
    data class LocalExtremitySet(
        val localExtremitiesX: Set<Double>,
        val localExtremitiesY: Set<Double>,
    )

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
        BezierCurve.bind(
            pointFunction = pathFunction,
            vectorFunction = tangentFunction,
        )
    }

    val normalFunction: TimeFunction<Vector> by lazy {
        tangentFunction.map { it.perpendicular }
    }

    val boundNormalFunction by lazy {
        BezierCurve.bind(
            pointFunction = pathFunction,
            vectorFunction = normalFunction,
        )
    }

    abstract val start: Point
    abstract val end: Point

    abstract val basisFormula: BezierFormula<Vector>

    fun findLocalExtremities(): LocalExtremitySet {
        fun findConsideredComponentLocalExtremities(
            componentFormula: BezierFormula<Double>,
        ): Set<Double> = componentFormula.findLocalExtremities().filter {
            it in (0.0..1.0)
        }.toSet()

        return LocalExtremitySet(
            localExtremitiesX = findConsideredComponentLocalExtremities(
                componentFormula = basisFormula.componentX,
            ), localExtremitiesY = findConsideredComponentLocalExtremities(
                componentFormula = basisFormula.componentY,
            )
        )
    }

    abstract fun toPath2D(): Path2D
}
