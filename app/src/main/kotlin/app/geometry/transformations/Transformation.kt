package app.geometry.transformations

import app.algebra.linear.matrices.matrix3.Matrix3x3
import app.geometry.Point

sealed class Transformation {
    data object Identity : Transformation() {
        override fun transform(point: Point): Point = point

        override val inverted: Transformation
            get() = this

        override val transformationMatrix: Matrix3x3
            get() = Matrix3x3.identity
    }

    /**
     * Combines this transformation with another one
     *
     * @param base - The transformation that comes before this transformation
     */
    fun applyOver(
        base: Transformation,
    ): MixedTransformation = MixedTransformation(
        transformationMatrix = transformationMatrix * base.transformationMatrix,
    )

    abstract fun transform(
        point: Point,
    ): Point

    abstract val inverted: Transformation

    abstract val transformationMatrix: Matrix3x3
}
