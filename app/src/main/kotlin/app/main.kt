package app

import app.algebra.bezier_binomials.RealFunction.SamplingStrategy
import app.geometry.*
import app.geometry.bezier_curves.ProperBezierCurve
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
                backwardControl = Point(250.0, 400.0),
                knotPoint = Point(400.0, 400.0),
                forwardControl = Point(500.0, 400.0),
            ),
            BezierSpline.InnerNode(
                backwardControl = Point(450.0, 650.0),
                knotPoint = Point(400.0, 700.0),
                forwardControl = Point(350.0, 750.0),
            ),
            BezierSpline.InnerNode(
                backwardControl = Point(300.0, 750.0),
                knotPoint = Point(250.0, 700.0),
                forwardControl = Point(200.0, 650.0),
            ),
        ),
    )

    val subCurve = baseSpline.subCurves[1]

    val subCurveOffsetSpline = subCurve.findOffsetSpline(
        strategy = ProperBezierCurve.BestFitOffsetStrategy,
        offset = 30.0,
    )

//    val baseSpline = OpenPolyBezierCurve(
//        startNode = BezierSpline.StartNode(
//            knotPoint = Point(400.0, 700.0),
//            forwardControl = Point(350.0, 750.0),
//        ),
//        innerNodes = emptyList(),
//        endNode = BezierSpline.EndNode(
//            backwardControl = Point(300.0, 750.0),
//            knotPoint = Point(250.0, 700.0),
//        ),
//    )

    val offset = 30.0
//    val offsetPointSeries = baseCurve.findOffsetTimedSeries(offset = offset)
//
//    val offsetCurveNormal = baseCurve.findOffsetCurveNormal(offset = offset)
//    val offsetCurveBestFit = baseCurve.findOffsetCurveBestFit(offset = offset).offsetCurve

    val contourSplineBestFit = baseSpline.findContourSpline(
        ProperBezierCurve.BestFitOffsetStrategy,
        offset = offset,
    )!!.contourSpline

//    val offsetSplineNormal = baseSpline.findOffsetSplineNormal(offset = offset)
//    val splitSpline = baseCurve.splitAtCriticalPoints()

    val width = 1024
    val height = 768

    val svgGraphics2D = SVGGraphics2D(width.toDouble(), height.toDouble())

    baseSpline.drawSpline(
        graphics2D = svgGraphics2D,
    )

    svgGraphics2D.stroke = BasicStroke(1.0f)

    contourSplineBestFit.drawSpline(
        graphics2D = svgGraphics2D,
        color = Color.RED,
    )

    svgGraphics2D.color = Color.ORANGE
    svgGraphics2D.draw(
        subCurve.asLongitudinal!!.toPath2D(),
    )

//    subCurveOffsetSpline.drawSpline(
//        graphics2D = svgGraphics2D,
//        color = Color.GREEN,
//    )

    val file = File("Bezier.svg")
    SVGUtils.writeToSVG(file, svgGraphics2D.svgElement)
}
