package app.geometry

import app.algebra.Vector

data class Translation(
    val tv: Vector,
) {
    constructor(
        tx: Double,
        ty: Double,
    ) : this(
        tv = Vector(
            x = tx,
            y = ty,
        ),
    )

    fun projectOnto(
        direction: Direction,
    ): Translation = Translation(
        tv = tv.projectOnto(direction.dv),
    )
}
