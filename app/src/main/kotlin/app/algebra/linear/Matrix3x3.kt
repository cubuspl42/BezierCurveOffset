package app.algebra.linear

data class Matrix3x3(
    val row0: Vector1x3,
    val row1: Vector1x3,
    val row2: Vector1x3,
) {
    companion object {
        val identity: Matrix3x3 = Matrix3x3(
            row0 = Vector1x3.of(1.0, 0.0, 0.0),
            row1 = Vector1x3.of(0.0, 1.0, 0.0),
            row2 = Vector1x3.of(0.0, 0.0, 1.0),
        )
    }

    val column0: Vector3x1
        get() = Vector3x1.of(
            x = row0.x,
            y = row1.x,
            z = row2.x,
        )

    val column1: Vector3x1
        get() = Vector3x1.of(
            x = row0.y,
            y = row1.y,
            z = row2.y,
        )

    val column2: Vector3x1
        get() = Vector3x1.of(
            x = row0.z,
            y = row1.z,
            z = row2.z,
        )

    operator fun times(
        other: Matrix3x3,
    ): Matrix3x3 = Matrix3x3(
        row0 = Vector1x3.of(
            x = row0.dotForced(other.column0),
            y = row0.dotForced(other.column1),
            z = row0.dotForced(other.column2),
        ),
        row1 = Vector1x3.of(
            x = row1.dotForced(other.column0),
            y = row1.dotForced(other.column1),
            z = row1.dotForced(other.column2),
        ),
        row2 = Vector1x3.of(
            x = row2.dotForced(other.column0),
            y = row2.dotForced(other.column1),
            z = row2.dotForced(other.column2),
        ),
    )

    operator fun times(
        other: Vector3x1,
    ): Vector3x1 = Vector3x1.of(
        x = row0.dotForced(other),
        y = row1.dotForced(other),
        z = row2.dotForced(other),
    )
}
