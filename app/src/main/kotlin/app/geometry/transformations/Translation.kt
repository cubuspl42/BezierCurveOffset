package app.geometry.transformations

import app.algebra.linear.matrices.matrix3.Matrix3x3
import app.algebra.linear.vectors.vector3.Vector1x3
import app.geometry.Direction
import app.geometry.Point
import app.geometry.RawVector
import app.geometry.times

@Suppress("DataClassPrivateConstructor")
data class Translation private constructor(
    val tv: RawVector,
) : Transformation() {
    companion object {
        fun of(
            tv: RawVector,
        ): Translation = Translation(
            tv = tv,
        )

        fun inDirection(
            direction: Direction,
            distance: Double,
        ): Translation {
            require(distance.isFinite())
            val dv = direction.dv
            return Translation(
                tv = distance * dv,
            )
        }

        fun of(
            tx: Double,
            ty: Double,
        ): Translation = of(
            tv = RawVector(
                x = tx,
                y = ty,
            ),
        )
    }

    override fun transform(
        point: Point,
    ): Point = translate(point = point)

    override val transformationMatrix: Matrix3x3
        get() = Matrix3x3.rowMajor(
            row0 = Vector1x3.of(1.0, 0.0, tv.x),
            row1 = Vector1x3.of(0.0, 1.0, tv.y),
            row2 = Vector1x3.of(0.0, 0.0, 1.0),
        )

    fun translate(point: Point): Point = Point.of(
        pv = point.pv + tv,
    )

    fun scale(
        s: Double,
    ): Translation {
        require(s.isFinite())
        return Translation(tv = s * tv)
    }

    fun extend(
        deltaLength: Double,
    ): Translation = Translation(
        tv = (tv.length + deltaLength) / tv.length * tv,
    )
}
