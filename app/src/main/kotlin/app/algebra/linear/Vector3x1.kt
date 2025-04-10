package app.algebra.linear

typealias Vector3x1 = Vector3<VectorOrientation.Vertical>

fun Vector3x1.dot(
    other: Vector1x3,
): Double = dotForced(other)

inline val Vector3x1.transposed: Vector1x3
    get() {
        @Suppress("UNCHECKED_CAST") return this as Vector1x3
    }
