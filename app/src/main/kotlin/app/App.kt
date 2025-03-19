package app

import app.geometry.Point
import app.geometry.bezier_curves.CubicBezierCurve
import org.jfree.svg.SVGGraphics2D
import org.jfree.svg.SVGUtils
import java.awt.Color
import java.io.File

fun main(args: Array<String>) {
    val svgGraphics2D = SVGGraphics2D(600.0, 400.0)

    val bezierCurve = CubicBezierCurve(
        start = Point(100.0, 100.0),
        control0 = Point(200.0, 100.0),
        control1 = Point(300.0, 101.0),
        end = Point(400.0, 100.0),
    )

    val offset = 20.0
    val offsetBezierCurve1 = bezierCurve.moveByOffset(offset)
    val offsetBezierCurve2 = bezierCurve.moveByOffset(-offset)

    // Draw the curve
    svgGraphics2D.draw(bezierCurve.toPath2D())

    // Draw the offset curve (red)
    svgGraphics2D.color = Color.RED
    svgGraphics2D.draw(offsetBezierCurve1.toPath2D())

    // Draw the offset curve (blue)
    svgGraphics2D.color = Color.BLUE
    svgGraphics2D.draw(offsetBezierCurve2.toPath2D())

    // Save the SVG to a file
    val file = File("CurveDemo.svg")
    SVGUtils.writeToSVG(file, svgGraphics2D.svgElement)
}
