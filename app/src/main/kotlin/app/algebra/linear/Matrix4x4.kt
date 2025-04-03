package app.algebra.linear

data class Matrix4x4(
    val row0: Vector1x4,
    val row1: Vector1x4,
    val row2: Vector1x4,
    val row3: Vector1x4,
) {
    companion object {
        val identity: Matrix4x4 = Matrix4x4(
            row0 = Vector1x4.of(1.0, 0.0, 0.0, 0.0),
            row1 = Vector1x4.of(0.0, 1.0, 0.0, 0.0),
            row2 = Vector1x4.of(0.0, 0.0, 1.0, 0.0),
            row3 = Vector1x4.of(0.0, 0.0, 0.0, 1.0),
        )
    }

    val column0: Vector4x1
        get() = Vector4x1.of(
            x = row0.x,
            y = row1.x,
            z = row2.x,
            w = row3.x,
        )

    val column1: Vector4x1
        get() = Vector4x1.of(
            x = row0.y,
            y = row1.y,
            z = row2.y,
            w = row3.y,
        )

    val column2: Vector4x1
        get() = Vector4x1.of(
            x = row0.z,
            y = row1.z,
            z = row2.z,
            w = row3.z,
        )

    val column3: Vector4x1
        get() = Vector4x1.of(
            x = row0.w,
            y = row1.w,
            z = row2.w,
            w = row3.w,
        )

    operator fun times(
        other: Matrix4x4,
    ): Matrix4x4 = Matrix4x4(
        row0 = Vector1x4.of(
            x = row0.dot(other.column0),
            y = row0.dot(other.column1),
            z = row0.dot(other.column2),
            w = row0.dot(other.column3),
        ),
        row1 = Vector1x4.of(
            x = row1.dot(other.column0),
            y = row1.dot(other.column1),
            z = row1.dot(other.column2),
            w = row1.dot(other.column3),
        ),
        row2 = Vector1x4.of(
            x = row2.dot(other.column0),
            y = row2.dot(other.column1),
            z = row2.dot(other.column2),
            w = row2.dot(other.column3),
        ),
        row3 = Vector1x4.of(
            x = row3.dot(other.column0),
            y = row3.dot(other.column1),
            z = row3.dot(other.column2),
            w = row3.dot(other.column3),
        ),
    )
}
