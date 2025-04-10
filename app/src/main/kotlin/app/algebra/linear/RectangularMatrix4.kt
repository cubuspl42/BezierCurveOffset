package app.algebra.linear

object RectangularMatrix4 {
    fun horizontal(
        columns: List<Vector4x1>,
    ): Matrix4xN = Matrix4xN(
        data = RectangularMatrix4Data(
            vectors = columns,
        ),
    )

    fun vertical(
        rows: List<Vector1x4>,
    ): MatrixNx4 = MatrixNx4(
        data = RectangularMatrix4Data(
            vectors = rows,
        ),
    )
}
