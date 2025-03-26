package app

import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.algebra.bezier_binomials.findInterestingCriticalPoints
import app.geometry.*
import app.geometry.bezier_curves.CubicBezierCurve
import app.geometry.bezier_splines.*
import org.jfree.svg.SVGGraphics2D
import org.jfree.svg.SVGUtils
import java.awt.BasicStroke
import java.awt.Color
import java.io.File

val outerSamplingStrategy = SamplingStrategy(
    x0 = -2.0,
    x1 = 2.0,
    xInterval = 0.01,
)

fun main(args: Array<String>) {
    val bezierCurve = CubicBezierCurve.of(
        start = Point.of(300.0, 100.0),
        control0 = Point.of(200.0, 100.0),
        control1 = Point.of(400.0, 100.0),
        end = Point.of(300.0, 100.0),
    )

    val splitSpline = bezierCurve.splitAtMultiple(
        bezierCurve.basisFormula.findInterestingCriticalPoints().criticalPointsXY,
    )!!

    val width = 1024
    val height = 768
    val svgGraphics2D = SVGGraphics2D(width.toDouble(), height.toDouble())

    svgGraphics2D.stroke = BasicStroke(1.0f)

    splitSpline.drawSpline(
        graphics2D = svgGraphics2D,
        color = Color.RED,
    )

    val file = File("Bezier.svg")
    SVGUtils.writeToSVG(file, svgGraphics2D.svgElement)
}
