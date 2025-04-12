package app.geometry.transformations

import app.algebra.linear.matrices.matrix3.Matrix3x3
import app.algebra.linear.vectors.vector2.Vector2
import app.algebra.linear.vectors.vector2.Vector2x1
import app.algebra.linear.vectors.vector2.plus
import app.algebra.linear.vectors.vector2.times
import app.algebra.linear.vectors.vector3.Vector1x3
import app.geometry.BiDirection
import app.geometry.Direction
import app.geometry.Point
import app.geometry.RawVector

@Suppress("DataClassPrivateConstructor")
data class Translation private constructor(
    val tv: Vector2<*>,
) : Transformation() {
    companion object {
        fun of(
            tv: Vector2<*>,
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
            tv = Vector2x1.of(
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

    fun projectOnto(
        biDirection: BiDirection,
    ): Translation {
        val dv = biDirection.dv
        return Translation(
            tv = (tv.dotForced(dv) / dv.lengthSquared) * dv
        )
    }
}
