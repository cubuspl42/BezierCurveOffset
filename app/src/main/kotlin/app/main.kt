package app

import app.algebra.bezier_formulas.RealFunction.SamplingStrategy
import app.algebra.bezier_formulas.findAllCriticalPoints
import app.geometry.*
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

    val p0 =  Point(318.0, 181.0)

    val t0 = Translation(400.0, -100.0)
    val t1 = Translation(-20.0, 200.0)
    val t2 = Translation(-120.0, 150.0)
    val t3 = t2
    val t4 = Translation(-160.0, 0.0)
    val t5 = Translation(-140.0, 30.0)

    val w0 = p0
    val w1 = p0.translate(t0)
    val w2 = w1.translate(t1)
    val w3 = w2.translate(t2)
    val w4 = w3.translate(t3)
    val w5 = w4.translate(t4)
    val w6 = w5.translate(t5)

    val baseSpline = PolyCubicBezierCurve(
        nodes = listOf(
            CubicBezierSpline.Node.start(
                point = w0,
                control1 = w1,
            ),
            CubicBezierSpline.Node(
                control0 = w2,
                point = w3,
                control1 = w4,
            ),
            CubicBezierSpline.Node.end(
                control0 = w5,
                point = w6,
            ),
        ),
    )

    val offset = 30.0
//    val offsetPointSeries = baseCurve.findOffsetTimedSeries(offset = offset)
//
//    val offsetCurveNormal = baseCurve.findOffsetCurveNormal(offset = offset)
//    val offsetCurveBestFit = baseCurve.findOffsetCurveBestFit(offset = offset).offsetCurve

    val offsetSplineBestFit = baseSpline.findOffsetSplineBestFitPoly(offset = offset)

//    val criticalPoints = baseCurve.basisFormula.findAllCriticalPoints().criticalPoints
    val criticalPoints = setOf(
        0.3734586167618398,
        0.854097765727581,
//        0.10750490539094985,
    )
    println(criticalPoints)

//    val splitSpline = baseCurve.splitAtCriticalPoints()

    val width = 1024
    val height = 768

    val svgGraphics2D = SVGGraphics2D(width.toDouble(), height.toDouble())

    baseSpline.drawSpline(
        graphics2D = svgGraphics2D,
//        innerColor = Color.BLACK,
//        outerColor = Color.LIGHT_GRAY,
//        outerSamplingStrategy = outerSamplingStrategy,
    )

//    splitSpline.drawSpline(
//        graphics2D = svgGraphics2D,
//    )

    svgGraphics2D.stroke = BasicStroke(1.0f)

//    offsetPointSeries.draw(
//        graphics2D = svgGraphics2D,
//        color = Color.ORANGE,
//    )

    offsetSplineBestFit.drawSpline(
        graphics2D = svgGraphics2D,
    )

//    offsetCurveBestFit.draw(
//        graphics2D = svgGraphics2D,
//        innerColor = Color.RED,
//        outerSamplingStrategy = outerSamplingStrategy,
//    )

//    offsetCurveNormal.draw(
//        graphics2D = svgGraphics2D,
//        innerColor = Color.BLUE,
//        outerSamplingStrategy = outerSamplingStrategy,
//    )

    val file = File("Bezier.svg")
    SVGUtils.writeToSVG(file, svgGraphics2D.svgElement)
}
