package app.algebra.linear

typealias Vector1x3 = Vector3<VectorOrientation.Horizontal>

fun Vector1x3.dot(
    other: Vector3x1,
): Double = dotForced(other)

inline val Vector1x3.transposed: Vector3x1
    get() {
        @Suppress("UNCHECKED_CAST") return this as Vector3x1
    }
