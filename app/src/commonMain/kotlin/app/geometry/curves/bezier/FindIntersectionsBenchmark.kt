package app.geometry.curves.bezier

import app.geometry.Point
import app.geometry.curves.LineSegment
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

@Suppress("FunctionName")
@State(Scope.Benchmark)
class FindIntersectionsBenchmark {
    private val lineSegment = LineSegment.of(
        start = Point.of(
            px = 29.878999710083008,
            py = 114.9489974975586,
        ),
        end = Point.of(
            px = 220.9720001220703,
            py = 161.66400146484375,
        ),
    )

    private val bezierCurve0 = CubicBezierCurve.of(
        start = Point.of(
            px = 302.8609924316406,
            py = 399.8139953613281,
        ),
        control0 = Point.of(
            px = 1097.5999755859375,
            py = 163.89300537109375,
        ),
        control1 = Point.of(
            px = -95.947998046875,
            py = 163.44400024414062,
        ),
        end = Point.of(
            px = 700.47900390625,
            py = 400.9320068359375,
        ),
    )

    private val bezierCurve1 = CubicBezierCurve.of(
        start = Point.of(
            px = 401.6960144042969,
            py = 102.31400299072266,
        ),
        control0 = Point.of(
            px = 525.4130249023438,
            py = 763.280029296875,
        ),
        control1 = Point.of(
            px = 471.4840087890625,
            py = -143.9980010986328,
        ),
        end = Point.of(
            px = 598.4459838867188,
            py = 398.2959899902344,
        ),
    )

    @Setup
    fun prepare() {
        bezierCurve0.basisFormula
        lineSegment.basisFormula
    }

    @Benchmark
    fun benchmarkFindIntersections_lineSegment(): Set<Point> = CubicBezierCurve.findIntersections(
        lineSegment = lineSegment,
        bezierCurve = bezierCurve0,
    )

    // 444.939 ±(99.9%) 26.834 ops/s [Average] (initial implementation, no solving optimizations)
    @Benchmark
    fun benchmarkFindIntersections_bezierCurve(): Set<Point> = CubicBezierCurve.findIntersections(
        bezierCurve0 = bezierCurve0,
        bezierCurve1 = bezierCurve1,
    )

    // 944.548 ±(99.9%) 13.074 ops/s [Average] (initial implementation)
    @Benchmark
    fun benchmarkFindIntersections_bezierCurve_bb(): Set<Point> = CubicBezierCurve.findIntersectionsBb(
        bezierCurve0 = bezierCurve0,
        bezierCurve1 = bezierCurve1,
    )
}
