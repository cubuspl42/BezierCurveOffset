package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.bezier_binomials.RealFunction
import app.geometry.RawVector

data class ParametricPolynomial(
    val xFunction: Polynomial,
    val yFunction: Polynomial,
) : RealFunction<RawVector>(), NumericObject {
    data class SolutionSet(
        val xSolutions: Set<Double>,
        val ySolutions: Set<Double>,
    )

    data class RootSet(
        val xRoots: Set<Double>,
        val yRoots: Set<Double>,
    ) {
        fun filter(
            predicate: (Double) -> Boolean,
        ): RootSet = RootSet(
            xRoots = xRoots.filter(predicate).toSet(),
            yRoots = yRoots.filter(predicate).toSet(),
        )

        val allRoots: Set<Double>
            get() = xRoots + yRoots
    }

    companion object {
        fun cubic(
            a: RawVector,
            b: RawVector,
            c: RawVector,
            d: RawVector,
        ): ParametricPolynomial = ParametricPolynomial(
            xFunction = CubicPolynomial.of(
                a = a.x,
                b = b.x,
                c = c.x,
                d = d.x,
            ),
            yFunction = CubicPolynomial.of(
                a = a.y,
                b = b.y,
                c = c.y,
                d = d.y,
            ),
        )

        fun quadratic(
            a: RawVector,
            b: RawVector,
            c: RawVector,
        ): ParametricPolynomial = ParametricPolynomial(
            xFunction = QuadraticPolynomial.of(
                a = a.x,
                b = b.x,
                c = c.x,
            ),
            yFunction = QuadraticPolynomial.of(
                a = a.y,
                b = b.y,
                c = c.y,
            ),
        )

        fun linear(
            a: RawVector,
            b: RawVector,
        ): ParametricPolynomial = ParametricPolynomial(
            xFunction = LinearPolynomial.of(
                a = a.x,
                b = b.x,
            ),
            yFunction = LinearPolynomial.of(
                a = a.y,
                b = b.y,
            ),
        )

        fun constant(
            a: RawVector,
        ): ParametricPolynomial = ParametricPolynomial(
            xFunction = ConstantPolynomial.of(
                a = a.x,
            ),
            yFunction = ConstantPolynomial.of(
                a = a.y,
            ),
        )
    }

    fun findRoots(): RootSet = RootSet(
        xRoots = xFunction.findRoots(),
        yRoots = yFunction.findRoots(),
    )

    override fun apply(x: Double): RawVector = RawVector(
        x = xFunction.apply(x),
        y = yFunction.apply(x),
    )

    override fun equalsWithTolerance(
        other: NumericObject, absoluteTolerance: Double
    ): Boolean = when {
        other !is ParametricPolynomial -> false
        xFunction != other.xFunction -> false
        yFunction != other.yFunction -> false
        else -> true
    }
}
