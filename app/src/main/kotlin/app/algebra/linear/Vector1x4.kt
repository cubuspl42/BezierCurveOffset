package app.algebra.linear

typealias Vector1x4 = Vector4<VectorOrientation.Horizontal>

fun Vector1x4.dot(
    other: Vector4x1,
): Double = dotForced(other)

@JvmName("timesRm")
operator fun Vector1x4.times(
    other: RmMatrix4x4,
): Vector1x4 = Vector4.horizontal(
    x = this.dot(other.column0),
    y = this.dot(other.column1),
    z = this.dot(other.column2),
    w = this.dot(other.column3),
)

@JvmName("timesCm")
operator fun Vector1x4.times(
    other: CmMatrix4x4,
): Vector1x4 = Vector4.horizontal(
    x = this.dot(other.column0),
    y = this.dot(other.column1),
    z = this.dot(other.column2),
    w = this.dot(other.column3),
)

inline val Vector1x4.transposed: Vector4x1
    get() {
        @Suppress("UNCHECKED_CAST") return this as Vector4x1
    }
