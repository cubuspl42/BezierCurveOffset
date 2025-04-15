package app.geometry.transformations

import app.algebra.linear.matrices.matrix3.Matrix3x3
import app.geometry.Point

data class ComplexTransformation(
    /**
     * The list of transformations to be applied, from the first to the last.
     */
    val transformations: List<SimpleTransformation>,
) : Transformation() {
    override fun transform(
        point: Point,
    ): Point = transformations.fold(point) { accPoint, transformation ->
        transformation.transform(accPoint)
    }

    override val inverted: ComplexTransformation
        get() = ComplexTransformation(
            transformations = transformations.map { it.inverted }.reversed(),
        )

    override val transformationMatrix: Matrix3x3 by lazy {
        transformations.fold(
            initial = Matrix3x3.identity,
        ) { accMatrix, transformation ->
            accMatrix * transformation.transformationMatrix
        }
    }
}
