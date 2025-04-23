package app.geometry.curves.bezier

import app.geometry.Point
import app.geometry.curves.LineSegment
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

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

    private val bezierCurve = CubicBezierCurve.of(
        start = Point.of(
            px = 31.933000564575195,
            py = 143.03799438476562,
        ),
        control0 = Point.of(
            px = 124.30000305175781,
            py = 43.27899932861328,
        ),
        control1 = Point.of(
            px = 124.23699951171875,
            py = 225.48199462890625,
        ),
        end = Point.of(
            px = 207.697998046875,
            py = 140.64100646972656,
        ),
    )

    @Setup
    fun prepare() {
        bezierCurve.basisFormula
        lineSegment.basisFormula
    }

    @Benchmark
    fun benchmarkFindIntersections(): Set<Point> = CubicBezierCurve.findIntersections(
        lineSegment = lineSegment,
        bezierCurve = bezierCurve,
    )
}
