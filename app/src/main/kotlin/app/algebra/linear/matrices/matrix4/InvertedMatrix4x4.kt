package app.algebra.linear.matrices.matrix4

import app.algebra.linear.vectors.vector4.Vector4x1

class InvertedMatrix4x4(
    /**
     * The LU(P) decomposition of the original matrix.
     */
    private val lupDecomposition: Matrix4x4.LupDecomposition,
) : LazyMatrix4x4() {
    override fun times(
        vector: Vector4x1,
    ): Vector4x1 {
        val yVector = lMatrix.solveByForwardSubstitution(
            yVector = pMatrix * vector,
        )

        val xVector = uMatrix.solveByBackSubstitution(
            yVector = yVector,
        )

        return xVector
    }

    override operator fun times(
        other: Matrix4x4,
    ): ColumnMajorMatrix4x4 {
        val yMatrix = lMatrix.solveByForwardSubstitution(
            yMatrix = pMatrix * other,
        )

        val xMatrix = uMatrix.solveByBackSubstitution(
            yMatrix = yMatrix,
        )

        return xMatrix
    }

    override fun compute(): EagerMatrix4x4 = this * Matrix4x4.identity

    private val lMatrix: RowMajorMatrix4x4
        get() = lupDecomposition.l

    private val uMatrix: RowMajorMatrix4x4
        get() = lupDecomposition.u

    private val pMatrix: RowMajorMatrix4x4
        get() = lupDecomposition.p
}
