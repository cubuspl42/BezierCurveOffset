package app.algebra.linear.matrices.matrix4

import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.linear.VectorOrientation
import kotlin.jvm.JvmName

data class RectangularMatrix4Data<Vo : VectorOrientation>(
    val vectors: List<Vector4<Vo>>,
) {
    init {
        require(vectors.isNotEmpty())
    }
}

val RectangularMatrix4Data<VectorOrientation.Horizontal>.interpretTransposed: RectangularMatrix4Data<VectorOrientation.Vertical>
    @JvmName("interpretTransposedH") get() {
        @Suppress("UNCHECKED_CAST") return this as RectangularMatrix4Data<VectorOrientation.Vertical>
    }

val RectangularMatrix4Data<VectorOrientation.Vertical>.interpretTransposed: RectangularMatrix4Data<VectorOrientation.Horizontal>
    @JvmName("interpretTransposedV") get() {
        @Suppress("UNCHECKED_CAST") return this as RectangularMatrix4Data<VectorOrientation.Horizontal>
    }
