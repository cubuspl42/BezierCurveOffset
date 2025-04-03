package app.algebra.linear

data class Matrix4x4(
    val column0: Vector4x1,
    val column1: Vector4x1,
    val column2: Vector4x1,
    val column3: Vector4x1,
) {
    companion object {
        val identity: Matrix4x4 = Matrix4x4(
            column0 = Vector4x1.of(1.0, 0.0, 0.0, 0.0),
            column1 = Vector4x1.of(0.0, 1.0, 0.0, 0.0),
            column2 = Vector4x1.of(0.0, 0.0, 1.0, 0.0),
            column3 = Vector4x1.of(0.0, 0.0, 0.0, 1.0),
        )
    }

    val row0: Vector1x4
        get() = Vector1x4.of(
            x = column0.x,
            y = column1.x,
            z = column2.x,
            w = column3.x,
        )

    val row1: Vector1x4
        get() = Vector1x4.of(
            x = column0.y,
            y = column1.y,
            z = column2.y,
            w = column3.y,
        )

    val row2: Vector1x4
        get() = Vector1x4.of(
            x = column0.z,
            y = column1.z,
            z = column2.z,
            w = column3.z,
        )

    val row3: Vector1x4
        get() = Vector1x4.of(
            x = column0.w,
            y = column1.w,
            z = column2.w,
            w = column3.w,
        )

    operator fun times(
        other: Matrix4x4,
    ): Matrix4x4 {
        return Matrix4x4(
            column0 = Vector4x1.of(
                x = column0.dot(other.row0),
                y = column0.dot(other.row1),
                z = column0.dot(other.row2),
                w = column0.dot(other.row3),
            ),
            column1 = Vector4x1.of(
                x = column1.dot(other.row0),
                y = column1.dot(other.row1),
                z = column1.dot(other.row2),
                w = column1.dot(other.row3),
            ),
            column2 = Vector4x1.of(
                x = column2.dot(other.row0),
                y = column2.dot(other.row1),
                z = column2.dot(other.row2),
                w = column2.dot(other.row3),
            ),
            column3 = Vector4x1.of(
                x = column3.dot(other.row0),
                y = column3.dot(other.row1),
                z = column3.dot(other.row2),
                w = column3.dot(other.row3),
            ),
        )
    }
}