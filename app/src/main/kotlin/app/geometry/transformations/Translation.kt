package app.geometry.transformations

import app.algebra.linear.Vector2
import app.algebra.linear.Vector2x1
import app.algebra.linear.plus
import app.algebra.linear.times
import app.geometry.Direction
import app.geometry.Point

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
                tv = dv.scale(distance / dv.length),
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

    fun translate(point: Point): Point = Point.of(
        pv = point.pv + tv,
    )

    fun projectOnto(
        direction: Direction,
    ): Translation {
        val dv = direction.dv
        return Translation(
            tv = (tv.dotForced(dv) / dv.lengthSquared) * dv
        )
    }
}
