package app.algebra.linear

typealias Vector1x3 = Vector3<VectorOrientation.Horizontal>

fun Vector1x3.dot(
    other: Vector3x1,
): Double = dotForced(other)

@JvmName("timesRm")
operator fun Vector1x3.times(
    matrix: RmMatrix3x3,
): Vector1x3 = Vector3.horizontal(
    x = this.dot(matrix.column0),
    y = this.dot(matrix.column1),
    z = this.dot(matrix.column2),
)

@JvmName("timesCm")
operator fun Vector1x3.times(
    matrix: CmMatrix3x3,
): Vector1x3 = Vector3.horizontal(
    x = this.dot(matrix.column0),
    y = this.dot(matrix.column1),
    z = this.dot(matrix.column2),
)

inline val Vector1x3.transposed: Vector3x1
    get() {
        @Suppress("UNCHECKED_CAST") return this as Vector3x1
    }
