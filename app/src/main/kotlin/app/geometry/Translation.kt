package app.geometry

import app.Vector

data class Translation(
    val t: Vector,
) {
    constructor(
        tx: Double,
        ty: Double,
    ) : this(
        t = Vector(
            x = tx,
            y = ty,
        ),
    )
}
