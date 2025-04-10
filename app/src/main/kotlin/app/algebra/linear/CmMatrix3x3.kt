package app.algebra.linear

/**
 * Column-major square 3x3 matrix.
 */
typealias CmMatrix3x3 = Matrix3x3<VectorOrientation.Vertical>

val CmMatrix3x3.row0: Vector1x3
    get() = Vector1x3.of(vector0.x, vector1.x, vector2.x)

val CmMatrix3x3.row1: Vector1x3
    get() = Vector1x3.of(vector0.y, vector1.y, vector2.y)

val CmMatrix3x3.row2: Vector1x3
    get() = Vector1x3.of(vector0.z, vector1.z, vector2.z)

val CmMatrix3x3.column0: Vector3x1
    get() = vector0

val CmMatrix3x3.column1: Vector3x1
    get() = vector1

val CmMatrix3x3.column2: Vector3x1
    get() = vector2

val CmMatrix3x3.transposed: RmMatrix3x3
    get() {
        @Suppress("UNCHECKED_CAST") return this as RmMatrix3x3
    }

operator fun CmMatrix3x3.times(
    vector: Vector3x1,
): Vector3x1 = Vector3x1.of(
    x = row0.dot(vector),
    y = row1.dot(vector),
    z = row2.dot(vector),
)

@JvmName("timesRm")
operator fun CmMatrix3x3.times(
    other: RmMatrix3x3,
): RmMatrix3x3 = Matrix3x3.rowMajor(
    row0 = Vector1x3.of(
        row0.dot(other.column0),
        row0.dot(other.column1),
        row0.dot(other.column2),
    ),
    row1 = Vector1x3.of(
        row1.dot(other.column0),
        row1.dot(other.column1),
        row1.dot(other.column2),
    ),
    row2 = Vector1x3.of(
        row2.dot(other.column0),
        row2.dot(other.column1),
        row2.dot(other.column2),
    ),
)

@JvmName("timesCm")
operator fun CmMatrix3x3.times(
    other: CmMatrix3x3,
): RmMatrix3x3 = Matrix3x3.rowMajor(
    row0 = Vector1x3.of(
        row0.dot(other.column0),
        row0.dot(other.column1),
        row0.dot(other.column2),
    ),
    row1 = Vector1x3.of(
        row1.dot(other.column0),
        row1.dot(other.column1),
        row1.dot(other.column2),
    ),
    row2 = Vector1x3.of(
        row2.dot(other.column0),
        row2.dot(other.column1),
        row2.dot(other.column2),
    ),
)