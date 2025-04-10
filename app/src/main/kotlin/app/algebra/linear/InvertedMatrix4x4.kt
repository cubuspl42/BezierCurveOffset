package app.algebra.linear

data class InvertedMatrix4x4(
    /**
     * The LU(P) decomposition of the original matrix.
     */
    val lupDecomposition: Matrix4x4.LupDecomposition,
) {
    operator fun times(
        other: RmMatrix4x4,
    ): CmMatrix4x4 {
        val lMatrix = lupDecomposition.l
        val uMatrix = lupDecomposition.u
        val pMatrix = lupDecomposition.p

        val yMatrix = lMatrix.solveByForwardSubstitution(
            yMatrix = (pMatrix * other).toColumnMajor(),
        )

        val xMatrix = uMatrix.solveByBackSubstitution(
            yMatrix = yMatrix,
        )

        return xMatrix
    }

    operator fun times(
        other: InvertedMatrix4x4,
    ): CmMatrix4x4 = this * other.calculate().toRowMajor()

    fun calculate(): CmMatrix4x4 = this * Matrix4x4.identity
}
