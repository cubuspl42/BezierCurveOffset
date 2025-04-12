package app.geometry.transformations

import app.algebra.linear.matrices.matrix3.Matrix3x3
import app.algebra.linear.vectors.vector3.Vector1x3
import app.geometry.Point
import app.geometry.times

data class Scaling(
    val factor: Double,
) : Transformation() {
    override fun transform(point: Point): Point {
        require(factor.isFinite())
        return Point.of(
            pv = factor * point.pv,
        )
    }

    override val transformationMatrix: Matrix3x3
        get() = Matrix3x3.rowMajor(
            row0 = Vector1x3.of(factor, 0.0, 0.0),
            row1 = Vector1x3.of(0.0, factor, 0.0),
            row2 = Vector1x3.of(0.0, 0.0, 1.0),
        )
}
