package app.algebra.polynomials

import app.algebra.NumericObject
import app.algebra.NumericObject.Tolerance
import app.algebra.euclidean.bezier_binomials.RealFunction
import app.geometry.RawVector

data class ParametricPolynomial(
    val xFunction: Polynomial<*>,
    val yFunction: Polynomial<*>,
) : RealFunction<RawVector>, NumericObject {
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
            a3: RawVector,
            a2: RawVector,
            a1: RawVector,
            a0: RawVector,
        ): ParametricPolynomial = ParametricPolynomial(
            xFunction = Polynomial.cubic(
                a3 = a3.x,
                a2 = a2.x,
                a1 = a1.x,
                a0 = a0.x,
            ),
            yFunction = Polynomial.cubic(
                a3 = a3.y,
                a2 = a2.y,
                a1 = a1.y,
                a0 = a0.y,
            ),
        )

        fun quadratic(
            a: RawVector,
            b: RawVector,
            c: RawVector,
        ): ParametricPolynomial = ParametricPolynomial(
            xFunction = Polynomial.quadratic(
                a2 = a.x,
                a1 = b.x,
                a0 = c.x,
            ),
            yFunction = Polynomial.quadratic(
                a2 = a.y,
                a1 = b.y,
                a0 = c.y,
            ),
        )

        fun linear(
            a1: RawVector,
            a0: RawVector,
        ): ParametricPolynomial = ParametricPolynomial(
            xFunction = Polynomial.linear(
                a1 = a1.x,
                a0 = a0.x,
            ),
            yFunction = Polynomial.linear(
                a1 = a1.y,
                a0 = a0.y,
            ),
        )

        fun constant(
            a: RawVector,
        ): ParametricPolynomial = ParametricPolynomial(
            xFunction = Polynomial.constant(
                a0 = a.x,
            ),
            yFunction = Polynomial.constant(
                a0 = a.y,
            ),
        )
    }

    fun findRoots(): RootSet = RootSet(
        xRoots = xFunction.findRoots().toSet(),
        yRoots = yFunction.findRoots().toSet(),
    )

    override fun apply(x: Double): RawVector = RawVector(
        x = xFunction.apply(x),
        y = yFunction.apply(x),
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: Tolerance,
    ): Boolean = when {
        other !is ParametricPolynomial -> false
        xFunction != other.xFunction -> false
        yFunction != other.yFunction -> false
        else -> true
    }
}
