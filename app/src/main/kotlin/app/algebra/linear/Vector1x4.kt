package app.algebra.linear

typealias Vector1x4 = Vector4<VectorOrientation.Horizontal>

@JvmName("dotHv")
fun Vector1x4.dot(
    other: Vector4x1,
): Double = dotForced(other)

inline val Vector1x4.transposed: Vector4x1
    get() {
        @Suppress("UNCHECKED_CAST") return this as Vector4x1
    }

val Vector1x4.vectorYzw: Vector1x3
    get() = Vector1x3.of(
        x = this.y,
        y = this.z,
        z = this.w,
    )

val Vector1x4.vectorZw: Vector1x2
    get() = Vector1x2.of(
        x = this.z,
        y = this.w,
    )

val Vector1x4.vectorXyz: Vector1x3
    get() = Vector1x3.of(
        x = this.x,
        y = this.y,
        z = this.z,
    )

val Vector1x4.vectorXy: Vector1x2
    get() = Vector1x2.of(
        x = this.x,
        y = this.y,
    )
