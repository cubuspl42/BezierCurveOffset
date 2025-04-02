package app.geometry

import app.algebra.linear.Vector

@Suppress("DataClassPrivateConstructor")
data class Translation private constructor(
    val tv: Vector,
) {
    companion object {
        fun of(
            tv: Vector,
        ): Translation = Translation(
            tv = tv,
        )

        fun of(
            tx: Double,
            ty: Double,
        ): Translation = of(
            tv = Vector(
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
