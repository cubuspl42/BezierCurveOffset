package app.geometry

import java.awt.geom.Rectangle2D

/**
 * A bounding box described by its diagonal from a to b
 */
data class BoundingBox(
    val topLeft: Point,
    val width: Double,
    val height: Double,
) {
    companion object {
        fun fromExtrema(
            xMin: Double,
            xMax: Double,
            yMin: Double,
            yMax: Double,
        ): BoundingBox {
            require(xMin <= xMax)
            require(yMin <= yMax)

            return BoundingBox(
                topLeft = Point(px = xMin, py = yMin),
                width = xMax - xMin,
                height = yMax - yMin,
            )
        }
    }

    init {
        require(width >= 0)
        require(height >= 0)
    }

    fun toRect2D(): Rectangle2D.Double = Rectangle2D.Double(
        topLeft.x,
        topLeft.y,
        width,
        height,
    )
}
