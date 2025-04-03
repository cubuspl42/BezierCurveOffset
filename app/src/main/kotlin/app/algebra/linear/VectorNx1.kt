package app.algebra.linear

import app.fillByRow
import org.ujmp.core.Matrix

data class VectorNx1(
    override val xs: List<Double>,
) : VectorN() {
    fun dot(
        other: Vector1xN,
    ): Double = dotForced(other)

    fun toUjmpMatrix(): Matrix = Matrix.Factory.fillByRow(
        rowElements = xs,
        rowWidth = 1,
        buildRow = { doubleArrayOf(it) },
    )
}
