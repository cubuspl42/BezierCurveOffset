package app.algebra.linear

import app.fillByColumn
import org.ujmp.core.Matrix

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
    ): Matrix4x4 = Matrix4x4(
        column0 = Vector4x1.of(
            x = row0.dot(other.column0),
            y = row1.dot(other.column0),
            z = row2.dot(other.column0),
            w = row3.dot(other.column0),
        ),
        column1 = Vector4x1.of(
            x = row0.dot(other.column1),
            y = row1.dot(other.column1),
            z = row2.dot(other.column1),
            w = row3.dot(other.column1),
        ),
        column2 = Vector4x1.of(
            x = row0.dot(other.column2),
            y = row1.dot(other.column2),
            z = row2.dot(other.column2),
            w = row3.dot(other.column2),
        ),
        column3 = Vector4x1.of(
            x = row0.dot(other.column3),
            y = row1.dot(other.column3),
            z = row2.dot(other.column3),
            w = row3.dot(other.column3),
        ),
    )

    fun toUjmpMatrix(): Matrix = Matrix.Factory.fillByColumn(
        columnElements = listOf(
            column0,
            column1,
            column2,
            column3,
        ),
        columnHeight = 4,
        buildColumn = { column ->
            column.toArray()
        },
    )
}
