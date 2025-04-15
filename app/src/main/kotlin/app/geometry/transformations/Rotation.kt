package app.geometry.transformations

import app.algebra.linear.matrices.matrix3.Matrix3x3
import app.algebra.linear.vectors.vector3.Vector1x3
import app.geometry.Point
import app.geometry.PrincipalAngle
import app.geometry.times

@Suppress("DataClassPrivateConstructor")
data class Rotation private constructor(
    val angle: PrincipalAngle,
) : SimpleTransformation() {
    companion object {
        fun byAngle(
            angle: PrincipalAngle,
        ): Rotation = Rotation(
            angle = angle,
        )
    }

    override fun transform(
        point: Point,
    ): Point = Point.of(
        px = point.x * angle.cosFi - point.y * angle.sinFi,
        py = point.x * angle.sinFi + point.y * angle.cosFi,
    )

    override val inverted: Rotation
        get() = Rotation(
            angle = angle.inverted,
        )

    override val transformationMatrix: Matrix3x3
        get() = Matrix3x3.rowMajor(
            row0 = Vector1x3.of(angle.cosFi, -angle.sinFi, 0.0),
            row1 = Vector1x3.of(angle.sinFi, angle.cosFi, 0.0),
            row2 = Vector1x3.of(0.0, 0.0, 1.0),
        )
}
