package app.algebra.linear

typealias Vector2x1 = Vector2<VectorOrientation.Vertical>

fun Vector2x1.dot(
    other: Vector1x2,
): Double = dotForced(other)

inline val Vector2x1.transposed: Vector1x2
    get() {
        @Suppress("UNCHECKED_CAST") return this as Vector1x2
    }

val Vector2x1.vectorX: Double
    get() = this.x

val Vector2x1.vectorY: Double
    get() = this.y
