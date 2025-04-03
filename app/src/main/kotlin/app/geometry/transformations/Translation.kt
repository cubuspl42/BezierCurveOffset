package app.geometry.transformations

import app.algebra.linear.Vector2
import app.geometry.Direction
import app.geometry.Point

@Suppress("DataClassPrivateConstructor")
data class Translation private constructor(
    val tv: Vector2,
) : Transformation() {
    companion object {
        fun of(
            tv: Vector2,
        ): Translation = Translation(
            tv = tv,
        )

        fun of(
            tx: Double,
            ty: Double,
        ): Translation = of(
            tv = Vector2(
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
    ): Translation = Translation(
        tv = tv.projectOnto(direction.dv),
    )
}
