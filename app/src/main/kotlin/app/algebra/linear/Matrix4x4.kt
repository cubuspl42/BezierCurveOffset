package app.algebra.linear

import app.algebra.NumericObject

data class Matrix4x4<out Vo : VectorOrientation>(
    val vector0: Vector4<Vo>,
    val vector1: Vector4<Vo>,
    val vector2: Vector4<Vo>,
    val vector3: Vector4<Vo>,
) : NumericObject {
    companion object {
        val zero = Matrix4x4.rowMajor(
            row0 = Vector4(0.0, 0.0, 0.0, 0.0),
            row1 = Vector4(0.0, 0.0, 0.0, 0.0),
            row2 = Vector4(0.0, 0.0, 0.0, 0.0),
            row3 = Vector4(0.0, 0.0, 0.0, 0.0),
        )

        val identity = Matrix4x4.rowMajor(
            row0 = Vector4(1.0, 0.0, 0.0, 0.0),
            row1 = Vector4(0.0, 1.0, 0.0, 0.0),
            row2 = Vector4(0.0, 0.0, 1.0, 0.0),
            row3 = Vector4(0.0, 0.0, 0.0, 1.0),
        )

        fun rowMajor(
            row0: Vector1x4,
            row1: Vector1x4,
            row2: Vector1x4,
            row3: Vector1x4,
        ): RmMatrix4x4 = Matrix4x4(
            vector0 = row0,
            vector1 = row1,
            vector2 = row2,
            vector3 = row3,
        )

        fun columnMajor(
            column0: Vector4x1,
            column1: Vector4x1,
            column2: Vector4x1,
            column3: Vector4x1,
        ): CmMatrix4x4 = Matrix4x4(
            vector0 = column0,
            vector1 = column1,
            vector2 = column2,
            vector3 = column3,
        )
    }

    data class LuDecomposition(
        val l: RmMatrix4x4,
        val u: RmMatrix4x4,
    )

    data class LupDecomposition(
        val l: RmMatrix4x4,
        val u: RmMatrix4x4,
        val p: RmMatrix4x4,
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        absoluteTolerance: Double
    ): Boolean = when {
        other !is Matrix4x4<*> -> false
        !vector0.equalsWithTolerance(other.vector0, absoluteTolerance = absoluteTolerance) -> false
        !vector1.equalsWithTolerance(other.vector1, absoluteTolerance = absoluteTolerance) -> false
        !vector2.equalsWithTolerance(other.vector2, absoluteTolerance = absoluteTolerance) -> false
        !vector3.equalsWithTolerance(other.vector3, absoluteTolerance = absoluteTolerance) -> false
        else -> true
    }
}
