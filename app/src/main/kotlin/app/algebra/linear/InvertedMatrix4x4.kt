package app.algebra.linear

data class InvertedMatrix4x4(
    /**
     * The LU(P) decomposition of the original matrix.
     */
    val lupDecomposition: Matrix4x4.LupDecomposition,
) {
    operator fun times(
        other: Matrix4x4,
    ): Matrix4x4 {
        val lMatrix = lupDecomposition.l
        val uMatrix = lupDecomposition.u
        val pMatrix = lupDecomposition.p

        val yMatrix = lMatrix.solveByForwardSubstitution(pMatrix * other)
        val xMatrix = uMatrix.solveByBackSubstitution(yMatrix)

        return xMatrix
    }

    fun calculate(): Matrix4x4 {
        val lMatrix = lupDecomposition.l
        val uMatrix = lupDecomposition.u
        val pMatrix = lupDecomposition.p

        // Let A = this (1)
        // It's given that A = P * L * U (1b)
        // let C = A^-1 (2)
        // From (1b) and (2), C = U^-1 * L^-1 * P^-1 (3)

        // Let B = L^-1 * P^-1 (4), so B^-1 = P * L (5)
        // From (3) and (4), C = U^-1 * B (6)
        // From (6), C^-1 = B^-1 * U (7)

        // As P is a pivot vector, P^-1 = P^T
        val pMatrixInverted = pMatrix.transposed

        // From B^-1 * B = Id and (5), P * L * B = Id, so L * B = P^-1 (8)
        // As L is an LTM, B can be found by forward substitution
        val bMatrix = lMatrix.solveByForwardSubstitution(pMatrixInverted)

        // From C^-1 * C = Id and (7), (B^-1 * U) * C = Id, so U * C = B
        // As U is an UTM, C can be found by backward substitution
        val cMatrix = uMatrix.solveByBackSubstitution(bMatrix)

        // From (2), we're done
        return cMatrix
    }
}
