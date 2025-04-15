package app.geometry.transformations

import app.algebra.linear.matrices.matrix3.Matrix3x3
import app.geometry.Point

sealed class Transformation {
    abstract fun transform(
        point: Point,
    ): Point

    abstract val inverted: Transformation

    abstract val transformationMatrix: Matrix3x3
}
