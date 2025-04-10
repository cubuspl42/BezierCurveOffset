package app.algebra.linear.vectors.vector2

import app.algebra.linear.VectorOrientation

typealias Vector1x2 = Vector2<VectorOrientation.Horizontal>

fun Vector1x2.dot(
    other: Vector2x1,
): Double = dotForced(other)

inline val Vector1x2.transposed: Vector2x1
    get() {
        @Suppress("UNCHECKED_CAST") return this as Vector2x1
    }

val Vector1x2.vectorX: Double
    get() = this.x

val Vector1x2.vectorY: Double
    get() = this.y
