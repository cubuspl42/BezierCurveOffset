package app

import app.algebra.bezier_formulas.*
import app.algebra.bezier_formulas.RealFunction.SamplingStrategy
import app.geometry.Point
import app.geometry.Translation
import app.geometry.bezier_curves.BezierCurve
import app.geometry.bezier_curves.CubicBezierCurve
import org.jfree.svg.SVGGraphics2D
import org.jfree.svg.SVGUtils
import java.awt.Color
import java.io.File

val outerSamplingStrategy = SamplingStrategy(
    x0 = -2.0,
    x1 = 2.0,
    xInterval = 0.01,
)

fun writeBezierCurveToFile(
    bezierCurve: BezierCurve,
    name: String,
) {
    val width = 1024
    val height = 768

    val svgGraphics2D = SVGGraphics2D(width.toDouble(), height.toDouble())

    bezierCurve.draw(
        graphics2D = svgGraphics2D,
        innerColor = Color.BLACK,
        outerColor = Color.LIGHT_GRAY,
        outerSamplingStrategy = outerSamplingStrategy,
    )

    val localExtremitySet = bezierCurve.basisFormula.findCriticalPoints()
    println(localExtremitySet)

    val file = File("$name.svg")
    SVGUtils.writeToSVG(file, svgGraphics2D.svgElement)

    LineChartUtils.writeToFile(
        name = "Bezier",
        width = width,
        height = height,
        dataset = bezierCurve.basisFormula.toDataset(
            samplingStrategy = outerSamplingStrategy,
        ),
    )
}

fun main(args: Array<String>) {
    val bezierCurve1 = CubicBezierCurve(
        start = Point(18.0, 81.0),
        control0 = Point(226.0, 26.0),
        control1 = Point(70.0, 259.0),
        end = Point(8.0, 181.0),
    ).translate(
        translationVector = Translation(
            tx = 200.0,
            ty = 200.0,
        ),
    )

    writeBezierCurveToFile(
        bezierCurve = bezierCurve1,
        name = "Bezier1",
    )

    val bezierCurve2 = CubicBezierCurve(
        start = Point(0.0, 100.0),
        control0 = Point(100.0, 0.0),
        control1 = Point(100.0, 100.0),
        end = Point(200.0, 0.0),
    ).translate(
        translationVector = Translation(
            tx = 200.0,
            ty = 200.0,
        ),
    )

    writeBezierCurveToFile(
        bezierCurve = bezierCurve2,
        name = "Bezier2",
    )
}
