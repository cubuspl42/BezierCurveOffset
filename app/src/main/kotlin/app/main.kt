package app

import app.algebra.bezier_formulas.RealFunction.SamplingStrategy
import app.geometry.Point
import app.geometry.Translation
import app.geometry.bezier_curves.CubicBezierCurve
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
    val baseCurve = CubicBezierCurve(
        start = Point(18.0, 81.0),
        control0 = Point(226.0, 26.0),
        control1 = Point(70.0, 259.0),
        end = Point(8.0, 181.0),
    ).translate(
        translation = Translation(
            tx = 400.0,
            ty = 200.0,
        ),
    )

    val offset = 20.0
    val offsetPointSeries = baseCurve.findOffsetPolyline(offset = offset)

    val offsetCurveNormal = baseCurve.findOffsetCurveNormal(offset = offset)
    val offsetCurveBestFit = baseCurve.findOffsetCurveBestFit(offset = offset)

    val (splitCurveA, splitCurveB) = baseCurve.split(t = 0.4)

    val width = 1024
    val height = 768

    val svgGraphics2D = SVGGraphics2D(width.toDouble(), height.toDouble())

    baseCurve.draw(
        graphics2D = svgGraphics2D,
        innerColor = Color.BLACK,
        outerColor = Color.LIGHT_GRAY,
        outerSamplingStrategy = outerSamplingStrategy,
    )

    splitCurveA.draw(
        graphics2D = svgGraphics2D,
        innerColor = Color.MAGENTA,
        outerColor = Color.LIGHT_GRAY,
        outerSamplingStrategy = outerSamplingStrategy,
    )

    splitCurveB.draw(
        graphics2D = svgGraphics2D,
        innerColor = Color.CYAN,
        outerColor = Color.LIGHT_GRAY,
        outerSamplingStrategy = outerSamplingStrategy,
    )

    svgGraphics2D.stroke = BasicStroke(1.0f)

    offsetPointSeries.draw(
        graphics2D = svgGraphics2D,
        color = Color.ORANGE,
    )

    offsetCurveBestFit.draw(
        graphics2D = svgGraphics2D,
        innerColor = Color.RED,
        outerSamplingStrategy = outerSamplingStrategy,
    )

//    offsetCurveNormal.draw(
//        graphics2D = svgGraphics2D,
//        innerColor = Color.BLUE,
//        outerSamplingStrategy = outerSamplingStrategy,
//    )

    val file = File("Bezier.svg")
    SVGUtils.writeToSVG(file, svgGraphics2D.svgElement)
}
