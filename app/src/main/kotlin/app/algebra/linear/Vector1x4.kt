package app.algebra.linear

typealias Vector1x4 = Vector4<VectorOrientation.Horizontal>

fun Vector1x4.dot(
    other: Vector4x1,
): Double = dotForced(other)

inline val Vector1x4.transposed: Vector4x1
    get() {
        @Suppress("UNCHECKED_CAST") return this as Vector4x1
    }
