package app.geometry

import app.algebra.linear.Vector2

@Suppress("DataClassPrivateConstructor")
data class Translation private constructor(
    val tv: Vector2,
) {
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

    fun projectOnto(
        direction: Direction,
    ): Translation = Translation(
        tv = tv.projectOnto(direction.dv),
    )
}
