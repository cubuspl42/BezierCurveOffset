package app

import app.algebra.bezier_formulas.RealFunction.SamplingStrategy
import app.geometry.*
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
    val baseSpline = ClosedPolyBezierCurve(
        innerNodes = listOf(
            BezierSpline.InnerNode(
                backwardControl = Point(300.0, 400.0),
                point = Point(400.0, 400.0),
                forwardControl = Point(500.0, 400.0),
            ),
            BezierSpline.InnerNode(
                backwardControl =  Point(450.0, 650.0),
                point = Point(400.0, 700.0),
                forwardControl = Point(350.0, 750.0),
            ),
            BezierSpline.InnerNode(
                backwardControl =  Point(300.0, 750.0),
                point = Point(250.0, 700.0),
                forwardControl = Point(200.0, 650.0),
            ),
        ),
    )

    val offset = 30.0
//    val offsetPointSeries = baseCurve.findOffsetTimedSeries(offset = offset)
//
//    val offsetCurveNormal = baseCurve.findOffsetCurveNormal(offset = offset)
//    val offsetCurveBestFit = baseCurve.findOffsetCurveBestFit(offset = offset).offsetCurve

    val offsetSplineBestFit = baseSpline.findOffsetSplineBestFit(offset = offset)
//    val offsetSplineNormal = baseSpline.findOffsetSplineNormal(offset = offset)

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

//    offsetSplineNormal.drawSpline(
//        graphics2D = svgGraphics2D,
//        color = Color.BLUE,
//    )

    offsetSplineBestFit.drawSpline(
        graphics2D = svgGraphics2D,
        color = Color.RED,
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
