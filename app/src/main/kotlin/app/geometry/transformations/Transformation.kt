package app.geometry.transformations

import app.algebra.linear.matrices.matrix3.Matrix3x3
import app.geometry.Point

abstract class Transformation {
    abstract fun transform(
        point: Point,
    ): Point

    abstract val transformationMatrix: Matrix3x3
}
