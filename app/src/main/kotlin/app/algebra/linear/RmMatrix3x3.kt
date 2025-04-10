package app.algebra.linear

/**
 * Row-major square 3x3 matrix.
 */
typealias RmMatrix3x3 = Matrix3x3<VectorOrientation.Horizontal>

val RmMatrix3x3.column0: Vector3x1
    get() = Vector3x1.of(vector0.x, vector1.x, vector2.x)

val RmMatrix3x3.column1: Vector3x1
    get() = Vector3x1.of(vector0.y, vector1.y, vector2.y)

val RmMatrix3x3.column2: Vector3x1
    get() = Vector3x1.of(vector0.z, vector1.z, vector2.z)

val RmMatrix3x3.row0: Vector1x3
    get() = vector0

val RmMatrix3x3.row1: Vector1x3
    get() = vector1

val RmMatrix3x3.row2: Vector1x3
    get() = vector2

val RmMatrix3x3.transposed: CmMatrix3x3
    get() {
        @Suppress("UNCHECKED_CAST") return this as CmMatrix3x3
    }

operator fun RmMatrix3x3.times(
    vector: Vector3x1,
): Vector3x1 = Vector3.vertical(
    x = row0.dot(vector),
    y = row1.dot(vector),
    z = row2.dot(vector),
)

@JvmName("timesRm")
operator fun RmMatrix3x3.times(
    other: RmMatrix3x3,
): RmMatrix3x3 = Matrix3x3.rowMajor(
    row0 = row0 * other,
    row1 = row1 * other,
    row2 = row2 * other,
)

@JvmName("timesCm")
operator fun RmMatrix3x3.times(
    other: CmMatrix3x3,
): RmMatrix3x3 = Matrix3x3.rowMajor(
    row0 = row0 * other,
    row1 = row1 * other,
    row2 = row2 * other,
)
