package app.algebra.linear

typealias Vector4x1 = Vector4<VectorOrientation.Vertical>

fun Vector4x1.dot(
    other: Vector1x4,
): Double = dotForced(other)

inline val Vector4x1.transposed: Vector1x4
    get() {
        @Suppress("UNCHECKED_CAST") return this as Vector1x4
    }
