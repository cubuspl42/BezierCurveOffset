package app.algebra.linear.matrices.matrix4

import app.algebra.linear.vectors.vector4.Vector4
import app.algebra.linear.VectorOrientation

data class SquareMatrix4Data<Vo : VectorOrientation>(
    val vector0: Vector4<Vo>,
    val vector1: Vector4<Vo>,
    val vector2: Vector4<Vo>,
    val vector3: Vector4<Vo>,
)

val SquareMatrix4Data<VectorOrientation.Horizontal>.interpretTransposed: SquareMatrix4Data<VectorOrientation.Vertical>
    @JvmName("interpretTransposedH") get() {
        @Suppress("UNCHECKED_CAST") return this as SquareMatrix4Data<VectorOrientation.Vertical>
    }

val SquareMatrix4Data<VectorOrientation.Vertical>.interpretTransposed: SquareMatrix4Data<VectorOrientation.Horizontal>
    @JvmName("interpretTransposedV") get() {
        @Suppress("UNCHECKED_CAST") return this as SquareMatrix4Data<VectorOrientation.Horizontal>
    }
