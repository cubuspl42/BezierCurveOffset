package app.algebra.linear

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
