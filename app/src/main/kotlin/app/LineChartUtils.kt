package app

import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.NumberAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.data.xy.XYDataset
import org.jfree.svg.SVGGraphics2D
import org.jfree.svg.SVGUtils
import java.awt.Color
import java.awt.Rectangle
import java.io.File

object LineChartUtils {
    private fun createChart(
        name: String,
        dataset: XYDataset,
    ): JFreeChart = ChartFactory.createXYLineChart(
        name, "t", "X/Y", dataset, PlotOrientation.VERTICAL, true, true, true
    ).apply {
        backgroundPaint = Color.white
        xyPlot.apply {
            backgroundPaint = Color.lightGray
            domainGridlinePaint = Color.white
            rangeGridlinePaint = Color.white

            renderer = XYLineAndShapeRenderer().apply {
                defaultShapesVisible = false
            }

            rangeAxis.apply {
                standardTickUnits = NumberAxis.createIntegerTickUnits()
            }
        }
    }

    fun writeToFile(
        name: String,
        width: Int,
        height: Int,
        dataset: XYDataset,
    ) {
        val chart = createChart(
            name = name, dataset = dataset
        )

        val graphics2D = SVGGraphics2D(width.toDouble(), height.toDouble())
        val rect = Rectangle(0, 0, width, height)
        chart.draw(graphics2D, rect)

        val file = File("$name.svg")
        SVGUtils.writeToSVG(file, graphics2D.svgElement)
    }
}
