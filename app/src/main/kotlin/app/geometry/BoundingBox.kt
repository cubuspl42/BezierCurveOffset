package app.geometry

import app.SvgViewBox
import app.geometry.transformations.Translation
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
        fun of(
            pointA: Point,
            pointB: Point,
        ): BoundingBox {
            val xMin = minOf(pointA.x, pointB.x)
            val xMax = maxOf(pointA.x, pointB.x)
            val yMin = minOf(pointA.y, pointB.y)
            val yMax = maxOf(pointA.y, pointB.y)

            return of(
                xMin = xMin,
                xMax = xMax,
                yMin = yMin,
                yMax = yMax,
            )
        }

        fun of(
            xMin: Double,
            xMax: Double,
            yMin: Double,
            yMax: Double,
        ): BoundingBox {
            require(xMin <= xMax)
            require(yMin <= yMax)

            return BoundingBox(
                topLeft = Point.of(px = xMin, py = yMin),
                width = xMax - xMin,
                height = yMax - yMin,
            )
        }

        fun unionAll(
            boundingBoxes: List<BoundingBox>,
        ): BoundingBox {
            require(boundingBoxes.isNotEmpty())

            val xMin = boundingBoxes.minOf { it.xMin }
            val xMax = boundingBoxes.maxOf { it.xMax }
            val yMin = boundingBoxes.minOf { it.yMin }
            val yMax = boundingBoxes.maxOf { it.yMax }

            return of(
                xMin = xMin,
                xMax = xMax,
                yMin = yMin,
                yMax = yMax,
            )
        }
    }

    val bottomLeft: Point
        get() = topLeft.translateVia(Translation.of(width, height))

    val xMin: Double
        get() = topLeft.x

    val xMax: Double
        get() = bottomLeft.x

    val yMin: Double
        get() = topLeft.y

    val yMax: Double
        get() = bottomLeft.y

    fun unionWith(
        other: BoundingBox,
    ): BoundingBox {
        val xMin = minOf(xMin, other.xMin)
        val xMax = maxOf(xMax, other.xMax)
        val yMin = minOf(yMin, other.yMin)
        val yMax = maxOf(yMax, other.yMax)

        return BoundingBox.of(
            xMin = xMin,
            xMax = xMax,
            yMin = yMin,
            yMax = yMax,
        )
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

    fun toSvgViewBox(): SvgViewBox = SvgViewBox(
        xMin = xMin,
        yMin = yMin,
        width = width,
        height = height,
    )
}
