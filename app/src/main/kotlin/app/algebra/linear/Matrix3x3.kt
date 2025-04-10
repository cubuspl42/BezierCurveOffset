package app.algebra.linear

import app.algebra.NumericObject

data class Matrix3x3<out Vo : VectorOrientation>(
    val vector0: Vector3<Vo>,
    val vector1: Vector3<Vo>,
    val vector2: Vector3<Vo>,
) : NumericObject {
    companion object {
        fun <Vo : VectorOrientation> identity(): Matrix3x3<Vo> = Matrix3x3(
            vector0 = Vector3(1.0, 0.0, 0.0),
            vector1 = Vector3(0.0, 1.0, 0.0),
            vector2 = Vector3(0.0, 0.0, 1.0),
        )

        fun rowMajor(
            row0: Vector1x3,
            row1: Vector1x3,
            row2: Vector1x3,
        ): RmMatrix3x3 = Matrix3x3(
            vector0 = row0,
            vector1 = row1,
            vector2 = row2,
        )

        fun columMajor(
            column0: Vector3x1,
            column1: Vector3x1,
            column2: Vector3x1,
        ): CmMatrix3x3 = Matrix3x3(
            vector0 = column0,
            vector1 = column1,
            vector2 = column2,
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double
    ): Boolean = when {
        other !is Matrix3x3<*> -> false
        !vector0.equalsWithTolerance(other.vector0, absoluteTolerance = absoluteTolerance) -> false
        !vector1.equalsWithTolerance(other.vector1, absoluteTolerance = absoluteTolerance) -> false
        !vector2.equalsWithTolerance(other.vector2, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }
}
