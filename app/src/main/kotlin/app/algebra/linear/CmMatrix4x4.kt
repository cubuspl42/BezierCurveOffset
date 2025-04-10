package app.algebra.linear

typealias CmMatrix4x4 = Matrix4x4<VectorOrientation.Vertical>

val CmMatrix4x4.row0: Vector1x4
    get() = Vector1x4.of(vector0.x, vector1.x, vector2.x, vector3.x)

val CmMatrix4x4.row1: Vector1x4
    get() = Vector1x4.of(vector0.y, vector1.y, vector2.y, vector3.y)

val CmMatrix4x4.row2: Vector1x4
    get() = Vector1x4.of(vector0.z, vector1.z, vector2.z, vector3.z)

val CmMatrix4x4.row3: Vector1x4
    get() = Vector1x4.of(vector0.w, vector1.w, vector2.w, vector3.w)

val CmMatrix4x4.column0: Vector4x1
    get() = vector0

val CmMatrix4x4.column1: Vector4x1
    get() = vector1

val CmMatrix4x4.column2: Vector4x1
    get() = vector2

val CmMatrix4x4.column3: Vector4x1
    get() = vector3

val CmMatrix4x4.transposed: RmMatrix4x4
    get() {
        @Suppress("UNCHECKED_CAST") return this as RmMatrix4x4
    }

fun CmMatrix4x4.toRowMajor(): RmMatrix4x4 = Matrix4x4.rowMajor(
    row0 = row0,
    row1 = row1,
    row2 = row2,
    row3 = row3,
)

operator fun CmMatrix4x4.times(
    vector: Vector4x1,
): Vector4x1 = Vector4x1.of(
    x = row0.dot(vector),
    y = row1.dot(vector),
    z = row2.dot(vector),
    w = row3.dot(vector),
)

@JvmName("timesRm")
operator fun CmMatrix4x4.times(
    other: RmMatrix4x4,
): RmMatrix4x4 = Matrix4x4.rowMajor(
    row0 = row0 * other,
    row1 = row1 * other,
    row2 = row2 * other,
    row3 = row3 * other,
)

@JvmName("timesCm")
operator fun CmMatrix4x4.times(
    other: CmMatrix4x4,
): CmMatrix4x4 = Matrix4x4.columnMajor(
    column0 = this * other.column0,
    column1 = this * other.column1,
    column2 = this * other.column2,
    column3 = this * other.column3,
)

@JvmName("timesRect")
operator fun CmMatrix4x4.times(
    other: Matrix4xN,
): Matrix4xN = Matrix4xN(
    columns = other.columns.map { column -> this * column },
)
