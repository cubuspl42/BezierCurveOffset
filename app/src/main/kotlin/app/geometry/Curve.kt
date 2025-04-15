package app.geometry

import app.algebra.NumericObject
import app.algebra.equalsWithTolerance

abstract class Curve {
    abstract class IntersectionDetails : GeometricObject, NumericObject {
        abstract val point: Point

        abstract val t0: Double

        abstract val t1: Double

        final override fun equalsWithTolerance(
            other: GeometricObject,
            tolerance: GeometricTolerance
        ): Boolean = when {
            other !is IntersectionDetails -> false
            !point.equalsWithTolerance(other.point, absoluteTolerance = tolerance.distanceTolerance) -> false
            !t0.equalsWithTolerance(other.t0, absoluteTolerance = tolerance.distanceTolerance) -> false
            !t1.equalsWithTolerance(other.t1, absoluteTolerance = tolerance.distanceTolerance) -> false
            else -> true
        }

        final override fun equalsWithTolerance(
            other: NumericObject,
            absoluteTolerance: Double,
        ): Boolean = when {
            other !is IntersectionDetails -> false
            !point.equalsWithTolerance(other.point, absoluteTolerance = absoluteTolerance) -> false
            !t0.equalsWithTolerance(other.t0, absoluteTolerance = absoluteTolerance) -> false
            !t1.equalsWithTolerance(other.t1, absoluteTolerance = absoluteTolerance) -> false
            else -> true
        }

        final override fun toString(): String = "IntersectionDetails(point=$point, t0=$t0, t1=$t1)"
    }

    abstract fun evaluate(t: Double): Point
}
