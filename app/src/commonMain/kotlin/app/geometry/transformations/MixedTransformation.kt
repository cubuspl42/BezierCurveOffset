package app.geometry.transformations

import app.algebra.equalsWithTolerance
import app.algebra.linear.matrices.matrix3.Matrix3x3
import app.algebra.linear.vectors.vector3.Vector1x3
import app.algebra.linear.vectors.vector3.Vector3x1
import app.algebra.linear.vectors.vector3.vector2
import app.geometry.Constants
import app.geometry.Point

data class MixedTransformation(
    override val transformationMatrix: Matrix3x3,
) : Transformation() {
    companion object {
        val identity = MixedTransformation(
            transformationMatrix = Matrix3x3.identity,
        )

        fun of(
            a: Double,
            b: Double,
            c: Double,
            d: Double,
            e: Double,
            f: Double,
        ): MixedTransformation = MixedTransformation(
            transformationMatrix = Matrix3x3.rowMajor(
                row0 = Vector1x3(
                    a0 = a,
                    a1 = c,
                    a2 = e,
                ),
                row1 = Vector1x3(
                    a0 = b,
                    a1 = d,
                    a2 = f,
                ),
                row2 = Vector1x3(
                    a0 = 0.0,
                    a1 = 0.0,
                    a2 = 1.0,
                ),
            ),
        )
    }

    override fun transform(
        point: Point,
    ): Point {
        // The transformed point in the homogeneous coordinates
        val pt: Vector3x1 = transformationMatrix * point.pv.vertical.toVec3()

        if (!pt.a2.equalsWithTolerance(1.0, absoluteTolerance = Constants.epsilon)) {
            throw AssertionError()
        }

        return Point.of(pv = pt.vector2.raw)
    }

    override val inverted: Transformation
        get() = TODO("Not yet implemented")
}