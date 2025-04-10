package app.algebra.linear.matrices.matrix3

import app.algebra.linear.vectors.vector3.Vector3
import app.algebra.linear.VectorOrientation

data class SquareMatrix3Data<Vo : VectorOrientation>(
    val vector0: Vector3<Vo>,
    val vector1: Vector3<Vo>,
    val vector2: Vector3<Vo>,
)

val SquareMatrix3Data<VectorOrientation.Horizontal>.interpretTransposed: SquareMatrix3Data<VectorOrientation.Vertical>
    @JvmName("interpretTransposedH") get() {
        @Suppress("UNCHECKED_CAST") return this as SquareMatrix3Data<VectorOrientation.Vertical>
    }

val SquareMatrix3Data<VectorOrientation.Vertical>.interpretTransposed: SquareMatrix3Data<VectorOrientation.Horizontal>
    @JvmName("interpretTransposedV") get() {
        @Suppress("UNCHECKED_CAST") return this as SquareMatrix3Data<VectorOrientation.Horizontal>
    }
