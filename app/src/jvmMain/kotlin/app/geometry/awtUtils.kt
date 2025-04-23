package app.geometry

import java.awt.geom.Rectangle2D

fun BoundingBox.toRect2D(): Rectangle2D.Double = Rectangle2D.Double(
    topLeft.x,
    topLeft.y,
    width,
    height,
)
