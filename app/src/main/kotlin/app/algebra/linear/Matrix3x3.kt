package app.algebra.linear

data class Matrix3x3(
    val row0: Vector3,
    val row1: Vector3,
    val row2: Vector3,
) {
    companion object {
        val identity: Matrix3x3 = Matrix3x3(
            row0 = Vector3(1.0, 0.0, 0.0),
            row1 = Vector3(0.0, 1.0, 0.0),
            row2 = Vector3(0.0, 0.0, 1.0),
        )
    }

    val column0: Vector3
        get() = Vector3(
            x = row0.x,
            y = row1.x,
            z = row2.x,
        )

    val column1: Vector3
        get() = Vector3(
            x = row0.y,
            y = row1.y,
            z = row2.y,
        )

    val column2: Vector3
        get() = Vector3(
            x = row0.z,
            y = row1.z,
            z = row2.z,
        )

    operator fun times(
        other: Matrix3x3,
    ): Matrix3x3 = Matrix3x3(
        row0 = Vector3(
            x = row0.dot(other.column0),
            y = row0.dot(other.column1),
            z = row0.dot(other.column2),
        ),
        row1 = Vector3(
            x = row1.dot(other.column0),
            y = row1.dot(other.column1),
            z = row1.dot(other.column2),
        ),
        row2 = Vector3(
            x = row2.dot(other.column0),
            y = row2.dot(other.column1),
            z = row2.dot(other.column2),
        ),
    )

    /**
     * Multiplies this matrix by a vector
     *
     * @param vector - the vector to multiply (interpreted as a column vector)
     */
    fun timesTransposed(
        vector: Vector3,
    ): Vector3 = Vector3(
        x = row0.dot(vector),
        y = row1.dot(vector),
        z = row2.dot(vector),
    )
}
